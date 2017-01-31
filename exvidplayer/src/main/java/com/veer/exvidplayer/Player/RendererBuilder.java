package com.veer.exvidplayer.Player;

public interface RendererBuilder {
  void buildRenderers(ExVidPlayerImp player);
  void cancel();
}
