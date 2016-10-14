package com.onsmith.unc.uhdr;

public class ScaledHDRImage implements HDRImage {
	private final HDRImage image;
	private final double   min, range;
	
	public ScaledHDRImage(HDRImage image, double range) {
		this(image, 0, range);
	}
	
	public ScaledHDRImage(HDRImage image, double min, double max) {
		this.min   = min;
		this.range = max - min;
		this.image = image;
	}
	
	@Override
	public int getWidth() {
		return image.getWidth();
	}
	
	@Override
	public int getHeight() {
		return image.getHeight();
	}
	
	@Override
	public double getPixel(int x, int y) {
		return scale(image.getPixel(x, y));
	}
	
	private double scale(double value) {
		return value*range + min;
	}
}
