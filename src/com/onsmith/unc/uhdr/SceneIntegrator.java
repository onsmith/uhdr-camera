package com.onsmith.unc.uhdr;

import java.util.Queue;
import java.util.PriorityQueue;

public class SceneIntegrator implements Source<PixelFire> {
  private        final int                  clock;            // Camera clock speed, in hertz
	private        final HDRScene             scene;
	private        final Queue<EmulatorPixel> queue;            // Decides which pixel to fire next

	private static final double stepsPerTick = 4;                        // Number of substeps for numerical integration
	private static final int    MAXD         = (int) Math.pow(2, 4) - 1; // Maximum allowable d
	private static final int    MAXDT        = (int) Math.pow(2, 8) - 1; // Maximum allowable dt
	
	private final double maxDtInSecs;
	private final double timestep;
	
	public SceneIntegrator(int clock, HDRScene scene) {
		this.clock = clock;
		this.scene = scene;
		this.queue = new PriorityQueue<EmulatorPixel>();
		
		int w = scene.getWidth(),
		    h = scene.getHeight();
		
		maxDtInSecs = ((double) MAXDT)/clock;
		timestep    = 1.0/clock/stepsPerTick;
		
		for (int x=0; x<w; x++) {
			for (int y=0; y<h; y++) {
				queue.add(new EmulatorPixel(x, y, 0));
			}
		}
	}
	
	@Override
	public PixelFire next() {
		EmulatorPixel p = queue.remove();
		PixelFire pf = new PixelFire(p.x, p.y, p.dt, p.d, p.ticks);
		p.fire();
		queue.add(p);
		return pf;
	}
	
	private int nextD(int x, int y, double ti) {
		double iI = scene.getPixel(x, y, ti),
		       fI = scene.getPixel(x, y, ti + maxDtInSecs);
		if (iI == fI) {
			return (int) Math.min(MAXD, Math.floor(Math.log(iI*maxDtInSecs)/Math.log(2)));
		}
		return 5;
		//return (int) Math.max(0, Math.floor(Math.log(iI/clock)/Math.log(2)));
	}
	
	private double nextFireTime(int x, int y, int D, double ti) {
		double sum    = 0,
		       tf     = ti,
		       target = (0x1 << D);
		for (int i=0; i<MAXDT*stepsPerTick; i++) {
			tf  += timestep;
			sum += scene.getPixel(x, y, tf)*timestep;
			if (sum >= target) break;
		}
		return tf;
	}
	
	/**
	 * Internal class representing a single pixel in the emulator. Used by the
	 *   internal PriorityQueue to determine which pixel to fire next.
	 */
	private class EmulatorPixel implements Comparable<EmulatorPixel> {
		public final int    x, y;  // Spatial location of the pixel
		public       int    d, dt, // Intensity value when pixel fires
		                    ticks; // Clock time when pixel fires
		public       double t;     // Actual time when pixel fires
		
		public EmulatorPixel(int x, int y, int d) {
			this.x = x;
			this.y = y;
			this.d = d;
			fire();
		}
		
		public void fire() {
			d = nextD(x, y, t);
			double tNext = nextFireTime(x, y, d, t);
			dt = (int) Math.ceil((tNext - t)*clock);
			t  = tNext;
			ticks += dt;
			
			//System.out.println("(x, y, d, dt) = (" + x + ", " + y + ", " + d + ", " + dt + ")");
		}
		
		// Note: Order is intentionally undefined if pixels fire at the same time
		public int compareTo(EmulatorPixel o) {
			return Double.compare(t, o.t);
		}
  }
}
