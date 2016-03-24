package com.onsmith.unc.uhdr;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;


public class BinDecoder extends Decoder {
  private static final int MAX_DT = 1000000000, // Maximum possible dt value
                           NBINS  = 10;         // Number of bins to create
  
  // Calculate the bin width in ticks
  private static final int BIN_WIDTH = MAX_DT/(NBINS-1);
  
  
  /**
   * Defer to parent constructor
   */
  public BinDecoder(int w, int h, int iD) {
    super(w, h, iD);
  }
  
  
  @Override
  public void run() {
    // Create bin buffer
    @SuppressWarnings("unchecked")
    Queue<FireEvent>[] bins = new ArrayBlockingQueue[NBINS];
    for (int i=0; i<NBINS; i++)
      bins[i] = new ArrayBlockingQueue<FireEvent>(w*h);
    
    // Fill bin buffer
    for (int i=0; i<w; i++)
      for (int j=0; j<h; j++)
        bins[0].add(new FireEvent(i, j));
    
    // Main encoding loop
    for (int i=0; true; i=(i+1)%NBINS) {
      while (bins[i].size() > 0) {
        FireEvent pfe = bins[i].remove();         // Remove pixel from queue
        pfe.dt += readNextPixel();                // Read pixel value change from wire
        writePixel(pfe.x, pfe.y, pfe.dt, iD);     // Write to wire
        pfe.t += pfe.dt;                          // Update t
        bins[(i+pfe.t/BIN_WIDTH)%NBINS].add(pfe); // Add pixel to the correct bin
        pfe.t %= BIN_WIDTH;                       // Update t to prevent overflow
      }
    }
  }

}
