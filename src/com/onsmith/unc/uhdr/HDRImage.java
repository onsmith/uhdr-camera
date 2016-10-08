package com.onsmith.unc.uhdr;

public interface HDRImage {
	int    getWidth();
	int    getHeight();
	double getPixel(int x, int y);
}
