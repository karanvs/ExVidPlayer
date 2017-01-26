package com.veer.exvidplayer.Utils;

import com.veer.exvidplayer.ExVidPlayer;

public interface RendererBuilder {
  void buildRenderers(ExVidPlayer player);
  void cancel();
}
