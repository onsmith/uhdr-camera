package com.onsmith.unc.uhdr;

public interface IntensityTransform {
  public int    toInt(int dt, int d);
  public double toDouble(int dt, int d);
}
