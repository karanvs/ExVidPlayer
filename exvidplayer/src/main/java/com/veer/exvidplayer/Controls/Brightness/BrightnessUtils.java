package com.veer.exvidplayer.Controls.Brightness;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;

public class BrightnessUtils {

    public static void set(Context context, int brightness){
        ContentResolver cResolver = context.getContentResolver();
        Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS, brightness);
    }

    public static int get(Context context) {
        ContentResolver cResolver = context.getContentResolver();
        try {
            return Settings.System.getInt(cResolver, Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            return 0;
        }
    }
}