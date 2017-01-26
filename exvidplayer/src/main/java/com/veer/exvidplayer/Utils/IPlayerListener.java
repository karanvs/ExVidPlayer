package com.veer.exvidplayer.Utils;

public interface IPlayerListener {
    void onError(String message);
    void onBufferingStarted();
    void onBuffering();
    void onBufferingFinished();
    void onRendereingstarted();
    void onCompletion();
}
