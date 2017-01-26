package com.veer.exvidplayersample;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ProgressBar;
import com.google.android.exoplayer.AspectRatioFrameLayout;
import com.veer.exvidplayer.Player.ExVidPlayer;
import com.veer.exvidplayer.Player.Constants;
import com.veer.exvidplayer.Player.IPlayerListener;
import com.veer.exvidplayersample.Utils.GetPaths;

public class MainActivity extends AppCompatActivity {


  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

  }

}
