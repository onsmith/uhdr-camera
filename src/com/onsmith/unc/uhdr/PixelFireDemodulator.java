package com.onsmith.unc.uhdr;

import java.util.LinkedList;
import java.util.Deque;
import java.util.Iterator;


public class PixelFireDemodulator {
  private       int                  n;
  private final Deque<PixelFire>[][] buffer;
  private final Iterator<PixelFire>  input;
  
  
  /**
   * Constructor
   */
  @SuppressWarnings("unchecked")
  public PixelFireDemodulator(int w, int h, Iterator<PixelFire> input) {
    this.input = input;
    
    // Initialize buffer
    buffer = new Deque[w][h];
    for (int i=0; i<w; i++)
      for (int j=0; j<h; j++)
        buffer[i][j] = new LinkedList<PixelFire>();
  }
  
  
  /**
   * Iterator-like hasNext() method
   */
  public boolean hasNext() {
    return input.hasNext() || n > 0;
  }
  
  
  /**
   * Iterator-like next() method that grabs the next PixelFire object in the
   *   iterator for a given pixel
   */
  public PixelFire next(int x, int y) {
    while (buffer[x][y].isEmpty()) bufferNextPixelFire();
    n--;
    return buffer[x][y].remove();
  }
  
  
  public PixelFire peek(int x, int y) {
    while (buffer[x][y].isEmpty()) bufferNextPixelFire();
    return buffer[x][y].peek();
  }
  
  
  /**
   * Method to read a PixelFire from the wire into the buffer
   */
  private void bufferNextPixelFire() {
    PixelFire pf = input.next();
    buffer[pf.x][pf.y].add(pf);
    n++;
  }
}
