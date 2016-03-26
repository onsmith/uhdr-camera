package com.onsmith.unc.uhdr;

import java.util.Comparator;


public class PlayerOrder implements Comparator<PixelFire> {
  private static final int Q = Integer.MAX_VALUE/2;
  
  
  @Override
  public int compare(PixelFire a, PixelFire b) {
    int comp = Integer.compare(a.tShow, b.tShow);
    if (a.tShow > Q && b.tShow < -Q || b.tShow > Q && a.tShow < -Q) return -comp; // overflow
    if (comp == 0) comp = Integer.compare(a.x, b.x);
    if (comp == 0) comp = Integer.compare(a.y, b.y);
    return comp;
  }
}
