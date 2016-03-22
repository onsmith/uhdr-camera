package com.onsmith.unc.uhdr;

import java.util.Queue;
import java.util.PriorityQueue;


public class OrderedEmulator extends Emulator {
  private final int w,     // Width of camera, in pixels
                    h,     // Height of camera, in pixels
                    clock, // Camera clock speed, in hertz
                    iD;    // Initial value of d
  
  
  /**
   * Constructor
   */
  public OrderedEmulator(int w, int h, int clock) {
    this(w, h, clock, 5); // Default iD
  }
  public OrderedEmulator(int w, int h, int clock, int iD) {
    this.w       = w;
    this.h       = h;
    this.clock   = clock;
    this.iD      = iD;
  }
  
  
  /**
   * Run function implements emulator algorithm
   */
  public void run() {
    // Queue to keep track of which pixel to write next
    Queue<PixelFire>queue = new PriorityQueue<PixelFire>();
    
    // Load every pixel into the queue
    for (int i=0; i<w; i++) {
      for (int j=0; j<h; j++) {
        double t = findRoot(i, j, iD, 0);
        queue.add(new PixelFire(i, j, (int) Math.ceil(t*clock), iD, t));
      }
    }
    
    // Main function loop
    while (true) {
      PixelFire pfe = queue.remove();                  // Get next pixel to fire
      writePixel(pfe.x, pfe.y, pfe.dt, pfe.d);         // Write pixel to output stream
                                                       // TODO: Update D
      double t = findRoot(pfe.x, pfe.y, pfe.d, pfe.t); // Calculate next time to fire
      pfe.dt = (int) Math.ceil((t - pfe.t)*clock);     // Update dt in data structure
      pfe.t  = t;                                      // Update t in data structure
      queue.add(pfe);                                  // Re-queue data structure
    }
  }
  
  
  /**
   * Internal class representing a single pixel firing at a specific time. Used
   *   by the internal PriorityQueue to determine which pixel to fire next.
   */
  private static class PixelFire implements Comparable<PixelFire> {
    public final int    x, y;  // Spatial location of the pixel
    public       int    d, dt; // Intensity value of the pixel when it fires
    public       double t;     // Time at which the pixel should fire
    
    public PixelFire(int x, int y, int dt, int d, double t) {
      this.x  = x;
      this.y  = y;
      this.t  = t;
      this.d  = d;
      this.dt = dt;
    }
    
    // Note: Order is undefined if two pixels are fired at the same time
    public int compareTo(PixelFire o) {
      return Double.compare(t, o.t);
    }
  }
}
