package com.onsmith.unc.uhdr;


abstract public class WaveSource {
  private static final double l   = Math.pow(2, 6);   // Minimum value of the intensity function
  private static final double r   = Math.pow(2, 9);   // Maximum value of the intensity function
  private static final double T   = 3.2;              // Wave period
  private static final double tol = Math.pow(10, -5); // Root finding algorithm tolerance
  
  
  // x, y, wavelength
  private static final int waves[][] = {
    //{  0,   0,  10},
    { 50,  50,  10},
    //{ 60,  25,  50}
  };
  
  
  /**
   * Estimates the root of the integrated intensity function. Since the function
   *   is monotonically increasing, this is a simple binary search
   *   implementation.
   */
  public static double nextFireTime(int x, int y, int D, double ti) {
    double l = guess(D, ti) - T/4,
           r = l + T/2;
    
    while (r-l > 2*tol) {
      double m = (l+r)/2,
             v = f(x, y, D, ti, m);
      if      (v > 0) r = m;
      else if (v < 0) l = m;
      else return m;
    }
    
    return (l+r)/2;
  }
  
  
  /**
   * Integrated intensity equation; the value of tf that makes this equation
   *   zero is the next time for the given pixel to fire
   */
  private static double f(int x, int y, int D, double ti, double tf) {
    double waveSum = 0;
    for (int i=0; i<waves.length; i++) {
      waveSum += Math.sin(2.0*Math.PI*(-Math.sqrt(Math.pow(waves[i][0]-x, 2) + Math.pow(waves[i][1]-y, 2))/waves[i][2] + tf/T))
               - Math.sin(2.0*Math.PI*(-Math.sqrt(Math.pow(waves[i][0]-x, 2) + Math.pow(waves[i][1]-y, 2))/waves[i][2] + ti/T));
    }
    return (r-l)*T/(4.0*Math.PI*waves.length)*waveSum  + (l+r)*(tf-ti)/2 - (0x1 << D);
  }
  
  
  /**
   * Hazards an initial guess for the root of the above function
   */
  private static double guess(int D, double ti) {
    //return Math.pow(2, D[x][y] + 1)/(l + r) + ti;
    return (0x2 << D)/(l + r) + ti;
  }
}