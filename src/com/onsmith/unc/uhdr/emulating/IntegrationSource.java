package com.onsmith.unc.uhdr.emulating;

abstract public class IntegrationSource implements Source {
  private static final double timestep = Math.pow(2, -14); // Timestep for numerical integration
  
  
  abstract protected double intensity(int x, int y, double t);
  
  
  public double nextFireTime(int x, int y, int D, double ti) {
    double sum    = 0,
           tf     = ti,
           target = (0x1 << D);
    do {
      tf  += timestep;
      sum += intensity(x, y, tf)*timestep;
    } while (sum < target);
    return tf;
  }
}
