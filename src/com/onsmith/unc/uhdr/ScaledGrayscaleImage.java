package com.onsmith.unc.uhdr;

import java.awt.Image;
import java.awt.image.BufferedImage;


public class ScaledGrayscaleImage {
  private final int           m;
  private final BufferedImage image;
  
  
  /**
   * Constructor
   */
  public ScaledGrayscaleImage(int w, int h, int m) {
    this.m = m;
    image = new BufferedImage(w*m, h*m, BufferedImage.TYPE_INT_RGB);
  }
  
  
  /**
   * Gets the underlying image
   */
  public Image getImage() {
    return image;
  }
  
  
  /**
   * Sets a pixel
   */
  public void setPixel(int x, int y, int value) {
    x *= m;
    y *= m;
    value = asGrayscale(value);
    for (int i=x; i<x+m; i++)
      for (int j=y; j<y+m; j++)
        image.setRGB(i, j, value);
  }
  
  
  /**
   * Static method to package three color bytes as a single integer
   */
  private static int getIntFromColor(int r, int g, int b) {
    r = (r << 16) & 0x00FF0000; // Shift red 16-bits and mask out other stuff
    g = (g << 8)  & 0x0000FF00; // Shift Green 8-bits and mask out other stuff
    b =  b        & 0x000000FF; // Mask out anything not blue.
    
    return 0xFF000000 | r | g | b; // 0xFF000000 for 100% Alpha. Bitwise OR everything together.
  }
  
  
  /**
   * Static method to transform an intensity into a greyscale value
   */
  private static int asGrayscale(int intensity) {
    return getIntFromColor(intensity, intensity, intensity);
  }
}