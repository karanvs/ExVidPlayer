package com.veer.exvidplayer.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.ProgressBar;

/**
 * Created by Brajendr on 1/31/2017.
 */

public class VolBar extends ProgressBar{
  private AudioManager audioManager;
  private int MAX_VOLUME=0;
  private int MIN_VOLUME=0;
  private BroadcastReceiver volumeReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      updateVolumeProgress();
    }
  };

  public VolBar(Context context) {
    super(context);
    initialise();
  }

  public VolBar(Context context, AttributeSet attrs) {
    super(context, attrs);
    initialise();
  }

  public VolBar(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initialise();
  }

  @Override
  public void onInitializeAccessibilityEvent(final AccessibilityEvent event) {
    super.onInitializeAccessibilityEvent(event);
    event.setClassName(VolBar.class.getName());
  }

  @Override
  public void onInitializeAccessibilityNodeInfo(final AccessibilityNodeInfo info) {
    super.onInitializeAccessibilityNodeInfo(info);
    info.setClassName(VolBar.class.getName());
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    registerVolumeReceiver();
  }

  @Override
  protected void onDetachedFromWindow() {
    unregisterVolumeReceiver();
    super.onDetachedFromWindow();
  }

  public void initialise() {
    this.audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
    this.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
    this.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
    MAX_VOLUME=getMax();
  }

  private void updateVolumeProgress() {
    this.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
  }

  private void registerVolumeReceiver() {
    getContext().registerReceiver(volumeReceiver, new IntentFilter("android.media.VOLUME_CHANGED_ACTION"));
  }

  private void unregisterVolumeReceiver() {
    getContext().unregisterReceiver(volumeReceiver);
  }

  @Override public synchronized void setProgress(int progress) {
    super.setProgress(progress);
    try {
      audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
    }
    catch (Exception e)
    {
      Log.e("error","once");
    }
  }

  public int getMAX_VOLUME() {
    return MAX_VOLUME;
  }
}
