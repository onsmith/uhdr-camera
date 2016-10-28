package com.onsmith.unc.uhdr;

public class IntFrameStream implements Source<int[][]> {
	private final Source<PixelFire> input; // Source of PixelFire objects
	private final int[][] frame; // Stores the current frame
	
	private PixelFire pf; // Stores the next pixel to display
	private int tNow; // Current time

	private static final int MAXD = 0xF; // Maximum allowable d
	private static final int MAXDT = 0xFF; // Maximum allowable dt
	private static final int Q = Integer.MAX_VALUE/2; // Factor used for dealing with integer overflow
	
	
	public IntFrameStream(Source<PixelFire> input, int w, int h) {
		this.input = input;
		this.frame = new int[w][h];
		pf = input.next();
	}
	
	
	@Override
	public int[][] next() {
		tNow++;
		while (pf.getTShow() < tNow && (pf.getTShow() > -Q || tNow < Q) || pf.getTShow() > Q && tNow < -Q) {
			frame[pf.getX()][pf.getY()] = canonicalizeIntensity(pf.getD(), pf.getDt());
			pf = input.next();
		}
		return frame;
	}
	
	
	private static int canonicalizeIntensity(int d, int dt) {
		return (MAXDT & dt) << (MAXD - d);
	}
}
