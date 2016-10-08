package com.onsmith.unc.uhdr;

public class AquariumScene implements HDRScene {
	private HDRImage bg;
	private Sprite[] sprites;
	
	public AquariumScene(HDRImage bg, Sprite[] sprites) {
		this.bg      = bg;
		this.sprites = sprites;
	}
	
	@Override
	public int getWidth() {
		return bg.getWidth();
	}
	
	@Override
	public int getHeight() {
		return bg.getHeight();
	}
	
	@Override
	public double getPixel(int x, int y, double t) {
		double val = bg.getPixel(x, y);
		for (Sprite sprite : sprites) {
			val = sprite.getPixel(x, y, t, val);
		}
		return val;
	}
}
