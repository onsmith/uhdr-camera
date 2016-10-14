package com.onsmith.unc.uhdr;

import java.io.InputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class PixelFireReader implements Source<PixelFire> {
	private final DataInputStream stream;
	private final int[][]         tFire;
	
	public PixelFireReader(InputStream stream, int w, int h) {
		this.stream = new DataInputStream(stream);
		tFire       = new int[w][h];
	}
	
	@Override
	public PixelFire next() {
		try {
			int x, y, d, dt;
			x = stream.readInt();
			y = stream.readInt();
			d = stream.readInt();
			dt = stream.readInt();
			tFire[x][y] += dt;
			return new PixelFire(x, y, dt, d, tFire[x][y]);
		} catch (IOException e) {
			System.err.println("Error reading PixelFire object from InputStream.");
			e.printStackTrace();
		}
		return null;
	}
}
