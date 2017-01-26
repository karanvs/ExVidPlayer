package com.veer.exvidplayersample.VideoPlayer;

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
import android.widget.ProgressBar;
import com.veer.exvidplayer.Controls.Brightness.BrightnessSeekBar;
import com.veer.exvidplayer.Controls.Volume.VolumeSeekBar;
import com.veer.exvidplayer.Gestures.GestureListener;
import com.veer.exvidplayer.Player.Constants;
import com.veer.exvidplayer.Player.ExVidPlayer;
import com.veer.exvidplayer.Player.IPlayerListener;
import com.veer.exvidplayersample.R;

/**
 * Created by Brajendr on 1/26/2017.
 */

public class VideoPlayerFragment extends Fragment{
  private View mView;
  private ProgressBar progressBar;
  private SurfaceView surfaceView;
  private ExVidPlayer exVidPlayer;
  private BrightnessSeekBar mBrightness;
  private VolumeSeekBar mVolumne;
  private IPlayerListener mPlayerListener;
  String[] video_url = new String[] {
      "http://playertest.longtailvideo.com/adaptive/bbbfull/bbbfull.m3u8",
      "http://cdnapi.kaltura.com/p/1878761/sp/187876100/playManifest/entryId/1_usagz19w/flavorIds/1_5spqkazq,1_nslowvhp,1_boih5aji,1_qahc37ag/format/applehttp/protocol/http/a.m3u8"
  };
  String[] video_type = new String[] {
      Constants.MEDIA_TYPE_HLS, Constants.MEDIA_TYPE_HLS
  };

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    mView = inflater.inflate(R.layout.fragment_video_player, container, false);
    initViews();
    setUpListenerForPlayer();
    setUpGestureControls();
    setExoPlayer(video_url,video_type);
    return mView;
  }

  private void setExoPlayer(String[] urls, String[] type) {
    exVidPlayer = new ExVidPlayer(getActivity(), surfaceView, new Handler());
    exVidPlayer.setListener(mPlayerListener);
    exVidPlayer.setSource( urls, type);
  }

  @Override public void onDestroy() {
    if (exVidPlayer != null) exVidPlayer.release();
    super.onDestroy();
  }


  private void initViews() {
    progressBar = (ProgressBar) mView.findViewById(R.id.pbar);
    surfaceView = (SurfaceView) mView.findViewById(R.id.surface_view);
    mBrightness=(BrightnessSeekBar)mView.findViewById(R.id.sbBrightness);
    mVolumne=(VolumeSeekBar)mView.findViewById(R.id.sbVolume);
  }
  private void setUpListenerForPlayer() {
    mPlayerListener=new IPlayerListener() {
      @Override public void onError(String message) {

      }

      @Override public void onBufferingStarted() {
        progressBar.setVisibility(View.VISIBLE);
      }

      @Override public void onBuffering(String percentage) {
        Log.e("percentage", percentage);
      }

      @Override public void onBufferingFinished() {
        progressBar.setVisibility(View.GONE);
      }

      @Override public void onRendereingstarted() {

      }

      @Override public void onCompletion() {

      }

    };
  }
  private void setUpGestureControls() {
    surfaceView.setOnTouchListener(new ExVidPlayerGestureListener(getActivity()));
  }

  private class ExVidPlayerGestureListener extends GestureListener
  {
    public ExVidPlayerGestureListener(Context ctx) {
      super(ctx);
    }

    @Override public void onDoubleTap() {

    }

    @Override public void onHorizontalScroll(MotionEvent event, float delta) {

    }

    @Override public void onVerticalScroll(MotionEvent event, float delta) {
      if (event.getPointerCount() == ONE_FINGER) {
        updateVolumeProgressBar(-delta);
      } else {
        //updateBrightnessProgressBar(-delta);
      }
    }

    @Override public void onSwipeRight() {

    }

    @Override public void onSwipeLeft() {

    }

    @Override public void onSwipeBottom() {

    }

    @Override public void onSwipeTop() {

    }
  }

  private void updateBrightnessProgressBar(float v) {
    mBrightness.manuallyUpdate((int)v);
  }

  private void updateVolumeProgressBar(float v) {
    mVolumne.manuallyUpdate((int)v);
  }
}
