package com.veer.exvidplayer.Player;

import android.app.Activity;
import android.os.Handler;
import android.view.SurfaceView;
import android.view.View;

public interface ExVidPlayer {
  void setSource(String[] url, String[] vtype);

  void reset();

  void release();

  void setListener(ExVidPlayerListener listener);

  void setQuality(View v);

  void nextTrack();

  void previousTrack();

  void showControls();

  void forward();

  void reverse();

  boolean isPlaying();

  void pause();

  void play();

  int getCurrentDuration();

  int getTotalDuration();

  boolean canSeekForward();

  boolean canSeekBackWard();

  void seekTo(long duration);

  public static final class Factory {

    private Factory() {
    }

    public static ExVidPlayerImp newInstance(Activity actvity, SurfaceView surface,
        Handler handler) {
      return new ExVidPlayerImp(actvity, surface, handler);
    }
  }
}

