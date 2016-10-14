package com.onsmith.unc.uhdr;

import java.io.IOException;
import java.io.OutputStream;
import java.io.DataOutputStream;

public class PixelFireWriter implements Sink<PixelFire> {
	private final DataOutputStream stream;
	
	public PixelFireWriter(OutputStream stream) {
		this.stream = new DataOutputStream(stream);
	}
	
	@Override
	public void send(PixelFire value) {
		try {
			stream.writeInt(value.getX());
			stream.writeInt(value.getY());
			stream.writeInt(value.getD());
			stream.writeInt(value.getDt());
		} catch (IOException e) {
			System.err.println("Error writing PixelFire object to OutputStream.");
			e.printStackTrace();
		}
	}
	
	public void close() throws IOException {
		stream.close();
	}
}
