package com.veer.exvidplayer.Player;


public interface ExVidPlayerListener {
  void onError(String message);

  void onBufferingStarted();

  void onBuffering(String percentage);

  void onBufferingFinished();

  void onRendereingstarted();

  void onCompletion();

  void hideControls();

  void showControls();

  void onProgressChanged(int progress);

}
