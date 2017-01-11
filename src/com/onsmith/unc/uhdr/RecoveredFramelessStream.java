package com.onsmith.unc.uhdr;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

public class RecoveredFramelessStream implements Source<PixelFire> {
	private final Source<IntFrame> input; // Source of IntFrame objects
  private final Queue<PixelFire> queue; // Decides which pixel to fire next
	
	private final IntFrame frame; // Stores the current frame
	private int tNow; // Current time

	private static final int MAXD  = 0xF;  // Maximum allowable d
	private static final int MAXDT = 0xFF; // Maximum allowable dt
	
	
	public RecoveredFramelessStream(Source<IntFrame> input, int w, int h) {
		this.input = input;
		frame = input.next();
		
		queue = new PriorityQueue<PixelFire>(w*h, new Comparator<PixelFire>() {
      @Override
      public int compare(PixelFire a, PixelFire b) {
        return Integer.compare(a.getTFire(), b.getTFire());
      }
    });
		
		for (int x=0; x<w; x++) {
		  for (int y=0; y<h; y++) {
		    queue.add(new PixelFire(x, y));
		  }
		}
	}
	
	
	@Override
	public PixelFire next() {
		PixelFire pf = queue.remove();
		while (tNow != pf.getTFire()) {
			tNow++;
			input.next();
		}
		pf = recoverPixelFire(pf.getX(), pf.getY(), frame.getPixel(pf.getX(), pf.getY()));
    queue.add(pf);
		return pf;
	}
	
	
	@Override
	public PixelFire current() {
		return queue.peek();
	}
	
	
	private PixelFire recoverPixelFire(int x, int y, int intensity) {
	  int d  = Integer.numberOfLeadingZeros(intensity) - 9;
	  int dt = (intensity >> (MAXD - d)) & MAXDT;
	  return new PixelFire(x, y, dt, d, tNow + dt);
	}
}
