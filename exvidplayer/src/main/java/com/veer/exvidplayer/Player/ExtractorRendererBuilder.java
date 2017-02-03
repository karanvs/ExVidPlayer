package com.veer.exvidplayer.Player;

import android.content.Context;
import android.media.MediaCodec;
import android.net.Uri;
import android.os.Handler;
import android.view.Surface;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.MediaCodecSelector;
import com.google.android.exoplayer.MediaCodecTrackRenderer;
import com.google.android.exoplayer.MediaCodecVideoTrackRenderer;
import com.google.android.exoplayer.TrackRenderer;
import com.google.android.exoplayer.extractor.ExtractorSampleSource;
import com.google.android.exoplayer.upstream.Allocator;
import com.google.android.exoplayer.upstream.DataSource;
import com.google.android.exoplayer.upstream.DefaultAllocator;
import com.google.android.exoplayer.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer.upstream.DefaultUriDataSource;

public class ExtractorRendererBuilder implements RendererBuilder {
  private static final int BUFFER_SEGMENT_SIZE = 64 * 1024;
  private static final int BUFFER_SEGMENT_COUNT = 256;

  private final Context context;
  private final String userAgent;
  private final Uri uri;

  public ExtractorRendererBuilder(Context context, String userAgent, Uri uri) {
    this.context = context;
    this.userAgent = userAgent;
    this.uri = uri;
  }
  @Override public void buildRenderers(final ExVidPlayerImp player) {
    Allocator allocator = new DefaultAllocator(BUFFER_SEGMENT_SIZE);
    Handler mainHandler = player.getMainHandler();

    // Build the video and audio renderers.
    DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter(mainHandler, null);
    DataSource dataSource = new DefaultUriDataSource(context, bandwidthMeter, userAgent);
    ExtractorSampleSource sampleSource = new ExtractorSampleSource(uri, dataSource, allocator,
        BUFFER_SEGMENT_COUNT * BUFFER_SEGMENT_SIZE);
    MediaCodecVideoTrackRenderer videoRenderer = new MediaCodecVideoTrackRenderer(context,
        sampleSource, MediaCodecSelector.DEFAULT, MediaCodec.VIDEO_SCALING_MODE_SCALE_TO_FIT, 5000,
        mainHandler, new MediaCodecVideoTrackRenderer.EventListener() {
      @Override public void onDroppedFrames(int count, long elapsed) {

      }

      @Override public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees,
          float pixelWidthHeightRatio) {
        player.setAspectRatio(
            height == 0 ? 1 : (width * pixelWidthHeightRatio) / height);
      }

      @Override public void onDrawnToSurface(Surface surface) {

      }

      @Override public void onDecoderInitializationError(
          MediaCodecTrackRenderer.DecoderInitializationException e) {

      }

      @Override public void onCryptoError(MediaCodec.CryptoException e) {

      }

      @Override public void onDecoderInitialized(String decoderName, long elapsedRealtimeMs,
          long initializationDurationMs) {

      }
    },-1);
    MediaCodecAudioTrackRenderer
        audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource, MediaCodecSelector.DEFAULT,null,true);
    // Invoke the callback.
    TrackRenderer[] renderers = new TrackRenderer[ExVidPlayerImp.RENDERER_COUNT];
    renderers[ExVidPlayerImp.TYPE_VIDEO] = videoRenderer;
    renderers[ExVidPlayerImp.TYPE_AUDIO] = audioRenderer;
    player.onRenderers(renderers, bandwidthMeter);
  }

  @Override public void cancel() {

  }
}
