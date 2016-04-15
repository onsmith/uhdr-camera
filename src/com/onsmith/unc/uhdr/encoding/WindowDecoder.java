package com.onsmith.unc.uhdr.encoding;

import java.util.Iterator;

import com.onsmith.unc.uhdr.PixelFire;


public class WindowDecoder implements Iterator<PixelFire> {
  private static final double timestep = 0.01; // Period width in seconds
  
  private int x, y; // Next pixel to send
  private int t;    // Clock time when the current period ends
  
  private final int               tAdvance; // Period width in clock ticks
  private final int               w, h;     // Width and height of image
  private final Iterator<Integer> input;    // Demodulates and buffers pixels
  private final PixelFire[][]     last;     // Stores the last PixelFire for each pixel

  private static final int Q = Integer.MAX_VALUE/2;
  
  
  /**
   *  Constructor
   */
  public WindowDecoder(int w, int h, int iD, int clock, Iterator<Integer> input) {
    this.w = w;
    this.h = h;
    this.input = input;
    this.tAdvance = (int) (clock*timestep);

    t = tAdvance;
    
    last = new PixelFire[w][h];
    for (int i=0; i<w; i++)
      for (int j=0; j<h; j++)
        last[i][j] = new PixelFire(i, j, iD);
  }
  
  
  /**
   * Iterator interface hasNext() method
   */
  public boolean hasNext() {
    return input.hasNext();
  }
  
  
  /**
   * Iterator interface next() method
   */
  public PixelFire next() {
    while (last[x][y].tFire > t && (t > -Q || last[x][y].tFire < Q) || last[x][y].tFire < -Q && t > Q)
      moveToNextPixel();
    
    int tFire = input.next();
    last[x][y] = new PixelFire(
        x, y,
        tFire - last[x][y].tFire,
        last[x][y].d,
        tFire
    );
    return last[x][y];
  }
  
  
  private void moveToNextPixel() {
    y++;
    if (y == h) {
      y = 0;
      x++;
      if (x == w) {
        x = 0;
        t += tAdvance;
      }
    }
  }
}
