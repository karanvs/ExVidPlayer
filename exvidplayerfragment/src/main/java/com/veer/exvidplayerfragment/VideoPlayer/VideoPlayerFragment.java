package com.veer.exvidplayerfragment.VideoPlayer;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import com.veer.exvidplayer.Gestures.GestureListener;
import com.veer.exvidplayer.Player.Constants;
import com.veer.exvidplayer.Player.ExVidPlayer;
import com.veer.exvidplayer.Player.ExVidPlayerListener;
import com.veer.exvidplayerfragment.R;
import java.util.concurrent.TimeUnit;
import com.veer.exvidplayerfragment.Utils.*;
/**
 * Created by Brajendr on 1/26/2017.
 */

public class VideoPlayerFragment extends Fragment {
  private View mView;
  private ProgressBar progressBar;
  private SurfaceView surfaceView;
  private ExVidPlayer exVidPlayer;
  //controls
  private LinearLayout root,prgCenterText;
  private ImageButton ivLock, ivNext, ivPrev, ivForword, ivRev, ivPlayPause, ivSetting;
  private SeekBar mProgress;
  private TextView tvCurrent,tvTotal,tvCenterCurrent,tvCenterProg;
  //brightness
  private LinearLayout brightnessSlider,brightnessCenterText;
  private ImageView ivBrightness,ivBrightnessImage;
  private ProgressBar pBarBrighness;
  private TextView tvBrightnessPercent;
  //volume
  private LinearLayout volumeSlider,volumeCenterText;
  private ImageView ivVolume,ivVolumeImage;
  private VolBar pBarVolume;
  private TextView tvVolumePercent;
  //playerevent listener
  private ExVidPlayerListener mPlayerListener;
  String[] video_url = new String[] {
      "http://playertest.longtailvideo.com/adaptive/bbbfull/bbbfull.m3u8",
      "http://cdnapi.kaltura.com/p/1878761/sp/187876100/playManifest/entryId/1_usagz19w/flavorIds/1_5spqkazq,1_nslowvhp,1_boih5aji,1_qahc37ag/format/applehttp/protocol/http/a.m3u8"
  };
  String[] video_type = new String[] {
      Constants.MEDIA_TYPE_HLS, Constants.MEDIA_TYPE_HLS
  };
  private Handler mainHandler;


  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    mView = inflater.inflate(R.layout.fragment_video_player, container, false);
    mainHandler=new Handler();
    initViews();
    setUpListenerForPlayer();
    setUpGestureControls();
    setExoPlayer(video_url, video_type);
    setUpControlEvents();
    setUpBrightness();
    return mView;
  }

  private void setUpBrightness() {
    pBarBrighness.setMax(255);
    pBarBrighness.setProgress(BrightnessUtils.get(getActivity()));
  }

  private void setUpControlEvents() {
    ivLock.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        if (exVidPlayer != null) {

        }
      }
    });
    ivForword.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        goForward();
      }
    });
    ivRev.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
       goReverse();
      }
    });
    ivNext.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        if (exVidPlayer != null) {
          exVidPlayer.nextTrack();
        }
      }
    });
    ivPrev.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        if (exVidPlayer != null) {
          exVidPlayer.previousTrack();
        }
      }
    });
    ivPlayPause.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        if (exVidPlayer != null) {
          if (exVidPlayer.isPlaying()) {
            exVidPlayer.pause();
            ivPlayPause.setImageResource(R.drawable.ic_play_circle_filled_white_white_24dp);
          } else {
            exVidPlayer.play();
            ivPlayPause.setImageResource(R.drawable.ic_pause_circle_filled_white_24dp);
          }
        }
      }
    });
    ivSetting.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        if (exVidPlayer != null) {
          exVidPlayer.setQuality(view);
        }
      }
    });
    mProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

      }

      @Override public void onStartTrackingTouch(SeekBar seekBar) {

      }

      @Override public void onStopTrackingTouch(SeekBar seekBar) {
          if(exVidPlayer!=null)
          {
            exVidPlayer.seekTo(mProgress.getProgress());
          }
      }
    });
  }

  private void setExoPlayer(String[] urls, String[] type) {
    exVidPlayer = ExVidPlayer.Factory.newInstance(getActivity(), surfaceView, mainHandler);
    exVidPlayer.setListener(mPlayerListener);
    exVidPlayer.setSource(urls, type);
  }

  @Override public void onDestroy() {
    if (exVidPlayer != null) exVidPlayer.release();
    super.onDestroy();
  }

  private void initViews() {
    progressBar = (ProgressBar) mView.findViewById(R.id.pbar);
    surfaceView = (SurfaceView) mView.findViewById(R.id.surface_view);
    //controls
    root = (LinearLayout) mView.findViewById(R.id.root);
    ivLock = (ImageButton) mView.findViewById(R.id.btn_lock);
    ivRev = (ImageButton) mView.findViewById(R.id.btn_rev);
    ivForword = (ImageButton) mView.findViewById(R.id.btn_fwd);
    ivNext = (ImageButton) mView.findViewById(R.id.btn_next);
    ivPrev = (ImageButton) mView.findViewById(R.id.btn_prev);
    ivPlayPause = (ImageButton) mView.findViewById(R.id.btn_pause);
    ivSetting = (ImageButton) mView.findViewById(R.id.btn_settings);
    mProgress=(SeekBar)mView.findViewById(R.id.seekbar);
    tvCurrent=(TextView)mView.findViewById(R.id.txt_currentTime);
    tvTotal=(TextView)mView.findViewById(R.id.txt_totalDuration);
    prgCenterText=(LinearLayout) mView.findViewById(R.id.seekbar_center_text);
    tvCenterCurrent=(TextView)mView.findViewById(R.id.txt_seek_currTime);
    tvCenterProg=(TextView)mView.findViewById(R.id.txt_seek_secs);
    //brightness
    brightnessSlider=(LinearLayout)getView(R.id.brightness_slider_container);
    ivBrightness=(ImageView)getView(R.id.brightness_image);
    ivBrightnessImage=(ImageView)getView(R.id.brightnessIcon);
    pBarBrighness=(ProgressBar)getView(R.id.brightness_slider);
    tvBrightnessPercent=(TextView)getView(R.id.brigtness_perc_center_text);
    brightnessCenterText=(LinearLayout)getView(R.id.brightness_center_text);
    //volume
    volumeSlider=(LinearLayout)getView(R.id.volume_slider_container);
    ivVolume=(ImageView)getView(R.id.vol_image);
    ivVolumeImage=(ImageView)getView(R.id.volIcon);
    pBarVolume=(VolBar) getView(R.id.volume_slider);
    tvVolumePercent=(TextView)getView(R.id.vol_perc_center_text);
    volumeCenterText=(LinearLayout)getView(R.id.vol_center_text);
  }

  public View getView(int id)
  {
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
        mProgress.setMax(exVidPlayer.getTotalDuration());
        tvTotal.setText(getDurationString(exVidPlayer.getTotalDuration()));
      }

      @Override public void onRendereingstarted() {

      }

      @Override public void onCompletion() {
        if (exVidPlayer != null) {
          exVidPlayer.nextTrack();
        }
      }

      @Override public void hideControls() {
        root.setVisibility(View.GONE);
      }

      @Override public void showControls() {
        root.setVisibility(View.VISIBLE);
      }

      @Override public void onProgressChanged(int progress) {
        mProgress.setMax(exVidPlayer.getTotalDuration());
        mProgress.setProgress(progress);
        tvCurrent.setText(getDurationString(exVidPlayer.getCurrentDuration()));

      }
    };
  }

  private String getDurationString(int duration)
  {
    return
    String.format("%02d.%02d.%02d", TimeUnit.MILLISECONDS.toHours(duration),
            TimeUnit.MILLISECONDS.toMinutes(duration) - TimeUnit.HOURS.toMinutes(
                TimeUnit.MILLISECONDS.toHours(duration)),
            // The change is in this line
            TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(
                TimeUnit.MILLISECONDS.toMinutes(duration)));
  }

  private void setUpGestureControls() {
    surfaceView.setOnTouchListener(new ExVidPlayerGestureListener(getActivity()));
  }

  private class ExVidPlayerGestureListener extends GestureListener {
    ExVidPlayerGestureListener(Context ctx) {
      super(ctx);
    }

    @Override public void onTap() {
      if (exVidPlayer != null) {
        exVidPlayer.showControls();
      }
    }

    @Override public void onHorizontalScroll(MotionEvent event, float delta) {
      updateVolumeProgressBar(-delta);
    }

    @Override public void onVerticalScroll(MotionEvent event, float delta) {
      updateBrightnessProgressBar(-delta);

    }

    @Override public void onSwipeRight() {
      goForward();

    }

    @Override public void onSwipeLeft() {
      goReverse();
    }

    @Override public void onSwipeBottom() {

    }

    @Override public void onSwipeTop() {

    }

  }

  private void goForward() {
    if(exVidPlayer!=null && exVidPlayer.canSeekForward())
    {
      prgCenterText.setVisibility(View.VISIBLE);
      tvCenterCurrent.setText("["+getDurationString(exVidPlayer.getCurrentDuration())+"]");
      tvCenterProg.setText("+ "+getDurationString(Constants.SEEK_TIME));
      exVidPlayer.forward();
      mainHandler.postDelayed(new Runnable() {
        @Override public void run() {
          prgCenterText.setVisibility(View.GONE);
        }
      },2000);
    }
  }

  private void goReverse()
  {
    if(exVidPlayer!=null&& exVidPlayer.canSeekBackWard())
    {
      tvCenterCurrent.setText("["+getDurationString(exVidPlayer.getCurrentDuration())+"]");
      tvCenterProg.setText("- "+getDurationString(Constants.SEEK_TIME));
      prgCenterText.setVisibility(View.VISIBLE);
      exVidPlayer.reverse();
      mainHandler.postDelayed(new Runnable() {
        @Override public void run() {
          prgCenterText.setVisibility(View.GONE);
        }
      },2000);
    }
  }

  private void updateBrightnessProgressBar(float v) {
    brightnessSlider.setVisibility(View.VISIBLE);
    brightnessCenterText.setVisibility(View.VISIBLE);
    int bfactor=(int) v*((255-pBarBrighness.getProgress())/255);
    int brighness=BrightnessUtils.get(getActivity())+bfactor;

    if (v < BrightnessUtils.MIN_BRIGHTNESS) {
      v = BrightnessUtils.MIN_BRIGHTNESS;
    } else if (v > BrightnessUtils.MAX_BRIGHTNESS) {
      v = BrightnessUtils.MAX_BRIGHTNESS;
    }
    BrightnessUtils.set(getActivity(), (int) v);
    pBarBrighness.setProgress((int) v);
    int brightPerc=pBarBrighness.getProgress()*100/255;
    if (brightPerc < 30) {
      ivBrightness.setImageResource(R.drawable.hplib_brightness_minimum);
      ivBrightnessImage.setImageResource(R.drawable.hplib_brightness_minimum);
    } else if (brightPerc > 30 && brightPerc < 80) {
      ivBrightness.setImageResource(R.drawable.hplib_brightness_medium);
      ivBrightnessImage.setImageResource(R.drawable.hplib_brightness_medium);
    } else if (brightPerc > 80) {
      ivBrightness.setImageResource(R.drawable.hplib_brightness_maximum);
      ivBrightnessImage.setImageResource(R.drawable.hplib_brightness_maximum);
    }
    tvBrightnessPercent.setText(" " + (int) brightPerc);
    mainHandler.postDelayed(new Runnable() {
      @Override public void run() {
        brightnessSlider.setVisibility(View.GONE);
        brightnessCenterText.setVisibility(View.GONE);
      }
    },2000);

  }

  private void updateVolumeProgressBar(float v) {
    int max=pBarVolume.getMax();
    volumeSlider.setVisibility(View.VISIBLE);
    volumeCenterText.setVisibility(View.VISIBLE);
    int bfactor=(int) v*((max-pBarVolume.getProgress())/max);
    int vol=pBarVolume.getProgress() +bfactor;
    if (vol < 0) {
      vol = 0;
    } else if (vol > max) {
      vol = max;
    }
    pBarVolume.setProgress((int) vol);
    int volPerc=pBarVolume.getProgress()*100/max;
    tvVolumePercent.setText(" " +  volPerc);
    if (volPerc < 1) {
      ivVolume.setImageResource(R.drawable.hplib_volume_mute);
      ivVolumeImage.setImageResource(R.drawable.hplib_volume_mute);
      tvVolumePercent.setVisibility(View.GONE);
    } else if (volPerc >= 1) {
      ivVolume.setImageResource(R.drawable.hplib_volume);
      ivVolumeImage.setImageResource(R.drawable.hplib_volume);
      tvVolumePercent.setVisibility(View.VISIBLE);
    }
    mainHandler.postDelayed(new Runnable() {
      @Override public void run() {
        volumeSlider.setVisibility(View.GONE);
        volumeCenterText.setVisibility(View.GONE);
      }
    },2000);

  }
}
