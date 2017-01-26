package com.veer.exvidplayer.Controls.Brightness;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.SeekBar;

/**
 * Created by Brajendr on 1/26/2017.
 */

public class BrightnessSeekBar extends SeekBar {

  public static final int MAX_BRIGHTNESS = 255;
  public static final int MIN_BRIGHTNESS = 0;
  public final OnSeekBarChangeListener brightnessSeekListener = new OnSeekBarChangeListener() {
    @Override
    public void onProgressChanged(SeekBar seekBar, int brightness, boolean fromUser) {
      setBrightness(brightness);
      setProgress(brightness);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
  };


  public BrightnessSeekBar(Context context) {
    super(context);
    initialise();
  }

  public BrightnessSeekBar(Context context, AttributeSet attrs) {
    super(context, attrs);
    initialise();
  }

  public BrightnessSeekBar(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    initialise();
  }


  public void initialise() {
    this.setMax(MAX_BRIGHTNESS);
    this.setOnSeekBarChangeListener(brightnessSeekListener);
    manuallyUpdate(BrightnessUtils.get(getContext()));
  }

  public void setBrightness(int brightness) {
    if (brightness < MIN_BRIGHTNESS) {
      brightness = MIN_BRIGHTNESS;
    } else if (brightness > MAX_BRIGHTNESS) {
      brightness = MAX_BRIGHTNESS;
    }

    BrightnessUtils.set(getContext(), brightness);
  }

  public void manuallyUpdate(int update) {
    brightnessSeekListener.onProgressChanged(this, update, true);
  }


}