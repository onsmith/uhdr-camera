package com.onsmith.unc.uhdr;

import java.io.IOException;

import com.onsmith.unc.uhdr.jcodec.AWTSequenceEncoder8Bit;

import java.awt.image.BufferedImage;
import java.io.File;


public class RawMP4Writer implements Sink<BufferedImage> {
	private final AWTSequenceEncoder8Bit encoder;
	
	public RawMP4Writer(File file, int fps, int q) throws IOException {
		encoder = AWTSequenceEncoder8Bit.createSequenceEncoder8Bit(file, fps, q);
	}
	
	@Override
	public void send(BufferedImage frame) {
		try {
			encoder.encodeImage(frame);
		} catch (IOException e) {
			System.err.println("Error encoding BufferedImage into SequenceEncoder");
			e.printStackTrace();
		}
	}
	
	public void close() throws IOException {
		encoder.finish();
	}
}
