package com.onsmith.unc.uhdr;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.jcodec.api.JCodecException;
import org.jcodec.api.awt.FrameGrab;
import org.jcodec.common.NIOUtils;

public class MP4Reader implements Source<IntFrame> {
	private static final int BITS_PER_STREAM = 8;
	private static final int BITMASK = (0x1 << BITS_PER_STREAM) - 1;
	private final FrameGrab[] movies;
	private final int w, h;
	
	public MP4Reader(File[] files, int w, int h) throws FileNotFoundException, IOException, JCodecException {
		this.w = w;
		this.h = h;
		movies = new FrameGrab[files.length];
		for (int i=0; i<files.length; i++) {
			movies[i] = new FrameGrab(NIOUtils.readableFileChannel(files[i]));
		}
	}
	
	public MP4Reader(File[] files) throws FileNotFoundException, IOException, JCodecException {
		BufferedImage frame = FrameGrab.getFrame(files[0], 0);
		this.w = frame.getWidth();
		this.h = frame.getHeight();
		
		movies = new FrameGrab[files.length];
		for (int i=0; i<files.length; i++) {
			movies[i] = new FrameGrab(NIOUtils.readableFileChannel(files[i]));
		}
	}
	
	@Override
	public IntFrame next() {
		try {
			IntFrame frame = new IntFrame(w, h);
			for (int i=0; i<movies.length; i++) {
				WritableRaster raster = movies[i].getFrame().getRaster();
				for (int x=0; x<w; x++) {
					for (int y=0; y<h; y++) {
						frame.setPixel(x, y, (frame.getPixel(x, y) << BITS_PER_STREAM) | (raster.getSample(x, y, 0) & BITMASK));
					}
				}
			}
			return frame;
		} catch (IOException e) {
			System.err.println("Error: could not grab frame from MP4 file.");
			e.printStackTrace();
		}
		return null;
	}
}
