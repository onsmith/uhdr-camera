package com.onsmith.aar;

import java.io.IOException;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.util.PriorityQueue;


public class CameraEmulator implements Runnable, DataSource {
  private static final double l      = Math.pow(2, 6);   // Minimum value of the intensity function
  private static final double r      = Math.pow(2, 9);   // Maximum value of the intensity function
  private static final double T      = 0.2;              // Wave period
  private static final double tol    = Math.pow(10, -5); // Root finding algorithm tolerance
  private static final int    iD     = 4;                // Initial value of d
  
  
  // x, y, wavelength
  private static final int waves[][] = {
    {  0,   0,  5},
    { 50,  50,  5},
    { 60,  25,  5},
  };
  
  
  private int w;     // Width of camera, in pixels
  private int h;     // Height of camera, in pixels
  private int clock; // Camera clock speed, in hertz

  private Integer dt[][];  // Amount of time that it will take for each pixel to fire, in clock ticks
  private Integer D[][];   // D matrix

  private PriorityQueue<PixelFireEvent> queue; // Heap to keep track of which pixel will fire next
  
  private DataOutputStream writer; // DataOutputStream object to handle output
  
  private Thread thread; // Every CameraEmulator instance gets its own thread
  
  
  /**
   * Constructor
   */
  public CameraEmulator(int w, int h, int clock) {
    this.w     = w;
    this.h     = h;
    this.clock = clock;
    
    D     = new Integer[w][h];
    dt    = new Integer[w][h];
    queue = new PriorityQueue<PixelFireEvent>();
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
    return (r-l)*T/(4.0*Math.PI*waves.length)*waveSum  + (l+r)*(tf-ti)/2 - Math.pow(2, D[x][y]);
  }
  
  
  /**
   * Hazards an initial guess for the root of the above function
   */
  private double guess(int x, int y, double ti) {
    return Math.pow(2, D[x][y] + 1)/(l + r) + ti;
  }
  
  
  /**
   * Helper function that calculates the definite integral of a single wave
   */
  private double wave(int x, int y, double ti, double tf, int a, int b, double l, double r, int lambda, double T) {
    return Math.sin(2.0*Math.PI*(-Math.sqrt(Math.pow(a-x, 2) + Math.pow(b-y, 2))/lambda + tf/T))
         - Math.sin(2.0*Math.PI*(-Math.sqrt(Math.pow(a-x, 2) + Math.pow(b-y, 2))/lambda + ti/T));
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
    {
      double t;
      for (int i=0; i<w; i++) {
        for (int j=0; j<h; j++) {
          t = findRoot(i, j, 0);
          dt[i][j] = (int) (t*clock);
          queue.add(new PixelFireEvent(i, j, t, D[i][j]));
        }
      }
    }
    
    // Main function loop
    {
      double t;
      int x, y, d;
      PixelFireEvent pfe;
      while (true) {
        // Find pixel to fire
        pfe = queue.remove();
        x = pfe.x();
        y = pfe.y();
        t = pfe.t();
        d = pfe.d();
        
        // Fire pixel
        try {
          writer.writeInt(x);
          writer.writeInt(y);
          writer.writeInt(dt[x][y]);
          writer.writeInt(d);
        } catch (IOException e) {
          System.out.println("CameraEmulator could not write to output stream. Thread terminated.");
          stopThread();
          return;
        }
        
        // Adjust D
        //if (D[x][y] > 1 && dt[x][y] > clock/25) // nudge D if dt is slower than 25 fps
        //  D[x][y]--;
        //else if (dt[x][y] < clock/35) // nudge D if dt is faster than 35 fps
        //  D[x][y]++;
        
        // Prepare next pixel to fire
        pfe.t(findRoot(x, y, t));
        pfe.d(D[x][y]);
        queue.add(pfe);
        dt[x][y] = (int) Math.ceil((pfe.t() - t)*clock);
      }
    }
  }
  
  
  /**
   * Method to start a new thread to run the camera emulator
   */
  public void startThread() {
    stopThread();
    thread = new Thread(this);
    thread.start();
  }
  
  
  /**
   * Method to stop the current thread
   */
  public void stopThread() {
    if (thread != null) thread.interrupt();
  }
}
