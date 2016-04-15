package com.onsmith.unc.uhdr.emulating;

public class WaveIntegrationSource extends IntegrationSource {
  private static final double l      = Math.pow(2, 6), // Minimum value of the intensity wave
                              r      = Math.pow(2, 9), // Maximum value of the intensity wave
                              T      = 3.2,            // Wave period
                              cx     = 10,             // Wave center x coordinate
                              cy     = 10,             // Wave center y coordinate
                              lambda = 10;             // Wavelength
  
  
  protected double intensity(int x, int y, double t) {
    return 0.5*(r-l)*Math.sin(2.0*Math.PI*(-Math.sqrt(Math.pow(cx-x, 2) + Math.pow(cy-y, 2))/lambda + t/T)) + 0.5*(r+l);
  }
}
