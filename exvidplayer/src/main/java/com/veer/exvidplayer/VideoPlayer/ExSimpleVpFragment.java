package com.veer.exvidplayer.VideoPlayer;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.google.android.exoplayer.AspectRatioFrameLayout;
import com.veer.exvidplayer.Gestures.GestureListener;
import com.veer.exvidplayer.Player.Constants;
import com.veer.exvidplayer.Player.ExVidPlayer;
import com.veer.exvidplayer.Player.ExVidPlayerListener;
import com.veer.exvidplayer.R;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import com.veer.exvidplayer.Utils.*;

/**
 * Created by Brajendr on 1/26/2017.
 */

public class ExSimpleVpFragment extends Fragment {
  private View mView;
  private ProgressBar progressBar;
  private SurfaceView surfaceView;
  private ExVidPlayer exVidPlayer;
  //brightness
 /* private LinearLayout brightnessSlider, brightnessCenterText;
  private ImageView ivBrightness, ivBrightnessImage;
  private ProgressBar pBarBrighness;
  private TextView tvBrightnessPercent;*/
  private AspectRatioFrameLayout aspectRatioFrameLayout;

  //controls
  private ViewGroup root;
  private SeekBar mProgress;
  private TextView tvCurrent, tvTotal;

  private LinearLayout prgCenterText;
  private TextView tvCenterCurrent, tvCenterProg;
  //volume
/*  private LinearLayout volumeSlider, volumeCenterText;
  private ImageView ivVolume, ivVolumeImage;
  private VolBar pBarVolume;
  private TextView tvVolumePercent;*/
  //playerevent listener
  private ExVidPlayerListener mPlayerListener;
  ArrayList<String> video_url,video_type;
  private int currentIndex=0;
  private Handler mainHandler;
  private RelativeLayout rlReplay;
  private ImageView ivReplay;


  public ExVpListener getExVpListener() {
    return exVpListener;
  }

  private ExVpListener exVpListener = new ExVpListener() {
    @Override public void setControlLayout(ViewGroup viewGroup) {
      root = viewGroup;
    }

    @Override public void play() {
      if (exVidPlayer != null) {
        exVidPlayer.play();
      }
    }

    @Override public void stop() {
      if (exVidPlayer != null) {
        exVidPlayer.pause();
      }
    }

    @Override public void forward() {
      if (exVidPlayer != null) {
        goForward();
      }
    }

    @Override public void reverse() {
      if (exVidPlayer != null) {
        goReverse();
      }
    }

    @Override public void nextTrack() {
      if (exVidPlayer != null) {
        exVidPlayer.nextTrack();
      }
    }

    @Override public void previousTrack() {
      if (exVidPlayer != null) {
        exVidPlayer.previousTrack();
      }
    }

    @Override public void setProgressBar(SeekBar seekBar) {
      mProgress = seekBar;
    }

    @Override public void setDurationText(TextView textView) {
      tvTotal = textView;
    }

    @Override public void setCurrentText(TextView textView) {
      tvCurrent = textView;
    }

    @Override public void changeQuality(View v) {
      if (exVidPlayer != null) {
        exVidPlayer.setQuality(v);
      }
    }

    @Override public void addTrack(String url, String type) {
      if(exVidPlayer!=null)
      {
        exVidPlayer.addTrack(url,type);
      }
    }

    @Override public void setCurrent(int position) {
      if(exVidPlayer!=null)
      {
        exVidPlayer.setCurrentTrack(position);
      }
    }

    @Override public boolean isPlaying() {

      return exVidPlayer.isPlaying();

    }

    @Override public void removeTrack(int position) {
      if(exVidPlayer!=null)
      {
        exVidPlayer.removeTrack(position);
      }
    }

    @Override public void seekToProgress(int progress) {
      if(exVidPlayer!=null)
      {
        exVidPlayer.seekTo(progress);
      }
    }
  };

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setRetainInstance(true);
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    getVideoUrls();
    mView = inflater.inflate(R.layout.fragment_videoplayer_without_controls, container, false);
    mainHandler = new Handler();
    initViews();
    setUpListenerForPlayer();
    //setUpGestureControls();
    setExoPlayer();
    //setUpBrightness();
    return mView;
  }

  private void getVideoUrls() {
    video_url = getArguments().getStringArrayList("urls");
    video_type = getArguments().getStringArrayList("type");
    currentIndex=getArguments().getInt("currentIndex");
  }

  /*private void setUpBrightness() {
    pBarBrighness.setMax(255);
    pBarBrighness.setProgress(BrightnessUtils.get(getActivity()));
  }*/

  private void setExoPlayer() {
    exVidPlayer = ExVidPlayer.Factory.newInstance(getActivity(), surfaceView, mainHandler,aspectRatioFrameLayout);
    exVidPlayer.setListener(mPlayerListener);
    exVidPlayer.setSource(video_url, video_type,currentIndex);
  }

  @Override public void onStop() {
    if (exVidPlayer != null) exVidPlayer.release();
    super.onStop();
  }

  @Override public void onDestroy() {
    if (exVidPlayer != null) exVidPlayer.release();
    super.onDestroy();
  }

  private void initViews() {
    progressBar = (ProgressBar) mView.findViewById(R.id.pbar);
    surfaceView = (SurfaceView) mView.findViewById(R.id.surface_view);
    //skiptexts
    prgCenterText = (LinearLayout) mView.findViewById(R.id.seekbar_center_text);
    tvCenterCurrent = (TextView) mView.findViewById(R.id.txt_seek_currTime);
    tvCenterProg = (TextView) mView.findViewById(R.id.txt_seek_secs);
    //brightness
    /*brightnessSlider = (LinearLayout) getView(R.id.brightness_slider_container);
    ivBrightness = (ImageView) getView(R.id.brightness_image);
    ivBrightnessImage = (ImageView) getView(R.id.brightnessIcon);
    pBarBrighness = (ProgressBar) getView(R.id.brightness_slider);
    tvBrightnessPercent = (TextView) getView(R.id.brigtness_perc_center_text);
    brightnessCenterText = (LinearLayout) getView(R.id.brightness_center_text);
    //volume
    volumeSlider = (LinearLayout) getView(R.id.volume_slider_container);
    ivVolume = (ImageView) getView(R.id.vol_image);
    ivVolumeImage = (ImageView) getView(R.id.volIcon);
    pBarVolume = (VolBar) getView(R.id.volume_slider);
    tvVolumePercent = (TextView) getView(R.id.vol_perc_center_text);
    volumeCenterText = (LinearLayout) getView(R.id.vol_center_text);*/

    aspectRatioFrameLayout=(AspectRatioFrameLayout)getView(R.id.video_frame);
    rlReplay = (RelativeLayout) getView(R.id.rlReplay);
    ivReplay = (ImageView) getView(R.id.replayEvent);
    ivReplay.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        rlReplay.setVisibility(View.GONE);
        if (exVidPlayer != null) {
          exVidPlayer.seekTo(0);
          exVidPlayer.play();
        }
      }
    });
  }

  public View getView(int id) {
    return mView.findViewById(id);
  }

  private void setUpListenerForPlayer() {
    mPlayerListener = new ExVidPlayerListener() {
      @Override public void onError(String message) {

      }

      @Override public void onBufferingStarted() {
        progressBar.setVisibility(View.VISIBLE);
      }

      @Override public void onBuffering(String percentage) {
        progressBar.setVisibility(View.VISIBLE);
      }

      @Override public void onBufferingFinished() {
        progressBar.setVisibility(View.GONE);
        if (mProgress != null) mProgress.setMax(exVidPlayer.getTotalDuration());
        if (tvTotal != null) tvTotal.setText(getDurationString(exVidPlayer.getTotalDuration()));
      }

      @Override public void onRendereingstarted() {

      }

      @Override public void onCompletion() {
        rlReplay.setVisibility(View.VISIBLE);
      }

      @Override public void hideControls() {
        Log.e("contrl","ds");
        if (root != null) {
          Log.e("contrl","ds");
          root.setVisibility(View.GONE);
        }
      }

      @Override public void showControls() {
        if (root != null) root.setVisibility(View.VISIBLE);
      }

      @Override public void onProgressChanged(int progress) {
        if (mProgress != null) {
          mProgress.setMax(exVidPlayer.getTotalDuration());
          mProgress.setProgress(progress);
        }
        if (tvCurrent != null) {
          tvCurrent.setText(getDurationString(exVidPlayer.getCurrentDuration()));
        }
      }
    };
  }

  private String getDurationString(int duration) {
    return String.format("%02d.%02d.%02d", TimeUnit.MILLISECONDS.toHours(duration),
        TimeUnit.MILLISECONDS.toMinutes(duration) - TimeUnit.HOURS.toMinutes(
            TimeUnit.MILLISECONDS.toHours(duration)),
        // The change is in this line
        TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(
            TimeUnit.MILLISECONDS.toMinutes(duration)));
  }


  private void goForward() {
    if (exVidPlayer != null && exVidPlayer.canSeekForward()) {
      prgCenterText.setVisibility(View.VISIBLE);
      tvCenterCurrent.setText("[" + getDurationString(exVidPlayer.getCurrentDuration()) + "]");
      tvCenterProg.setText("+ " + getDurationString(Constants.SEEK_TIME));
      exVidPlayer.forward();
      mainHandler.postDelayed(new Runnable() {
        @Override public void run() {
          prgCenterText.setVisibility(View.GONE);
        }
      }, 2000);
    }
  }

  private void goReverse() {
    if (exVidPlayer != null && exVidPlayer.canSeekBackWard()) {
      tvCenterCurrent.setText("[" + getDurationString(exVidPlayer.getCurrentDuration()) + "]");
      tvCenterProg.setText("- " + getDurationString(Constants.SEEK_TIME));
      prgCenterText.setVisibility(View.VISIBLE);
      exVidPlayer.reverse();
      mainHandler.postDelayed(new Runnable() {
        @Override public void run() {
          prgCenterText.setVisibility(View.GONE);
        }
      }, 2000);
    }
  }


  private int extractVerticalDeltaScale(float deltaY, ProgressBar progressBar) {
    return extractDeltaScale(progressBar.getHeight(), deltaY, progressBar);
  }

  private int extractDeltaScale(int availableSpace, float deltaX, ProgressBar progressBar) {
    int x = (int) deltaX;
    float scale;
    float progress = progressBar.getProgress();
    final int max = progressBar.getMax();

    if (x < 0) {
      scale = (float) (x) / (float) (max - availableSpace);
      progress = progress - (scale * progress);
    } else {
      scale = (float) (x) / (float) availableSpace;
      progress += scale * max;
    }

    return (int) progress;
  }


}
