package com.onsmith.unc.uhdr.emulating;

import java.awt.image.BufferedImage;


public class AquariumIntegrationSource extends IntegrationSource {
  private static final Sprite[]      sprites    = {};
  private static final BufferedImage background = null;
  
  
  protected double intensity(int x, int y, double t) {
    int intensity = background.getRGB(x, y);
    for (Sprite sprite : sprites)
      intensity = sprite.intensity(x, y, t, intensity);
    return intensity;
  }
}
