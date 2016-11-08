package com.onsmith.unc.uhdr;

import java.io.IOException;

import com.onsmith.unc.uhdr.jcodec.AWTSequenceEncoder8Bit;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;


public class CanonicalMP4Writer implements Sink<IntFrame> {
	private final AWTSequenceEncoder8Bit[] encoders;
	private static final int BITS_PER_STREAM = 8;
	private static final int BITMASK = (0x1 << BITS_PER_STREAM) - 1;
	
	public CanonicalMP4Writer(File[] files, int fps, int q) throws IOException {
		encoders = new AWTSequenceEncoder8Bit[files.length];
		for (int i=0; i<files.length; i++) {
			encoders[i] = AWTSequenceEncoder8Bit.createSequenceEncoder8Bit(files[i], fps, q);
			encoders[i].getEncoder().setKeyInterval(fps); // 1 key frame per second
		}
	}
	
	@Override
	public void send(IntFrame frame) {
		int w = frame.getWidth(),
		    h = frame.getHeight();
		BufferedImage[] frames = new BufferedImage[encoders.length];
		WritableRaster[] rasters = new WritableRaster[encoders.length];
		for (int i=0; i<frames.length; i++) {
			frames[i]  = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
			rasters[i] = frames[i].getRaster();
		}
		for (int x=0; x<w; x++) {
			for (int y=0; y<h; y++) {
				for (int i=0; i<encoders.length; i++) {
					rasters[i].setSample(x, y, 0, (frame.getPixel(x, y) >> BITS_PER_STREAM*i) & BITMASK);
				}
			}
		}
		for (int i=0; i<encoders.length; i++) {
			try {
				encoders[i].encodeImage(frames[i]);
			} catch (IOException e) {
				System.err.println("Error encoding BufferedImage into MP4 file.");
				e.printStackTrace();
			}
		}
	}
	
	public void close() throws IOException {
		for (AWTSequenceEncoder8Bit encoder : encoders) {
			encoder.finish();
		}
	}
}
