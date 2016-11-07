package com.onsmith.unc.uhdr;

public class IntFrame {
	private final int[][] frame;
	
	public IntFrame(int w, int h) {
		this(new int[w][h]);
	}
	
	public IntFrame(int[][] frame) {
		this.frame = frame;
	}
	
	public void setPixel(int x, int y, int val) {
		frame[x][y] = val;
	}
	
	public int getPixel(int x, int y) {
		return frame[x][y];
	}
	
	public int getWidth() {
		return frame.length;
	}
	
	public int getHeight() {
		return frame[0].length;
	}
	
	public int[][] getFrame() {
		return frame;
	}
}
