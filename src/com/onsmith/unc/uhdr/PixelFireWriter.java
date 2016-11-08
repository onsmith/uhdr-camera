package com.onsmith.unc.uhdr;

import java.io.OutputStream;
import java.io.IOException;

import com.onsmith.unc.uhdr.util.BitOutputStream;


public class PixelFireWriter implements Sink<PixelFire> {
	private final BitOutputStream writer;
	
	public PixelFireWriter(OutputStream stream) {
		this.writer = new BitOutputStream(stream);
	}
	
	@Override
	public void send(PixelFire value) {
		try {
			writer.write(value.getX(), 11);
			writer.write(value.getY(), 11);
			writer.write(value.getD(),  4);
			writer.write(value.getDt(), 8);
		} catch (IOException e) {
			System.err.println("Error writing PixelFire object to OutputStream.");
			e.printStackTrace();
		}
	}
	
	public void close() throws IOException {
		writer.close();
	}
}
