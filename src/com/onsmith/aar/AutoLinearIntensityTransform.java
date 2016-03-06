package com.onsmith.aar;

public class AutoLinearIntensityTransform implements IntensityTransform {
  // Parameters for keeping track of observed intensity values
  private double iMin = Double.POSITIVE_INFINITY,
                 iMax = Double.NEGATIVE_INFINITY;
  
  private int clock;
  
  // Constructor
  public AutoLinearIntensityTransform(int clock) {
    this.clock = clock;
  }
  
  
  // Scales the raw intensity value linearly to a double value in the range
  //   [0, 1] such that a value of 0 is given to the smallest intensity
  //   observed and a value of 1 is given to the largest intensity observed
  public double toDouble(int dt, int d) {
    double iRaw = Math.pow(2, d)/((double) dt/clock);
    iMin = Math.min(iMin, iRaw);
    iMax = Math.max(iMax, iRaw);
    return (iRaw - iMin)/(iMax - iMin);
  }
  
  
  // Converts the calculated intensity value to an integer in the range [0, 255]
  public int toInt(int dt, int d) {
    return (int) (255*toDouble(dt, d));
  }
}