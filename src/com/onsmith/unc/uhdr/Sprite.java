package com.onsmith.unc.uhdr;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;


public class Sprite {
  private final int[][]       path;
  private final BufferedImage image;
  private final double        speed; // path points per second
  
  
  public Sprite(String file, int[][] path) throws IOException {
    this.path = path;
    speed = 10;
    image = ImageIO.read(new File(file));
  }
  
  
  public int intensity(int x, int y, double t, int i) {
    int j = (int) ((t*speed) % path.length);

    x -= path[j][0];
    y -= path[j][1];
    
    if (x < 0 || x >= image.getWidth() || y < 0 || y >= image.getHeight())
      return i;
    else
      return image.getRGB(x, y);
  }
}
