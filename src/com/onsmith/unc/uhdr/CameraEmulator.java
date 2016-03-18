package com.onsmith.unc.uhdr;

import java.io.IOException;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.util.PriorityQueue;
import java.util.Queue;


public class CameraEmulator implements Runnable, DataSource {
  private static final double l      = Math.pow(2, 6);   // Minimum value of the intensity function
  private static final double r      = Math.pow(2, 9);   // Maximum value of the intensity function
  private static final double T      = 3.1;              // Wave period
  private static final double tol    = Math.pow(10, -5); // Root finding algorithm tolerance
  
  
  // x, y, wavelength
  private static final int waves[][] = {
    //{  0,   0,  10},
    { 50,  50,  10},
    //{ 60,  25,  50}
  };
  
  
  private final int xOffset, // Window x offset, in pixels
                    yOffset; // Window y offset, in pixels
  
  private final int w,     // Width of camera, in pixels
                    h,     // Height of camera, in pixels
                    clock, // Camera clock speed, in hertz
                    iD;    // Initial value of d
  
  private DataOutputStream writer; // DataOutputStream object to handle output
  
  private Thread thread; // Every CameraEmulator instance gets its own thread
  
  
  /**
   * Constructor
   */
  public CameraEmulator(int w, int h, int clock) {
    this(w, h, clock, 5); // Default iD
  }
  public CameraEmulator(int w, int h, int clock, int iD) {
    this(w, h, clock, iD, 0, 0); // Default xOffset, yOffset
  }
  public CameraEmulator(int w, int h, int clock, int iD, int xOffset, int yOffset) {
    this.w       = w;
    this.h       = h;
    this.clock   = clock;
    this.iD      = iD;
    this.xOffset = xOffset;
    this.yOffset = yOffset;
  }
  
  
  /**
   * Public method to start a new thread for the camera emulator
   */
  public void start() {
    stop();
    thread = new Thread(this);
    thread.start();
  }
  
  
  /**
   * Public method to stop the current thread
   */
  public void stop() {
    if (thread != null) thread.interrupt();
  }
  
  
  /**
   * Public method to set the output stream
   */
  public void pipeTo(OutputStream stream) {
    this.writer = new DataOutputStream(stream);
  }
  
  
  /**
   * Method to run the emulator
   */
  public void run() {
    // Queue to keep track of which pixel to write next
    Queue<PixelFire >queue = new PriorityQueue<PixelFire>();
    
    // Load every pixel into the queue
    for (int i=0; i<w; i++) {
      for (int j=0; j<h; j++) {
        double t = findRoot(i+xOffset, j+yOffset, iD, 0);
        queue.add(new PixelFire(i, j, (int) Math.ceil(t*clock), iD, t));
      }
    }
    
    // Main function loop
    while (true) {
      // Get next pixel to write
      PixelFire pfe = queue.remove();
      
      // Write pixel
      writePixel(pfe);
      
      // This is where D would be updated
      
      // Reset pixel to fire again
      double t = findRoot(pfe.x+xOffset, pfe.y+yOffset, pfe.d, pfe.t);
      pfe.dt = (int) Math.ceil((t - pfe.t)*clock);
      pfe.t  = t;
      queue.add(pfe);
    }
  }
  
  
  /**
   * Method to write a pixel to the wire
   */
  private void writePixel(PixelFire pfe) {
    try {
      writer.writeInt(pfe.x);
      writer.writeInt(pfe.y);
      writer.writeInt(pfe.dt);
      writer.writeInt(pfe.d);
    } catch (IOException e) {
      System.out.println("CameraEmulator could not write to output stream. Thread terminated.");
      this.stop();
    }
  }
  
  
  /**
   * Intensity equation; the value of tf that makes this equation zero is the
   *   next time for the given pixel to fire
   */
  private static double f(int x, int y, int D, double ti, double tf) {
    double waveSum = 0;
    for (int i=0; i<waves.length; i++) {
      waveSum += Math.sin(2.0*Math.PI*(-Math.sqrt(Math.pow(waves[i][0]-x, 2) + Math.pow(waves[i][1]-y, 2))/waves[i][2] + tf/T))
               - Math.sin(2.0*Math.PI*(-Math.sqrt(Math.pow(waves[i][0]-x, 2) + Math.pow(waves[i][1]-y, 2))/waves[i][2] + ti/T));
    }
    return (r-l)*T/(4.0*Math.PI*waves.length)*waveSum  + (l+r)*(tf-ti)/2 - (0x1 << D);
  }
  
  
  /**
   * Hazards an initial guess for the root of the above function
   */
  private static double guess(int D, double ti) {
    //return Math.pow(2, D[x][y] + 1)/(l + r) + ti;
    return (0x2 << D)/(l + r) + ti;
  }
  
  
  /**
   * Estimates the root of the intensity function. Since the function is
   *   monotonically increasing, this is a simple binary search implementation.
   */
  private static double findRoot(int x, int y, int D, double ti) {
    double l = guess(D, ti) - T/4,
           r = l + T/2;
    
    while (r-l > 2*tol) {
      double m = (l+r)/2,
             v = f(x, y, D, ti, m);
      if      (v > 0) r = m;
      else if (v < 0) l = m;
      else return m;
    }
    
    return (l+r)/2;
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
    
    public int compareTo(PixelFire o) {
      return Double.compare(t, o.t);
    }
  }
}