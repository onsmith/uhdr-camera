package com.onsmith.unc.uhdr;

import java.util.Comparator;


public class CameraOrder implements Comparator<PixelFire> {
  private static final int Q = Integer.MAX_VALUE/2;
  
  
  @Override
  public int compare(PixelFire a, PixelFire b) {
    int comp = Integer.compare(a.tFire, b.tFire);
    if (a.tFire > Q && b.tFire < -Q || b.tFire > Q && a.tFire < -Q) return -comp; // overflow
    if (comp == 0) comp = Integer.compare(a.x, b.x);
    if (comp == 0) comp = Integer.compare(a.y, b.y);
    return comp;
  }
}
