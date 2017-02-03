package com.veer.exvidplayersample;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.veer.exvidplayer.Player.Constants;
import com.veer.exvidplayer.VideoPlayer.ExVpCompleteFragment;
import com.veer.exvidplayer.VideoPlayer.ExVpFragment;
import com.veer.exvidplayer.VideoPlayer.ExVpListener;

public class CustomControlsActivity extends AppCompatActivity {
  String[] video_url = new String[] {
      "http://playertest.longtailvideo.com/adaptive/bbbfull/bbbfull.m3u8",
      "http://cdnapi.kaltura.com/p/1878761/sp/187876100/playManifest/entryId/1_usagz19w/flavorIds/1_5spqkazq,1_nslowvhp,1_boih5aji,1_qahc37ag/format/applehttp/protocol/http/a.m3u8"
  };
  String[] video_type = new String[] {
      Constants.MEDIA_TYPE_HLS, Constants.MEDIA_TYPE_HLS
  };
  //controls
  private LinearLayout root;
  private ImageButton ivNext, ivPrev, ivForword, ivRev, ivPlayPause, ivSetting;
  private SeekBar mProgress;
  private TextView tvCurrent, tvTotal;
  private ExVpListener exVpControls;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_simple_player);
    init();
    setUpPlayer();
  }

  private void setUpControls() {
    ivForword.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        exVpControls.forward();
      }
    });
    ivRev.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
       exVpControls.reverse();

      }
    });
    ivNext.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
     exVpControls.nextTrack();

      }
    });
    ivPrev.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
      exVpControls.previousTrack();
      }
    });
    ivPlayPause.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {

      }
    });
    ivSetting.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
       exVpControls.changeQuality(view);
      }
    });
    exVpControls.setControlLayout(root);
    exVpControls.setProgressBar(mProgress);
    exVpControls.setCurrentText(tvCurrent);
    exVpControls.setDurationText(tvTotal);
  }

  private void setUpPlayer() {
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    ExVpFragment exVpFragment = new ExVpFragment();
    exVpControls=exVpFragment.getExVpListener();
    setUpControls();
    Bundle bundle = new Bundle();
    bundle.putStringArray("urls", video_url);
    bundle.putStringArray("type", video_type);
    exVpFragment.setArguments(bundle);
    fragmentTransaction.add(R.id.parent, exVpFragment);
    fragmentTransaction.commit();
  }

  private void init() {
    root = (LinearLayout) findViewById(com.veer.exvidplayer.R.id.root);
    ivRev = (ImageButton) findViewById(com.veer.exvidplayer.R.id.btn_rev);
    ivForword = (ImageButton) findViewById(com.veer.exvidplayer.R.id.btn_fwd);
    ivNext = (ImageButton) findViewById(com.veer.exvidplayer.R.id.btn_next);
    ivPrev = (ImageButton) findViewById(com.veer.exvidplayer.R.id.btn_prev);
    ivPlayPause = (ImageButton) findViewById(com.veer.exvidplayer.R.id.btn_pause);
    ivSetting = (ImageButton) findViewById(com.veer.exvidplayer.R.id.btn_settings);
    mProgress = (SeekBar) findViewById(com.veer.exvidplayer.R.id.seekbar);
    tvCurrent = (TextView) findViewById(com.veer.exvidplayer.R.id.txt_currentTime);
    tvTotal = (TextView) findViewById(com.veer.exvidplayer.R.id.txt_totalDuration);
  }

}
