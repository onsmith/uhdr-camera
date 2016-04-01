package com.onsmith.unc.uhdr;

import java.awt.image.BufferedImage;


public class AquariumSource {
  private static final Sprite[]      sprites    = {};
  private static final BufferedImage background = null;
  
  private static final double timestep = 0.005; // Timestep for numerical integration
  
  
  public static double nextFireTime(int x, int y, int D, double ti) {
    double sum    = 0,
           tf     = ti,
           target = (0x1 << D);
    do {
      tf  += timestep;
      sum += intensity(x, y, tf)*timestep;
    } while (sum < target);
    return tf;
  }
  
  
  private static double intensity(int x, int y, double t) {
    int intensity = background.getRGB(x, y);
    for (Sprite sprite : sprites)
      intensity = sprite.intensity(x, y, t, intensity);
    return intensity;
  }
}
