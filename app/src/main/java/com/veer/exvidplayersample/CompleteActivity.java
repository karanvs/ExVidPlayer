package com.veer.exvidplayersample;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.veer.exvidplayer.Player.Constants;
import com.veer.exvidplayer.VideoPlayer.ExVpCompleteFragment;

public class CompleteActivity extends AppCompatActivity {
  String[] video_url = new String[] {
      "http://playertest.longtailvideo.com/adaptive/bbbfull/bbbfull.m3u8",
      "http://cdnapi.kaltura.com/p/1878761/sp/187876100/playManifest/entryId/1_usagz19w/flavorIds/1_5spqkazq,1_nslowvhp,1_boih5aji,1_qahc37ag/format/applehttp/protocol/http/a.m3u8"
  };
  String[] video_type = new String[] {
      Constants.MEDIA_TYPE_HLS, Constants.MEDIA_TYPE_HLS
  };
  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    FragmentManager fragmentManager=getSupportFragmentManager();
    FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
    ExVpCompleteFragment exVpCompleteFragment =new ExVpCompleteFragment();
    Bundle bundle=new Bundle();
    bundle.putStringArray("urls",video_url);
    bundle.putStringArray("type",video_type);
    exVpCompleteFragment.setArguments(bundle);
    fragmentTransaction.add(R.id.parent, exVpCompleteFragment);
    fragmentTransaction.commit();

  }

}
