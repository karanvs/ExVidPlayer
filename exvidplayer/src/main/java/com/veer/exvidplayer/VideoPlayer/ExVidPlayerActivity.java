package com.veer.exvidplayer.VideoPlayer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.veer.exvidplayer.R;
import java.util.ArrayList;

public class ExVidPlayerActivity extends AppCompatActivity {
  private ArrayList video_url = new ArrayList();
  private ArrayList video_type = new ArrayList();
  private int currentIndex = 0;
  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_video_player);
    getUrls();
    if(isSettingPermissionGranted())
    setUpPlayer();
  }

  private boolean isSettingPermissionGranted() {
    if (Build.VERSION.SDK_INT >= 23) {
      if (checkSelfPermission(Manifest.permission.WRITE_SETTINGS)
          == PackageManager.PERMISSION_GRANTED) {
        return true;
      } else {
        requestPermissions(new String[] {
            Manifest.permission.WRITE_SETTINGS
        }, 2909);
        return false;
      }
    } else { //permission is automatically granted on sdk<23 upon installation
      return true;
    }
  }

  private void setUpPlayer() {
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    ExVpCompleteFragment exVpCompleteFragment = new ExVpCompleteFragment();
    Bundle bundle = new Bundle();
    bundle.putStringArrayList("urls", video_url);
    bundle.putStringArrayList("type", video_type);
    bundle.putInt("currentIndex", currentIndex);
    exVpCompleteFragment.setArguments(bundle);
    fragmentTransaction.add(R.id.parent, exVpCompleteFragment);
    fragmentTransaction.commit();
  }

  private void getUrls() {
    video_url.addAll(getIntent().getStringArrayListExtra("urls"));
    video_type.addAll(getIntent().getStringArrayListExtra("types"));
    currentIndex = getIntent().getIntExtra("currentIndex",0);
  }

  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    switch (requestCode) {
      case 2909: {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          setUpPlayer();
        } else {
          //denied
          String message = "Permission needed to run the player";
          Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
          finish();

        }
        return;
      }
    }
  }
}
