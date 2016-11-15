package com.onsmith.unc.uhdr;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.jcodec.api.JCodecException;
import org.jcodec.api.awt.AWTFrameGrab8Bit;
import org.jcodec.common.io.NIOUtils;

public class RawMP4Reader implements Source<BufferedImage> {
	private final AWTFrameGrab8Bit movie;
	private BufferedImage current;
	
	public RawMP4Reader(File file) throws FileNotFoundException, IOException, JCodecException {
		movie = AWTFrameGrab8Bit.createAWTFrameGrab8Bit(NIOUtils.readableChannel(file));
	}
	
	@Override
	public BufferedImage next() {
		try {
			current = movie.getFrame();
			return current;
		} catch (IOException e) {
			System.err.println("Error: could not grab frame from MP4 file.");
			e.printStackTrace();
			current = null;
		}
		return null;
	}
	
	@Override
	public BufferedImage current() {
		return current;
	}
}
