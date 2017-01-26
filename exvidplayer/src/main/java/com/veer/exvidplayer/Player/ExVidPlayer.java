package com.veer.exvidplayer.Player;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.widget.PopupMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import com.google.android.exoplayer.AspectRatioFrameLayout;
import com.google.android.exoplayer.DummyTrackRenderer;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.MediaCodecVideoTrackRenderer;
import com.google.android.exoplayer.MediaFormat;
import com.google.android.exoplayer.TrackRenderer;
import com.google.android.exoplayer.chunk.Format;
import com.google.android.exoplayer.hls.HlsSampleSource;
import com.google.android.exoplayer.upstream.BandwidthMeter;
import com.google.android.exoplayer.util.MimeTypes;
import com.google.android.exoplayer.util.PlayerControl;
import com.google.android.exoplayer.util.Util;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by Brajendr on 1/26/2017.
 */

public class ExVidPlayer implements HlsSampleSource.EventListener, IPlayer {
  private static final String TAG = "ExVidPlayer";

  public static final int RENDERER_COUNT = 2;
  public static final int TYPE_AUDIO = 1;
  public static final int TYPE_VIDEO = 0;

  private ExoPlayer player;
  private PlayerControl playerControl;
  private SurfaceView surface;
  private Activity activity;
  private Handler mainHandler;
  private IPlayerListener mlistener;
  private String[] video_url, video_type;
  private int currentTrackIndex = 0;
  private RendererBuilder rendererBuilder;
  private TrackRenderer videoRenderer;
  private AspectRatioFrameLayout aspectRatioFrameLayout;
  //duration strings
  private String totDuration;
  private String curDuration;

  public String getCurrentDuration() {
    return curDuration;
  }

  public String getTotoalDuration() {
    return totDuration;
  }

  public ExVidPlayer(Activity activity, SurfaceView surface, Handler handler) {
    this.activity = activity;
    this.surface = surface;
    this.mainHandler = handler;
    this.aspectRatioFrameLayout = aspectRatioFrameLayout;
  }

  private void initPlayer() {
    player = ExoPlayer.Factory.newInstance(RENDERER_COUNT);
    playerControl = new PlayerControl(player);
    if (player != null) {
      rendererBuilder = getHpLibRendererBuilder();
      rendererBuilder.buildRenderers(this);
      mainHandler.postDelayed(updatePlayer, 200);
    }
  }

  private RendererBuilder getHpLibRendererBuilder() {
    String userAgent = Util.getUserAgent(activity, "HpLib");
    switch (video_type[currentTrackIndex]) {
      case "hls":
        return new HlsRendererBuilder(activity, userAgent, video_url[currentTrackIndex]);
      case "others":
        return new ExtractorRendererBuilder(activity, userAgent,
            Uri.parse(video_url[currentTrackIndex]));
      default:
        throw new IllegalStateException("Unsupported type: " + video_url[currentTrackIndex]);
    }
  }

  public Handler getMainHandler() {
    return mainHandler;
  }

  public void onRenderersError(Exception e) {
  }

  public void onRenderers(TrackRenderer[] renderers, BandwidthMeter bandwidthMeter) {
    for (int i = 0; i < renderers.length; i++) {
      if (renderers[i] == null) {
        renderers[i] = new DummyTrackRenderer();
      }
    }
    // Complete preparation.
    this.videoRenderer = renderers[TYPE_VIDEO];
    pushSurface(false);
    player.prepare(renderers);
    player.setPlayWhenReady(true);
  }

  private void pushSurface(boolean blockForSurfacePush) {
    if (videoRenderer == null) {
      return;
    }
    if (blockForSurfacePush) {
      player.blockingSendMessage(videoRenderer, MediaCodecVideoTrackRenderer.MSG_SET_SURFACE,
          surface.getHolder().getSurface());
    } else {
      player.sendMessage(videoRenderer, MediaCodecVideoTrackRenderer.MSG_SET_SURFACE,
          surface.getHolder().getSurface());
    }
  }

  private void killPlayer() {
    if (player != null) {
      player.release();
    }
  }

  @Override public void setSource(String[] url, String[] vtype) {
    video_url = url;
    video_type = vtype;
    initPlayer();
  }

  @Override public void reset() {

  }

  @Override public void release() {
    killPlayer();
  }

  @Override public void setListener(IPlayerListener listener) {
    mlistener = listener;
  }

  @Override public void setQuality(View v) {
    PopupMenu popup = new PopupMenu(activity, v);
    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
      @Override public boolean onMenuItemClick(MenuItem item) {
        player.setSelectedTrack(0, (item.getItemId() - 1));
        return false;
      }
    });
    ArrayList<Integer> formats = new ArrayList<>();
    Menu menu = popup.getMenu();
    menu.add(Menu.NONE, 0, 0, "Video Quality");
    for (int i = 0; i < player.getTrackCount(0); i++) {
      MediaFormat format = player.getTrackFormat(0, i);
      if (MimeTypes.isVideo(format.mimeType)) {
        if (format.adaptive) {
          menu.add(1, (i + 1), (i + 1), "Auto");
        } else {
          if (!formats.contains(format.width)) {
            menu.add(1, (i + 1), (i + 1), format.width + "p");
            formats.add(format.width);
          }
        }
      }
    }
    menu.setGroupCheckable(1, true, true);
    menu.findItem((player.getSelectedTrack(0) + 1)).setChecked(true);
    popup.show();
  }

  @Override public void nextTrack() {
    int nextTrack = currentTrackIndex + 1;
    if (nextTrack < video_url.length) {
      release();
      currentTrackIndex = nextTrack;
      initPlayer();
    }
  }

  @Override public void previousTrack() {
    int previousTrack = currentTrackIndex - 1;
    if (previousTrack >= 0) {
      release();
      currentTrackIndex = previousTrack;
      initPlayer();
    }
  }

  private Runnable updatePlayer = new Runnable() {
    @Override public void run() {
      switch (player.getPlaybackState()) {
        case ExoPlayer.STATE_BUFFERING:
          mlistener.onBuffering(playerControl.getBufferPercentage() + "");
          break;
        case ExoPlayer.STATE_ENDED:
          break;
        case ExoPlayer.STATE_IDLE:
          break;
        case ExoPlayer.STATE_PREPARING:
          mlistener.onBufferingStarted();
          break;
        case ExoPlayer.STATE_READY:
          mlistener.onBufferingFinished();
          break;
        default:
          break;
      }
      totDuration =
          String.format("%02d.%02d.%02d", TimeUnit.MILLISECONDS.toHours(player.getDuration()),
              TimeUnit.MILLISECONDS.toMinutes(player.getDuration()) - TimeUnit.HOURS.toMinutes(
                  TimeUnit.MILLISECONDS.toHours(player.getDuration())),
              // The change is in this line
              TimeUnit.MILLISECONDS.toSeconds(player.getDuration()) - TimeUnit.MINUTES.toSeconds(
                  TimeUnit.MILLISECONDS.toMinutes(player.getDuration())));
      curDuration = String.format("%02d.%02d.%02d",
          TimeUnit.MILLISECONDS.toHours(player.getCurrentPosition()),
          TimeUnit.MILLISECONDS.toMinutes(player.getCurrentPosition()) - TimeUnit.HOURS.toMinutes(
              TimeUnit.MILLISECONDS.toHours(player.getCurrentPosition())),
          // The change is in this line
          TimeUnit.MILLISECONDS.toSeconds(player.getCurrentPosition()) - TimeUnit.MINUTES.toSeconds(
              TimeUnit.MILLISECONDS.toMinutes(player.getCurrentPosition())));
      mainHandler.postDelayed(updatePlayer, 200);
    }
  };

  //hlsEvents
  @Override public void onLoadStarted(int sourceId, long length, int type, int trigger,
      Format format, long mediaStartTimeMs, long mediaEndTimeMs) {

  }

  @Override
  public void onLoadCompleted(int sourceId, long bytesLoaded, int type, int trigger, Format format,
      long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs) {

  }

  @Override public void onLoadCanceled(int sourceId, long bytesLoaded) {

  }

  @Override public void onLoadError(int sourceId, IOException e) {

  }

  @Override
  public void onUpstreamDiscarded(int sourceId, long mediaStartTimeMs, long mediaEndTimeMs) {

  }

  @Override public void onDownstreamFormatChanged(int sourceId, Format format, int trigger,
      long mediaTimeMs) {

  }

}

