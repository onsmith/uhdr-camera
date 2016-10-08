package com.onsmith.unc.uhdr;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.util.Hashtable;

public class GrayBufferedImage extends BufferedImage {
  public GrayBufferedImage(int arg0, int arg1, int arg2) {
    super(arg0, arg1, arg2);
  }
  public GrayBufferedImage(int arg0, int arg1, int arg2,
      IndexColorModel arg3) {
    super(arg0, arg1, arg2, arg3);
  }
  public GrayBufferedImage(ColorModel arg0, WritableRaster arg1,
      boolean arg2, Hashtable<?, ?> arg3) {
    super(arg0, arg1, arg2, arg3);
  }
  
  
  
  /**
   * Sets a pixel to a grayscale value
   */
  public void setGray(int x, int y, int intensity) {
    setRGB(x, y, combineRGB(intensity, intensity, intensity));
  }
  
  
  /**
   * Packages three color bytes as a single integer
   */
  private static int combineRGB(int r, int g, int b) {
    r = (r << 16) & 0x00FF0000; // Shift red 16-bits and mask out other stuff
    g = (g << 8)  & 0x0000FF00; // Shift Green 8-bits and mask out other stuff
    b =  b        & 0x000000FF; // Mask out anything not blue.
    return 0xFF000000 | r | g | b; // 0xFF000000 for 100% Alpha. Bitwise OR everything together.
  }
}
