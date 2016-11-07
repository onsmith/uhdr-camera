package com.onsmith.unc.uhdr;


public class UhdrFrameStream implements Source<IntFrame> {
	private final Source<PixelFire> input; // Source of PixelFire objects
	private final IntFrame frame; // Stores the current frame
	
	private PixelFire pf; // Stores the next pixel to display
	private int tNow; // Current time

	private static final int MAXD  = 0xF;  // Maximum allowable d
	private static final int MAXDT = 0xFF; // Maximum allowable dt
	private static final int Q = Integer.MAX_VALUE/2; // Factor used for dealing with integer overflow
	
	
	public UhdrFrameStream(Source<PixelFire> input, int w, int h) {
		this.input = input;
		this.frame = new IntFrame(w, h);
		pf = input.next();
	}
	
	
	@Override
	public IntFrame next() {
		tNow++;
		while (pf.getTShow() < tNow && (pf.getTShow() > -Q || tNow < Q) || pf.getTShow() > Q && tNow < -Q) {
			frame.setPixel(pf.getX(), pf.getY(), canonicalizeIntensity(pf.getD(), pf.getDt()));
			pf = input.next();
		}
		return frame;
	}
	
	
	private static int canonicalizeIntensity(int d, int dt) {
		return (MAXDT & dt) << (MAXD - d);
	}
}
