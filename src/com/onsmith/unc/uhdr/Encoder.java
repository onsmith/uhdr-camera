package com.onsmith.unc.uhdr;

public class Encoder {
  private static final int    clock = 1000000000;
  private static final double tol   = 1;
  
  
  public static int setNextD(int t1, int d1, int t2, int d2) {
    int d = d2;
    if (Math.abs((0x1 << d1)/((double) t1/clock) - (0x1 << d2)/((double) t2/clock)) < tol)
      d++;
    else
      d--;
    
    if (d < 0)  d = 0;
    if (d > 15) d = 15;
    return d;
  }
}
