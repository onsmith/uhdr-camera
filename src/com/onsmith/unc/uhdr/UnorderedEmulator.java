package com.onsmith.unc.uhdr;


public class UnorderedEmulator extends Emulator {
  private static final double timestep = 0.1; // Number of seconds to advance for each iteration
  
  private final int w,     // Width of camera, in pixels
                    h,     // Height of camera, in pixels
                    clock, // Camera clock speed, in hertz
                    iD;    // Initial value of d
  
  
  /**
   * Constructor
   */
  public UnorderedEmulator(int w, int h, int clock) {
    this(w, h, clock, 5); // Default iD
  }
  public UnorderedEmulator(int w, int h, int clock, int iD) {
    this.w       = w;
    this.h       = h;
    this.clock   = clock;
    this.iD      = iD;
  }
  
  
  /**
   * Run() implements emulator algorithm
   */
  public void run() {
    double[][] tLast = new double[w][h]; // Keeps track of the last time each pixel fired
    double     tNow  = 0;                // Keeps track of the current time
    
    // Main function loop
    while (true) {
      // Advance current time
      tNow += timestep;
      
      // Loop through pixels and write any pixel that fired
      for (int x=0; x<w; x++) {
        for (int y=0; y<h; y++) {
          while (tLast[x][y] < tNow) {
            double t = findRoot(x, y, iD, tLast[x][y]); 
            writePixel(x, y, (int) Math.ceil((t - tLast[x][y])*clock), iD);
            tLast[x][y] = t;
          }
        }
      }
    }
  }
}