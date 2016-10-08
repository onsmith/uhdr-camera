package com.onsmith.unc.uhdr;

public interface HDRScene {
	int    getWidth();
	int    getHeight();
	double getPixel(int x, int y, double t);
}
