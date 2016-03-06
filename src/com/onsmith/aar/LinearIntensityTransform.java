package com.onsmith.aar;

public class LinearIntensityTransform implements IntensityTransform {
  // Parameters for keeping track of observed intensity values
  private double iMin, iRange;
  
  private int clock;
  
  // Constructor
  public LinearIntensityTransform(int clock, double iMin, double iMax) {
    this.clock = clock;
    this.iMin = iMin;
    this.iRange = iMax - iMin;
  }
  
  
  // Scales the raw intensity value linearly to a double value in the range
  //   [0, 1] such that any intensity less than or equal to iMin is mapped to 0,
  //   while any intensity greater or equal to iMax is mapped to 1; intensities
  //   in between are mapped linearly.
  public double toDouble(int dt, int d) {
    double iRaw = Math.pow(2, d)/((double) dt/clock);
    return Math.min(1, Math.max(0, (iRaw - iMin)/iRange));
  }
  
  
  // Converts the calculated intensity value to an integer in the range [0, 255]
  public int toInt(int dt, int d) {
    return (int) (255*toDouble(dt, d));
  }
}