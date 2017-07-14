package com.veer.exvidplayer.Player;

import android.app.Activity;
import android.media.MediaCodec;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;

import com.google.android.exoplayer.DummyTrackRenderer;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.MediaCodecTrackRenderer;
import com.google.android.exoplayer.MediaCodecVideoTrackRenderer;
import com.google.android.exoplayer.MediaFormat;
import com.google.android.exoplayer.TrackRenderer;
import com.google.android.exoplayer.chunk.Format;
import com.google.android.exoplayer.hls.HlsSampleSource;
import com.google.android.exoplayer.upstream.BandwidthMeter;
import com.google.android.exoplayer.util.MimeTypes;
import com.google.android.exoplayer.util.PlayerControl;
import com.google.android.exoplayer.util.Util;
import com.google.android.exoplayer.AspectRatioFrameLayout;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by Brajendra on 1/26/2017.
 */

final class ExVidPlayerImp implements HlsSampleSource.EventListener, ExVidPlayer {
  private static final String TAG = "ExVidPlayerImp";

  public static final int RENDERER_COUNT = 2;
  public static final int TYPE_AUDIO = 1;
  public static final int TYPE_VIDEO = 0;

  private ExoPlayer player;
  private PlayerControl playerControl;
  private SurfaceView surface;
  private Activity activity;
  private Handler mainHandler;
  private ExVidPlayerListener mlistener;
  private ArrayList<String> video_url, video_type;
  private int currentTrackIndex = 0;
  private RendererBuilder rendererBuilder;
  private TrackRenderer videoRenderer;
  private AspectRatioFrameLayout aspectRatioFrameLayout;
  //duration strings
  private String totDuration;
  private String curDuration;

  public int getCurrentDuration() {
    return (int) player.getCurrentPosition();
  }

  public int getTotalDuration() {
    return (int) player.getDuration();
  }

  @Override public boolean canSeekForward() {
    if (player.getCurrentPosition() + Constants.SEEK_TIME > player.getDuration()) {
      return false;
    }
    return true;
  }

  @Override public boolean canSeekBackWard() {
    if (player.getCurrentPosition() + Constants.SEEK_TIME < 0) {
      return false;
    }
    return true;
  }

  @Override public void seekTo(long duration) {
    player.seekTo(duration);
  }

  @Override public void addTrack(String url, String type) {
    video_url.add(url);
    video_type.add(type);
  }

  @Override public void removeTrack(int position) {
    video_url.remove(position);
    video_type.remove(position);
  }

  @Override public void setAspectRatio(float ratio) {
    aspectRatioFrameLayout.setAspectRatio(ratio);
  }

  public ExVidPlayerImp(Activity activity, SurfaceView surface, Handler handler,
      AspectRatioFrameLayout aspectRatioFrameLayout) {
    this.activity = activity;
    this.surface = surface;
    this.aspectRatioFrameLayout = aspectRatioFrameLayout;
    this.mainHandler = handler;
  }

  public ExVidPlayerImp(Activity activity, SurfaceView surface, Handler handler) {
    this.activity = activity;
    this.surface = surface;
    this.aspectRatioFrameLayout = aspectRatioFrameLayout;
    this.mainHandler = handler;
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
    switch (video_type.get(currentTrackIndex)) {
      case "hls":
        return new HlsRendererBuilder(activity, userAgent, video_url.get(currentTrackIndex));
      case "others":
        return new ExtractorRendererBuilder(activity, userAgent,
            Uri.parse(video_url.get(currentTrackIndex)));
      default:
        throw new IllegalStateException("Unsupported type: " + video_url.get(currentTrackIndex));
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
      mainHandler.removeCallbacks(updatePlayer);
      mainHandler.removeCallbacks(hideControl);
      player.release();
    }
  }

  @Override
  public void setSource(ArrayList<String> url, ArrayList<String> vtype, int currentIndex) {
    video_url = url;
    video_type = vtype;
    currentTrackIndex = currentIndex;
    initPlayer();
  }

  @Override public void reset() {

  }

  @Override public void setCurrentTrack(int position) {

    release();
    currentTrackIndex = position;
    initPlayer();
  }

  @Override public void release() {
    killPlayer();
  }

  @Override public void setListener(ExVidPlayerListener listener) {
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
    menu.add(Menu.NONE, 0, 0, "Bitrate");

    for (int i = 0; i < player.getTrackCount(0); i++) {
      MediaFormat format = player.getTrackFormat(0, i);
      if (MimeTypes.isVideo(format.mimeType)) {
        Log.e("dsa", format.bitrate + "");
        if (format.adaptive) {
          menu.add(1, (i + 1), (i + 1), "Auto");
        } else {

          if (!formats.contains(format.bitrate)) {
            menu.add(1, (i + 1), (i + 1), (format.bitrate) / 1000 + " kbps");
            formats.add(format.bitrate);
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
    if (nextTrack < video_url.size()) {
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

  @Override public void showControls() {
    mlistener.showControls();
    mainHandler.removeCallbacks(hideControl);
    mainHandler.postDelayed(hideControl, 3000);
  }

  @Override public void forward() {
    player.seekTo(player.getCurrentPosition() + Constants.SEEK_TIME);
  }

  @Override public void reverse() {
    player.seekTo(player.getCurrentPosition() - Constants.SEEK_TIME);
  }

  @Override public boolean isPlaying() {
    return player.getPlayWhenReady();
  }

  @Override public void pause() {
    player.setPlayWhenReady(false);
  }

  @Override public void play() {
    player.setPlayWhenReady(true);
  }

  private Runnable updatePlayer = new Runnable() {
    @Override public void run() {
      switch (player.getPlaybackState()) {
        case ExoPlayer.STATE_BUFFERING:
          mlistener.onBuffering(playerControl.getBufferPercentage() + "");
          break;
        case ExoPlayer.STATE_ENDED:
          mlistener.onCompletion();
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
      mlistener.onProgressChanged((int) player.getCurrentPosition());
      mainHandler.postDelayed(updatePlayer, 200);
    }
  };

  private Runnable hideControl = new Runnable() {
    @Override public void run() {
      mlistener.hideControls();
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

