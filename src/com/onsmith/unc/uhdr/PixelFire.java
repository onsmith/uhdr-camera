package com.onsmith.unc.uhdr;

public class PixelFire {
  private final int x, y,  // Spatial information
                    dt, d, // Intensity information
                    t;     // Clock time when pixel is fired
  
  
  public PixelFire(int x, int y) {
    this(x, y, 0);
  }
  public PixelFire(int x, int y, int d) {
    this(x, y, 0, d, 0);
  }
  public PixelFire(int x, int y, int dt, int d, int tFire) {
    this.x  = x;
    this.y  = y;
    this.dt = dt;
    this.d  = d;
    this.t  = tFire;
  }
  
  
  public int getX() {
    return x;
  }
  
  public int getY() {
    return y;
  }
  
  
  public int getDt() {
    return dt;
  }
  
  
  public int getD() {
    return d;
  }
  
  
  public int getTShow() {
    return t - dt;
  }
  
  public int getTFire() {
    return t;
  }
}
