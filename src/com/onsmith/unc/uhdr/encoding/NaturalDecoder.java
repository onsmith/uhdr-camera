package com.onsmith.unc.uhdr.encoding;

import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Queue;

import com.onsmith.unc.uhdr.CameraOrder;
import com.onsmith.unc.uhdr.PixelFire;


public class NaturalDecoder implements Iterator<PixelFire> {
  private final Iterator<Integer> input; // Input stream of integers
  private final Queue<PixelFire>  queue; // Scheduler
  
  
  /**
   *  Constructor
   */
  public NaturalDecoder(int w, int h, int iD, Iterator<Integer> input) {
    this.input = input;
    
    // Initialize and fill scheduler
    queue = new PriorityQueue<PixelFire>(w*h, new CameraOrder());
    for (int i=0; i<w; i++)
      for (int j=0; j<h; j++)
        queue.add(new PixelFire(i, j, iD));
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
    PixelFire pf = queue.remove();
    int dt = input.next();
    pf = new PixelFire(
        pf.x, pf.y,
        dt,
        pf.d,
        pf.tFire+dt
    );
    queue.add(pf);
    return pf;
  }
}
