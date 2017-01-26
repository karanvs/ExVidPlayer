package com.veer.exvidplayer.Player;

import com.veer.exvidplayer.Player.ExVidPlayer;

public interface RendererBuilder {
  void buildRenderers(ExVidPlayer player);
  void cancel();
}
