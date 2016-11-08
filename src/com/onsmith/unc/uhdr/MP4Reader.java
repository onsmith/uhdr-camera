package com.onsmith.unc.uhdr;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.jcodec.api.JCodecException;
import org.jcodec.api.awt.AWTFrameGrab8Bit;
import org.jcodec.common.io.NIOUtils;

public class MP4Reader implements Source<IntFrame> {
	private static final int BITS_PER_STREAM = 8;
	private static final int BITMASK = (0x1 << BITS_PER_STREAM) - 1;
	private final AWTFrameGrab8Bit[] movies;
	private final int w, h;
	private IntFrame current;
	
	public MP4Reader(File[] files, int w, int h) throws FileNotFoundException, IOException, JCodecException {
		this.w = w;
		this.h = h;
		movies = new AWTFrameGrab8Bit[files.length];
		for (int i=0; i<files.length; i++) {
			movies[i] = AWTFrameGrab8Bit.createAWTFrameGrab8Bit(NIOUtils.readableChannel(files[i]));
		}
	}
	
	public MP4Reader(File[] files) throws FileNotFoundException, IOException, JCodecException {
		BufferedImage frame = AWTFrameGrab8Bit.getFrame(files[0], 0);
		this.w = frame.getWidth();
		this.h = frame.getHeight();
		
		movies = new AWTFrameGrab8Bit[files.length];
		for (int i=0; i<files.length; i++) {
			movies[i] = AWTFrameGrab8Bit.createAWTFrameGrab8Bit(NIOUtils.readableChannel(files[i]));
		}
	}
	
	@Override
	public IntFrame next() {
		try {
			current = new IntFrame(w, h);
			for (int i=0; i<movies.length; i++) {
				Raster raster = movies[i].getFrame().getData();
				for (int x=0; x<w; x++) {
					for (int y=0; y<h; y++) {
						current.setPixel(x, y, (current.getPixel(x, y) << BITS_PER_STREAM) | (raster.getSample(x, y, 0) & BITMASK));
					}
				}
			}
			return current;
		} catch (IOException e) {
			System.err.println("Error: could not grab frame from MP4 file.");
			e.printStackTrace();
			current = null;
		}
		return null;
	}
	
	@Override
	public IntFrame current() {
		return current;
	}
}
