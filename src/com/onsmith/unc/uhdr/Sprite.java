package com.onsmith.unc.uhdr;

import java.io.IOException;

public class Sprite {
  private final int[][]  path;
  private final HDRImage image;
  private final double   speed; // path points per second
  
  public Sprite(HDRImage image, int[][] path) throws IOException {
    this.path  = path;
    this.speed = 100;
    this.image = image;
  }
  
  public double getPixel(int x, int y, double t, double i) {
    int j = (int) ((t*speed) % path.length);
    
    x -= path[j][0];
    y -= path[j][1];
    
    if (0 <= x && x < image.getWidth() &&
        0 <= y && y < image.getHeight()) {
      return image.getPixel(x, y);
    }
    else {
      return i;
    }
  }
}
