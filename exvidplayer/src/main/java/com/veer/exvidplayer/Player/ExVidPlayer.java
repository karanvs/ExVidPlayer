package com.veer.exvidplayer.Player;

import android.app.Activity;
import android.os.Handler;
import android.view.SurfaceView;
import android.view.View;
import com.google.android.exoplayer.AspectRatioFrameLayout;

import java.util.ArrayList;

public interface ExVidPlayer {
  void setSource(ArrayList<String> urls, ArrayList<String> vtype, int currentIndex);

  void reset();

  void setCurrentTrack(int position);

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

  void addTrack(String url,String type);

  void removeTrack(int position);

  void setAspectRatio(float ratio);

  public static final class Factory {

    private Factory() {
    }

    public static ExVidPlayerImp newInstance(Activity actvity, SurfaceView surface,
        Handler handler) {
      return new ExVidPlayerImp(actvity, surface, handler);
    }

    public static ExVidPlayerImp newInstance(Activity actvity, SurfaceView surface, Handler handler,
        AspectRatioFrameLayout aspectRatioFrameLayout) {
      return new ExVidPlayerImp(actvity, surface, handler, aspectRatioFrameLayout);
    }
  }
}

