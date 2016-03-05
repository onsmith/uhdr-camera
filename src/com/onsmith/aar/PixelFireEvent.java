package com.onsmith.aar;

public class PixelFireEvent implements Comparable<PixelFireEvent> {
  private double  t;
  private int     x, y, d;
  
  
  public PixelFireEvent(int x, int y, double t, int d) {
    x(x); y(y); t(t); d(d);
  }
  
  
  int x() {
    return x;
  }
  void x(int x) {
    this.x = x;
  }
  
  
  int y() {
    return y;
  }
  void y(int y) {
    this.y = y;
  }
  
  
  double t() {
    return t;
  }
  void t(double t) {
    this.t = t;
  }
  
  
  int d() {
    return d;
  }
  void d(int d) {
    this.d = d;
  }
  
  
  public int compareTo(PixelFireEvent o) {
    return Double.compare(t(), o.t());
  }
}