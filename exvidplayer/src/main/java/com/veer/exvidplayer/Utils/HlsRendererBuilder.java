package com.veer.exvidplayer.Utils;

import android.content.Context;
import android.media.MediaCodec;
import android.os.Handler;
import com.google.android.exoplayer.DefaultLoadControl;
import com.google.android.exoplayer.LoadControl;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.MediaCodecSelector;
import com.google.android.exoplayer.MediaCodecVideoTrackRenderer;
import com.google.android.exoplayer.TrackRenderer;
import com.google.android.exoplayer.hls.DefaultHlsTrackSelector;
import com.google.android.exoplayer.hls.HlsChunkSource;
import com.google.android.exoplayer.hls.HlsMasterPlaylist;
import com.google.android.exoplayer.hls.HlsPlaylist;
import com.google.android.exoplayer.hls.HlsPlaylistParser;
import com.google.android.exoplayer.hls.HlsSampleSource;
import com.google.android.exoplayer.hls.PtsTimestampAdjusterProvider;
import com.google.android.exoplayer.upstream.DataSource;
import com.google.android.exoplayer.upstream.DefaultAllocator;
import com.google.android.exoplayer.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer.upstream.DefaultUriDataSource;
import com.google.android.exoplayer.util.ManifestFetcher;
import com.veer.exvidplayer.ExVidPlayer;
import java.io.IOException;

/**
 * Created by Brajendr on 1/26/2017.
 */

public class HlsRendererBuilder implements RendererBuilder {
  private static final int BUFFER_SEGMENT_SIZE = 64 * 1024;
  private static final int MAIN_BUFFER_SEGMENTS = 254;
  private static final int AUDIO_BUFFER_SEGMENTS = 54;
  private static final int TEXT_BUFFER_SEGMENTS = 2;
  private final Context context;
  private final String userAgent;
  private final String url;
  private AsyncRendererBuilder currentAsyncBuilder;

  public HlsRendererBuilder(Context context, String userAgent, String video_url) {
    this.context = context;
    this.userAgent = userAgent;
    this.url = video_url;
  }
  @Override public void buildRenderers(ExVidPlayer player) {
    currentAsyncBuilder = new AsyncRendererBuilder(context, userAgent, url, player);
    currentAsyncBuilder.init();
  }
  @Override public void cancel() {
    if (currentAsyncBuilder != null) {
      currentAsyncBuilder.cancel();
      currentAsyncBuilder = null;
    }
  }

  private class AsyncRendererBuilder implements ManifestFetcher.ManifestCallback<HlsPlaylist>{
    private final Context context;
    private final String userAgent;
    private final String url;
    private final ExVidPlayer player;
    private final ManifestFetcher<HlsPlaylist> playlistFetcher;
    private boolean canceled;
    public AsyncRendererBuilder(Context context, String userAgent, String url, ExVidPlayer player) {
      this.context = context;
      this.userAgent = userAgent;
      this.url = url;
      this.player = player;
      HlsPlaylistParser parser = new HlsPlaylistParser();
      playlistFetcher = new ManifestFetcher<>(url, new DefaultUriDataSource(context, userAgent),
          parser);
    }
    public void init() {
      playlistFetcher.singleLoad(player.getMainHandler().getLooper(), this);
    }
    public void cancel() {
      canceled = true;
    }
    @Override
    public void onSingleManifestError(IOException e) {
      if (canceled) {
        return;
      }

      player.onRenderersError(e);
    }
    @Override
    public void onSingleManifest(HlsPlaylist manifest) {
      if (canceled) {
        return;
      }
      Handler mainHandler = player.getMainHandler();
      LoadControl loadControl = new DefaultLoadControl(new DefaultAllocator(BUFFER_SEGMENT_SIZE));
      DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
      PtsTimestampAdjusterProvider
          timestampAdjusterProvider = new PtsTimestampAdjusterProvider();

      boolean haveSubtitles = false;
      boolean haveAudios = false;
      if (manifest instanceof HlsMasterPlaylist) {
        HlsMasterPlaylist masterPlaylist = (HlsMasterPlaylist) manifest;
        haveSubtitles = !masterPlaylist.subtitles.isEmpty();

      }
      // Build the video/id3 renderers.
      DataSource dataSource = new DefaultUriDataSource(context, bandwidthMeter, userAgent);
      HlsChunkSource
          chunkSource = new HlsChunkSource(true /* isMaster */, dataSource, manifest,
          DefaultHlsTrackSelector.newDefaultInstance(context), bandwidthMeter,
          timestampAdjusterProvider, HlsChunkSource.ADAPTIVE_MODE_SPLICE);
      HlsSampleSource sampleSource = new HlsSampleSource(chunkSource, loadControl,
          MAIN_BUFFER_SEGMENTS * BUFFER_SEGMENT_SIZE, mainHandler, player, ExVidPlayer.TYPE_VIDEO);
      MediaCodecVideoTrackRenderer
          videoRenderer = new MediaCodecVideoTrackRenderer(context, sampleSource,
          MediaCodecSelector.DEFAULT, MediaCodec.VIDEO_SCALING_MODE_SCALE_TO_FIT);
      MediaCodecAudioTrackRenderer audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource,
          MediaCodecSelector.DEFAULT);

      TrackRenderer[] renderers = new TrackRenderer[2];
      renderers[0] = videoRenderer;
      renderers[1] = audioRenderer;
      //renderers[2] = textRenderer;
      player.onRenderers(renderers, bandwidthMeter);

    }
  }
}
