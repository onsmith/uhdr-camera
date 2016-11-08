package com.onsmith.unc.uhdr;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;



public class PixelFireToBufferedImage implements Source<BufferedImage> {
  private int fps; // Frames per second
  private final int clock; // Clock speed
  private PixelFire pf; // Stores the next pixel to display
  private int tNow; // Current time
  private final BufferedImage image;
  private final Source<PixelFire> input; // Source of PixelFire objects
  private IntensityTransform intensityTransform; // Delegate intensity transformation to an IntensityTransform object
  private static final int Q = Integer.MAX_VALUE/2; // Factor used for dealing with integer overflow
  
  
  
  /**
   * Constructor
   */
  public PixelFireToBufferedImage(Source<PixelFire> input, int w, int h, int clock, int fps, double iMin, double iMax) {
    this.clock = clock;
    this.input = input;
    this.image = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
    
    setFps(fps);
    setIntensityRange(iMin, iMax);
    
    // Priming read
    pf = input.next();
  }
  
  
  
  /**
   * Runnable run() method. Displays the prepared frame and prepares the next
   *   one.
   */
  @Override
  public BufferedImage next() {
    // Advance current time
    tNow += ticksPerFrame();
    
    // Prepare next frame
    //   Keep sending pixels to the image while (tShow < tNow)
    //   Check for possible overflow
    WritableRaster raster = image.getRaster();
    while (pf.getTShow() < tNow && (pf.getTShow() > -Q || tNow < Q) || pf.getTShow() > Q && tNow < -Q) {
      raster.setSample(pf.getX(), pf.getY(), 0, intensityTransform.toInt(pf.getDt(), pf.getD()));
      pf = input.next();
    }
    
    // Show prepared frame
    return image;
  }
  
  
  
  @Override
  public BufferedImage current() {
    return image;
  }
  
  
  
  /**
   * Setter for fps field.
   */
  public void setFps(int fps) {
    this.fps = fps;
  }
  
  
  
  /**
   * Setter for intensity range.
   */
  public void setIntensityRange(double iMin, double iMax) {
    intensityTransform = new LinearIntensityTransform(clock, iMin, iMax);
  }
  
  
  
  /**
   * Calculates the number of ticks that should elapse between frames
   */
  private int ticksPerFrame() {
    return clock/fps;
  }
}
