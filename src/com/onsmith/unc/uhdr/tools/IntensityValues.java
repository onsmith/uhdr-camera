package com.onsmith.unc.uhdr.tools;

public class IntensityValues {
  private static final int DMAX  = 0xF;
  private static final int DTMAX = 0xFF;
  private static final int CLOCK = (0x1 << 10);
  
  public static void main(String[] args) {
    for (int d=0; d<=DMAX; d++) {
      System.out.printf("%10d ", d);
    }
    System.out.println("");
    
    for (int dt=1; dt<=DTMAX; dt++) {
      System.out.printf("%3d|", dt);
      for (int d=0; d<=DMAX; d++) {
        double intensity = ((double) (0x1 << d)) / (((double) dt) / CLOCK);
        System.out.printf("%10.1f ", intensity);
      }
      System.out.println("");
    }
  }
}
