package com.onsmith.unc.uhdr;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;


public class BinEncoder extends Encoder {
  private static final int MAX_DT = 1000000000, // Maximum possible dt value
                           NBINS  = 10;         // Number of bins to create
  
  // Calculate the bin width in ticks
  private static final int BIN_WIDTH = MAX_DT/(NBINS-1);
  
  
  /**
   * Defer to parent constructor
   */
  public BinEncoder(int w, int h) {
    super(w, h);
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
        FireEvent pfe = bins[i].remove();
        Intensity pi = nextIncoming(pfe.x, pfe.y);
        writePixel(pi.dt - pfe.dt);
        pfe.dt = pi.dt;
        pfe.t += pi.dt;
        bins[(i+pfe.t/BIN_WIDTH)%NBINS].add(pfe);
        pfe.t %= BIN_WIDTH;
      }
    }
  }
}
