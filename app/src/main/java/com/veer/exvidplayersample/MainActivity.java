package com.veer.exvidplayersample;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ProgressBar;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.TrackRenderer;
import com.google.android.exoplayer.util.PlayerControl;
import com.veer.exvidplayer.ExVidPlayer;
import com.veer.exvidplayer.Utils.IPlayerListener;
import com.veer.exvidplayer.Utils.RendererBuilder;
import com.veer.exvidplayersample.Utils.GetPaths;

public class MainActivity extends AppCompatActivity {
  private ProgressBar progressBar;
  private SurfaceView surfaceView;
  private ExVidPlayer exVidPlayer;

  private int PICK_VIDEO = 1;
  String[] video_url = new String[] {
      "http://playertest.longtailvideo.com/adaptive/bbbfull/bbbfull.m3u8",
  };
  String[] video_type = new String[] { "hls" };
  private IPlayerListener iPlayerListener = new IPlayerListener() {
    @Override public void onError(String message) {

    }

    @Override public void onBufferingStarted() {
      progressBar.setVisibility(View.VISIBLE);
    }

    @Override public void onBuffering() {

    }

    @Override public void onBufferingFinished() {
      progressBar.setVisibility(View.GONE);
    }

    @Override public void onRendereingstarted() {

    }

    @Override public void onCompletion() {

    }
  };

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    progressBar = (ProgressBar) findViewById(R.id.pbar);
    surfaceView = (SurfaceView) findViewById(R.id.surface_view);
    findViewById(R.id.playLocal).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        if (exVidPlayer != null) {
          exVidPlayer.release();
        }
        requestVideo();
      }
    });
    findViewById(R.id.playOnline).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        if (exVidPlayer != null) {
          exVidPlayer.release();
        }
        setExoPlayer(video_url, video_type);
      }
    });
  }

  private void requestVideo() {
    Intent intent = new Intent(Intent.ACTION_PICK);
    intent.setType("video/*");

    intent.setAction(Intent.ACTION_GET_CONTENT);
    startActivityForResult(Intent.createChooser(intent, "Select Video"), PICK_VIDEO);
  }

  private void setExoPlayer(String[] urls, String[] type) {
    exVidPlayer = new ExVidPlayer(this, surfaceView, new Handler());
    exVidPlayer.setListener(iPlayerListener);
    exVidPlayer.setSource(this, urls, type);
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode == RESULT_OK) {
      if (requestCode == PICK_VIDEO) {
        Uri selectedImageUri = data.getData();

        // OI FILE Manager
        String filemanagerstring = selectedImageUri.getPath();

        // MEDIA GALLERY
        String selectedImagePath = GetPaths.getPath(MainActivity.this, selectedImageUri);

        if (selectedImagePath != null) {
          String[] type = { "others" };
          String[] uri = { selectedImagePath };
          setExoPlayer(uri, type);
        }
      }
    }
  }

  @Override protected void onDestroy() {
    if (exVidPlayer != null) exVidPlayer.release();
    super.onDestroy();
  }
}
