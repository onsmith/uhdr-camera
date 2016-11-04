package com.onsmith.unc.uhdr;

import java.io.IOException;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;

import org.jcodec.api.awt.SequenceEncoder;

public class CanonicalMP4Writer implements Sink<int[][]> {
	private final SequenceEncoder low, med, high;
	
	public CanonicalMP4Writer(File low, File med, File high) throws IOException {
    this.low  = new SequenceEncoder(low);
    this.med  = new SequenceEncoder(med);
    this.high = new SequenceEncoder(high);
	}
	
	@Override
	public void send(int[][] frame) {
		try {
			low.enc
		} catch (IOException e) {
			System.err.println("Error encoding BufferedImage into SequenceEncoder");
			e.printStackTrace();
		}
	}
	
	public void close() throws IOException {
		encoder.finish();
	}
	
	private BufferedImage[] splitFrame(int[][] frame) {
	  
	}
	
	public static Image makeImage(int[] pixels, int w, int h) {
    BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    WritableRaster raster = (WritableRaster) image.getData();
    raster.setPixels(0, 0, w, h, pixels);
    return image;
	}
}
