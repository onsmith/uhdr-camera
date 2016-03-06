package com.onsmith.aar;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.PriorityQueue;


public class CameraEmulator implements Runnable {
  private static final double l      = Math.pow(2,  0);  // Minimum value of the intensity function
  private static final double r      = Math.pow(2, 12);  // Maximum value of the intensity function
  private static final double T      = 1.2;              // Wave period
  private static final double tol    = Math.pow(10, -5); // Root finding algorithm tolerance
  
  
  // x, y, wavelength, period
  private static final int waves[][] = {
    { 10,   8,  45},
    {195, 187,  64},
    {165,  40,  32},
  };
  
  
  private int w;     // Width of camera, in pixels
  private int h;     // Height of camera, in pixels
  private int clock; // Camera clock speed, in hertz

  private Integer dt[][];  // Amount of time that it will take for each pixel to fire, in clock ticks
  private Integer D[][];   // D matrix

  private PriorityQueue<PixelFireEvent> queue; // Heap to keep track of which pixel will fire next
  
  private PrintWriter  writer; // PrintWriter object to handle output
  
  
  /**
   * Intensity equation; the value of tf that makes this equation zero is the
   *   next time for the given pixel to fire
   */
  private double f(int x, int y, double ti, double tf) {
    double ret = 0;
    for (int i=0; i<waves.length; i++) {
      ret += wave(
        x, y, ti, tf,                   // x, y, ti, tf
        waves[i][0], waves[i][1],       // x, y of wave center
        l/waves.length, r/waves.length, // min intensity, max intensity
        waves[i][2], T                  // wavelength, period
      );
    }
    return ret - Math.pow(D[x][y], 2);
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
    double factor = T*(r-l)/(4.0*Math.PI);
    double waves = Math.sin(2.0*Math.PI*(-Math.sqrt(Math.pow(a-x, 2) + Math.pow(b-y, 2))/lambda + tf/T))
                 - Math.sin(2.0*Math.PI*(-Math.sqrt(Math.pow(a-x, 2) + Math.pow(b-y, 2))/lambda + ti/T));
    return factor*waves + (l + 0.5)*(tf - ti);
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
   * Method to set the output stream
   */
  public void pipeTo(OutputStream stream) {
    this.writer = new PrintWriter(stream);
  }
  
  
  /**
   * Method to run the emulator
   */
  public void run() {
    // Set D to all 5's
    for (int i=0; i<w; i++)
      for (int j=0; j<h; j++)
        D[i][j] = 5;
    
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
        writer.printf("%d %d %d %d\n", x, y, dt[x][y], d); // Ax Ay dt D
        //System.out.printf("%3d %3d %5d %2d\n", x, y, dt[x][y], d); // Ax Ay dt D
        
        // Adjust D
        if (D[x][y] > 1 && dt[x][y] > clock/25) // nudge D if dt is slower than 25 fps
          D[x][y]--;
        else if (dt[x][y] < clock/35) // nudge D if dt is faster than 35 fps
          D[x][y]++;
        
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
    (new Thread(this)).start();
  }
}
