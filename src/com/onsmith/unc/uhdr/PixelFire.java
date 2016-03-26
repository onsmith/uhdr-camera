package com.onsmith.unc.uhdr;


public class PixelFire {
  public final int x, y,  // Spatial information
                   dt, d, // Intensity information
                   tShow, // Clock time when pixel is shown
                   tFire; // Clock time when pixel fires
  
  
  public PixelFire(int x, int y) {
    this(x, y, 0);
  }
  public PixelFire(int x, int y, int d) {
    this(x, y, 0, d, 0);
  }
  public PixelFire(int x, int y, int dt, int d, int tFire) {
    this.x     = x;
    this.y     = y;
    this.dt    = dt;
    this.d     = d;
    this.tFire = tFire;
    
    tShow = tFire - dt;
  }
}
