package com.veer.exvidplayer.VideoPlayer;

import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by Brajendr on 2/3/2017.
 */

public interface ExVpListener {
  void setControlLayout(ViewGroup viewGroup);

  void play();

  void stop();

  void forward();

  void reverse();

  void nextTrack();

  void previousTrack();

  void setProgressBar(SeekBar seekBar);

  void setDurationText(TextView textView);

  void setCurrentText(TextView te);

  void changeQuality(View v);

  void seekToProgress(int progress);
}
