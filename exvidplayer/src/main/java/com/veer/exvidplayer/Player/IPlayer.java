package com.veer.exvidplayer.Player;

import android.content.Context;
import android.view.View;

public interface IPlayer {
    void setSource( String[] url, String[] vtype);
    void reset();
    void release();
    void setListener(IPlayerListener listener);
    void setQuality(View v);
    void nextTrack();
    void previousTrack();
}

