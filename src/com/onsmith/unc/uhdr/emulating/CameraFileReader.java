package com.onsmith.unc.uhdr.emulating;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Scanner;

import com.onsmith.unc.uhdr.PixelFire;


public class CameraFileReader implements Iterator<PixelFire> {
  private int tLast[][];  // Time when each pixel last fired
  private Scanner reader; // Scanner object to handle reading input from text file
  
  
  /**
   * Constructor
   */
  public CameraFileReader(int w, int h, String filename) throws FileNotFoundException {
    tLast  = new int[w][h];
    reader = new Scanner(new File(filename));
  }
  
  
  /**
   * Iterator interface hasNext() method
   */
  public boolean hasNext() {
    return reader.hasNextInt();
  }
  
  
  /**
   * Iterator interface next() method
   */
  public PixelFire next() {
    int y  = reader.nextInt(),
        x  = reader.nextInt(),
        d  = reader.nextInt(),
        t  = reader.nextInt(),
        dt = t - tLast[x][y];
    tLast[x][y] = t;
    return new PixelFire(x, y, dt, d, t);
  }
}
