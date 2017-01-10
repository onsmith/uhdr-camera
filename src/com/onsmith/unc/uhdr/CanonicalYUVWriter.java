package com.onsmith.unc.uhdr;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.File;


public class CanonicalYUVWriter implements Sink<IntFrame> {
  private final YUVEncoder[] encoders;
  private static final int BITS_PER_STREAM = 8;
  private static final int BITMASK = (0x1 << BITS_PER_STREAM) - 1;
  
  public CanonicalYUVWriter(File[] files) throws IOException {
    encoders = new YUVEncoder[files.length];
    for (int i=0; i<files.length; i++) {
      encoders[i] = new YUVEncoder(files[i]);
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
        final int pixel = frame.getPixel(x, y);
        for (int i=0; i<encoders.length; i++) {
          rasters[i].setSample(x, y, 0, (pixel >>> (BITS_PER_STREAM*i)) & BITMASK);
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
    for (YUVEncoder encoder : encoders) {
      encoder.finish();
    }
  }
}
