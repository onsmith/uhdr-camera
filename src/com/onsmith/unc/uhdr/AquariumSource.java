package com.onsmith.unc.uhdr;

public class AquariumSource {
  private static final double timestep = 0.005;
  
  private static final double l      = Math.pow(2, 6), // Minimum value of the intensity wave
                              r      = Math.pow(2, 9), // Maximum value of the intensity wave
                              T      = 3.2,            // Wave period
                              cx     = 10,
                              cy     = 10,
                              lambda = 10;
  
  
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
    return 0.5*(r-l)*Math.sin(2.0*Math.PI*(-Math.sqrt(Math.pow(cx-x, 2) + Math.pow(cy-y, 2))/lambda + t/T)) + 0.5*(r+l);
  }
}
