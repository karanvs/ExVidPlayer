package com.veer.exvidplayer.Player;

public interface IPlayerListener {
  void onError(String message);

  void onBufferingStarted();

  void onBuffering(String percentage);

  void onBufferingFinished();

  void onRendereingstarted();

  void onCompletion();

}
