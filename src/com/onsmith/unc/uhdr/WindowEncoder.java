package com.onsmith.unc.uhdr;

import java.util.Iterator;


public class WindowEncoder implements Iterator<Integer> {
  private static final double timestep = 0.01; // Frame width in seconds
  
  private int x, y; // Next pixel to send
  private int t;    // Clock time when the current period ends
  
  private final int                  tAdvance; // Frame width in clock ticks
  private final int                  w, h;     // Width and height of image
  private final PixelFireDemodulator buffer;   // Demodulates and buffers pixels
  
  private static final int Q = Integer.MAX_VALUE/2;
  
  
  /**
   *  Constructor
   */
  public WindowEncoder(int w, int h, int clock, Iterator<PixelFire> input) {
    this.w = w;
    this.h = h;
    this.tAdvance = (int) (clock*timestep);
    
    t = tAdvance;
    buffer = new PixelFireDemodulator(w, h, input);
  }
  
  
  /**
   * Iterator interface hasNext() method
   */
  public boolean hasNext() {
    return buffer.hasNext();
  }
  
  
  /**
   * Iterator interface next() method
   */
  public Integer next() {
    while (buffer.peek(x, y).tShow > t && (t > -Q || buffer.peek(x, y).tShow < Q) || buffer.peek(x, y).tShow < -Q && t > Q)
      moveToNextPixel();
    return buffer.next(x, y).tFire;
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
