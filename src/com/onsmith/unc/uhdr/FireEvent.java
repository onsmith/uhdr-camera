package com.onsmith.unc.uhdr;


public class FireEvent implements Comparable<FireEvent> {
  public final int x, y; // Pixel's spatial location
  public       int dt,   // Last value of this pixel
                   t;    // Next time this pixel should fire
  
  public static int Q = Integer.MAX_VALUE/2;
  
  public FireEvent(int x, int y) {
    this.x  = x;
    this.y  = y;
    this.t  = 0;
    this.dt = 0;
  }
  
  public int compareTo(FireEvent o) {
    if (t == o.t) {
      if (x == o.x) return Integer.compare(y, o.y);
      else          return Integer.compare(x, o.x);
    }
    else if (t > Q && o.t < -Q || o.t > Q && t < -Q) // Check for overflow
      return Integer.compare(o.t, t);
    else
      return Integer.compare(t, o.t);
  }
}