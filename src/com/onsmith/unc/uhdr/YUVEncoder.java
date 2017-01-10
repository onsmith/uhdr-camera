package com.onsmith.unc.uhdr;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.DataOutputStream;

public class YUVEncoder {
  private final DataOutputStream video;
  
  public YUVEncoder(File file) throws FileNotFoundException {
    video = new DataOutputStream(new FileOutputStream(file));
  }
  
  void encodeImage(BufferedImage frame) throws IOException {
    byte[] y = ((DataBufferByte) frame.getRaster().getDataBuffer()).getData();
    byte[] cb = new byte[y.length/4];
    byte[] cr = cb;
    Arrays.fill(cb, (byte) 128);
    
    video.write(y);
    video.write(cb);
    video.write(cr);
  }
  
  public void finish() throws IOException {
    video.close();
  }
}
