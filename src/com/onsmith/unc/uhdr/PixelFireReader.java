package com.onsmith.unc.uhdr;

import java.io.InputStream;
import java.io.IOException;

import com.onsmith.unc.uhdr.util.BitInputStream;


public class PixelFireReader implements Source<PixelFire> {
	private final BitInputStream writer;
	private final int[][]        tFire;
	private       PixelFire      current;
	
	public PixelFireReader(InputStream stream, int w, int h) {
		this.writer = new BitInputStream(stream);
		tFire       = new int[w][h];
	}
	
	@Override
	public PixelFire next() {
		try {
			int x, y, d, dt;
			x  = writer.readBits(11);
			y  = writer.readBits(11);
			d  = writer.readBits(4);
			dt = writer.readBits(8);
			tFire[x][y] += dt;
			current = new PixelFire(x, y, dt, d, tFire[x][y]);
			return current;
		} catch (IOException e) {
			System.err.println("Error reading PixelFire object from InputStream.");
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public PixelFire current() {
		return current;
	}
}
