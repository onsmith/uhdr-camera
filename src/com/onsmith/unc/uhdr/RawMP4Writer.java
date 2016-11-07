package com.onsmith.unc.uhdr;

import java.io.IOException;
import java.awt.image.BufferedImage;
import java.io.File;

import org.jcodec.api.awt.SequenceEncoder;

public class RawMP4Writer implements Sink<BufferedImage> {
	private final SequenceEncoder encoder;
	
	public RawMP4Writer(File file) throws IOException {
		encoder = new SequenceEncoder(file);
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