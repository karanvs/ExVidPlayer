package com.veer.exvidplayer.Utils;

import android.content.Context;

public interface IPlayer {
    void setSource(Context context, String[] url, String[] vtype);
    void reset();
    void release();
    void setListener(IPlayerListener listener);
}

