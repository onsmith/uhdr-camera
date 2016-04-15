package com.onsmith.unc.uhdr.encoding;

import java.util.Iterator;
import java.util.Queue;

import com.onsmith.unc.uhdr.CameraOrder;
import com.onsmith.unc.uhdr.PixelFire;
import com.onsmith.unc.uhdr.PixelFireDemodulator;

import java.util.PriorityQueue;


public class NaturalEncoder implements Iterator<Integer> {
  private final Queue<PixelFire>     queue;  // Schedules pixels
  private final PixelFireDemodulator buffer; // Demodulates and buffers pixels
  
  
  /**
   *  Constructor
   */
  public NaturalEncoder(int w, int h, Iterator<PixelFire> input) {
    // Initialize buffer
    buffer = new PixelFireDemodulator(w, h, input);
    
    // Initialize and fill scheduler
    queue = new PriorityQueue<PixelFire>(w*h, new CameraOrder());
    for (int i=0; i<w; i++)
      for (int j=0; j<h; j++)
        queue.add(new PixelFire(i, j));
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
    PixelFire pf = queue.remove(); // Which pixel should go next?
    pf = buffer.next(pf.x, pf.y);  // Okay, get the next value for that pixel
    queue.add(pf);                 // Add back to scheduler
    return pf.dt;                  // Return the new dt value
  }
  
  
  /*
  private static final int clock = 1000000000;
  private static final double tol   = 6;
  public static int setNextD(int t1, int d1, int t2, int d2) {
    int d = d2;
    if (Math.abs((0x1 << d1)/((double) t1/clock) - (0x1 << d2)/((double) t2/clock)) < tol)
      d++;
    else
      d--;
    
    if (d < 0)  d = 0;
    if (d > 15) d = 15;
    
    System.out.println(d);
    return d;
  }
  */
}
