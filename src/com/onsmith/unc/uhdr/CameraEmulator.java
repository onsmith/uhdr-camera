package com.onsmith.unc.uhdr;

import java.io.IOException;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.util.PriorityQueue;


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
  
  
  private final int w;     // Width of camera, in pixels
  private final int h;     // Height of camera, in pixels
  private final int clock; // Camera clock speed, in hertz
  private final int iD;    // Initial value of d
  
  private final Integer D[][]; // D matrix

  private final PriorityQueue<PixelFire> queue; // Heap to keep track of which pixel will fire next
  
  private DataOutputStream writer; // DataOutputStream object to handle output
  
  private Thread thread; // Every CameraEmulator instance gets its own thread
  
  
  /**
   * Constructor
   */
  public CameraEmulator(int w, int h, int clock) {
    this(w, h, clock, 5);
  }
    public CameraEmulator(int w, int h, int clock, int iD) {
    this.w     = w;
    this.h     = h;
    this.clock = clock;
    this.iD    = iD;
    
    D     = new Integer[w][h];
    queue = new PriorityQueue<PixelFire>();
  }
  
  
  /**
   * Intensity equation; the value of tf that makes this equation zero is the
   *   next time for the given pixel to fire
   */
  private double f(int x, int y, double ti, double tf) {
    double waveSum = 0;
    for (int i=0; i<waves.length; i++) {
      waveSum += Math.sin(2.0*Math.PI*(-Math.sqrt(Math.pow(waves[i][0]-x, 2) + Math.pow(waves[i][1]-y, 2))/waves[i][2] + tf/T))
               - Math.sin(2.0*Math.PI*(-Math.sqrt(Math.pow(waves[i][0]-x, 2) + Math.pow(waves[i][1]-y, 2))/waves[i][2] + ti/T));
    }
    return (r-l)*T/(4.0*Math.PI*waves.length)*waveSum  + (l+r)*(tf-ti)/2 - (0x1 << D[x][y]);
  }
  
  
  /**
   * Hazards an initial guess for the root of the above function
   */
  private double guess(int x, int y, double ti) {
    //return Math.pow(2, D[x][y] + 1)/(l + r) + ti;
    return (0x2 << D[x][y])/(l + r) + ti;
  }
  
  
  /**
   * Estimates the root of the intensity function. Since the function is
   *   monotonically increasing, this is a simple binary search implementation.
   */
  private double findRoot(int x, int y, double ti) {
    double l = guess(x, y, ti) - T/4;
    double r = l + T/2;
    double m, v;
    
    while (r-l > 2*tol) {
      m = (l+r)/2;
      v = f(x, y, ti, m);
      if      (v > 0) r = m;
      else if (v < 0) l = m;
      else return m;
    }
    
    return (l+r)/2;
  }
  
  
  /**
   * Method to set the output stream
   */
  public void pipeTo(OutputStream stream) {
    this.writer = new DataOutputStream(stream);
  }
  
  
  /**
   * Method to run the emulator
   */
  public void run() {
    // Initialize D matrix
    for (int i=0; i<w; i++)
      for (int j=0; j<h; j++)
        D[i][j] = iD;
    
    // Load every pixel into the queue
    queue.clear();
    for (int i=0; i<w; i++) {
      for (int j=0; j<h; j++) {
        final double t = findRoot(i, j, 0);
        queue.add(new PixelFire(i, j, t, D[i][j], (int) Math.ceil(t*clock)));
      }
    }
    
    // Main function loop
    while (true) {
      // Get next pixel to fire
      final PixelFire pfe = queue.remove();
      final double    t   = pfe.t;
      final int       x   = pfe.x,
                      y   = pfe.y,
                      d   = pfe.d,
                      dt  = pfe.dt;
      
      // Fire pixel
      try {
        writer.writeInt(x);
        writer.writeInt(y);
        writer.writeInt(dt);
        writer.writeInt(d);
      } catch (IOException e) {
        System.out.println("CameraEmulator could not write to output stream. Thread terminated.");
        stop();
        return;
      }
      
      // Reset pixel to fire again
      pfe.t  = findRoot(x, y, t);
      pfe.d  = D[x][y];
      pfe.dt = (int) Math.ceil((pfe.t - t)*clock);
      queue.add(pfe);
      
      // Adjust D
      //D[x][y] = Encoder.setNextD(dt, d, pfe.dt(), pfe.d());
    }
  }
  
  
  /**
   * Method to start a new thread to run the camera emulator
   */
  public void start() {
    stop();
    thread = new Thread(this);
    thread.start();
  }
  
  
  /**
   * Method to stop the current thread
   */
  public void stop() {
    if (thread != null) thread.interrupt();
  }
  
  
  /**
   * Internal class representing a single pixel firing at a specific time. Used
   *   by the internal PriorityQueue to determine which pixel to fire next.
   */
  private static class PixelFire implements Comparable<PixelFire> {
    public       double t;
    public       int    d, dt;
    public final int    x, y;
    
    public PixelFire(int x, int y, double t, int d, int dt) {
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
