package com.onsmith.unc.uhdr;

import java.awt.image.BufferedImage;

public class BufferedHDRImage implements HDRImage {
	final double[][] pixels;
	final int        h, w;
	
	public BufferedHDRImage(BufferedImage image) {
		this.h = image.getHeight();
		this.w = image.getWidth();
		pixels = new double[w][h];
		for (int i=0; i<w; i++) {
			for (int j=0; j<h; j++) {
				pixels[i][j] = asDouble(image.getRGB(i, j));
			}
		}
	}
	
	@Override
	public double getPixel(int x, int y) {
		return pixels[x][y];
	}
	
	@Override
	public int getHeight() {
		return h;
	}
	
	@Override
	public int getWidth() {
		return w;
	}
	
	private static double asDouble(int rgb) {
		int r = (rgb >> 16) & 0xFF,
		    g = (rgb >>  8) & 0xFF,
		    b =  rgb        & 0xFF;
		return ((double) (r + g + b))/765;
	}
}
