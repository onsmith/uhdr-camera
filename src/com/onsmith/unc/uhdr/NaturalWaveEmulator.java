package com.onsmith.unc.uhdr;

import java.util.Queue;
import java.util.PriorityQueue;


public class NaturalWaveEmulator extends WaveEmulator {
  private final int                clock; // Camera clock speed, in hertz
  private       Queue<CameraPixel> queue; // Decides which pixel to fire next
  
  
  /**
   * Constructor
   */
  public NaturalWaveEmulator(int w, int h, int clock, int iD) {
    this.clock = clock;
    
    queue = new PriorityQueue<CameraPixel>();
    for (int x=0; x<w; x++)
      for (int y=0; y<h; y++)
        queue.add(new CameraPixel(x, y, iD));
  }
  
  
  /**
   * Iterator next() method
   */
  public PixelFire next() {
    CameraPixel p = queue.remove();
    PixelFire pfe = new PixelFire(p.x, p.y, p.dt, p.d, p.ticks);
    p.fire();
    queue.add(p);
    return pfe;
  }
  
  
  /**
   * Internal class representing a single pixel in the camera. Used by the
   *   internal PriorityQueue to determine which pixel to fire next.
   */
  private class CameraPixel implements Comparable<CameraPixel> {
    public final int    x, y;  // Spatial location of the pixel
    public       int    d, dt, // Intensity value when pixel fires
                        ticks; // Clock time when pixel fires
    public       double t;     // Actual time when pixel fires
    
    public CameraPixel(int x, int y, int d) {
      this.x = x;
      this.y = y;
      this.d = d;
      fire();
    }
    
    public void fire() {
      double tNext = findRoot(x, y, d, t);
      dt = (int) Math.ceil((tNext - t)*clock);
      t = tNext;
      ticks += dt;
    }
    
    // Note: Order is undefined if two pixels are fired at the same time
    public int compareTo(CameraPixel o) {
      return Double.compare(t, o.t);
    }
  }
}
