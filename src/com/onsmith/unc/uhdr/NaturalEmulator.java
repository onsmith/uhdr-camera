package com.onsmith.unc.uhdr;

import java.util.Queue;
import java.util.Iterator;
import java.util.PriorityQueue;


public class NaturalEmulator implements Iterator<PixelFire> {
  private final int                  clock; // Camera clock speed, in hertz
  private       Queue<EmulatorPixel> queue; // Decides which pixel to fire next
  
  
  /**
   * Constructor
   */
  public NaturalEmulator(int w, int h, int clock, int iD) {
    this.clock = clock;
    
    queue = new PriorityQueue<EmulatorPixel>();
    for (int x=0; x<w; x++)
      for (int y=0; y<h; y++)
        queue.add(new EmulatorPixel(x, y, iD));
  }
  
  
  /**
   * Iterator next() method
   */
  @Override
  public PixelFire next() {
    EmulatorPixel p = queue.remove();
    PixelFire pfe = new PixelFire(p.x, p.y, p.dt, p.d, p.ticks);
    p.fire();
    queue.add(p);
    return pfe;
  }
  
  
  /**
   * Iterator hasNext() method
   */
  @Override
  public boolean hasNext() {
    return true;
  }
  
  
  /**
   * Internal class representing a single pixel in the emulator. Used by the
   *   internal PriorityQueue to determine which pixel to fire next.
   */
  private class EmulatorPixel implements Comparable<EmulatorPixel> {
    public final int    x, y;  // Spatial location of the pixel
    public       int    d, dt, // Intensity value when pixel fires
                        ticks; // Clock time when pixel fires
    public       double t;     // Actual time when pixel fires
    
    public EmulatorPixel(int x, int y, int d) {
      this.x = x;
      this.y = y;
      this.d = d;
      fire();
    }
    
    public void fire() {
      double tNext = AquariumSource.nextFireTime(x, y, d, t);
      dt = (int) Math.ceil((tNext - t)*clock);
      t = tNext;
      ticks += dt;
    }
    
    // Note: Order is intentionally undefined if pixels fire at the same time
    public int compareTo(EmulatorPixel o) {
      return Double.compare(t, o.t);
    }
  }
}
