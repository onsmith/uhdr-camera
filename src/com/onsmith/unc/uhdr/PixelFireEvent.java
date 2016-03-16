package com.onsmith.unc.uhdr;

public class PixelFireEvent implements Comparable<PixelFireEvent> {
  private double  t;
  private int     x, y, d, dt;
  
  
  public PixelFireEvent(int x, int y, double t, int d, int dt) {
    x(x); y(y); t(t); d(d); dt(dt);
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
  
  
  int dt() {
    return dt;
  }
  void dt(int dt) {
    this.dt = dt;
  }
  
  
  public int compareTo(PixelFireEvent o) {
    return Double.compare(t(), o.t());
  }
}