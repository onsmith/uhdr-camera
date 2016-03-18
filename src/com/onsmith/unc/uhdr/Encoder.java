package com.onsmith.unc.uhdr;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;


public class Encoder implements Runnable, DataTransform {
  private final int w, h;  // Video width and height
  
  private final List<Intensity>[][] buffer; // Structure to buffer pixels
  
  private DataInputStream  reader; // DataInputStream object for reading input
  private DataOutputStream writer; // DataOutputStream object for writing output
  
  private Thread thread; // Every Encoder runs its own thread
  
  
  /**
   *  Constructor
   */
  @SuppressWarnings("unchecked")
  public Encoder(int w, int h) {
    this.w = w;
    this.h = h;
    
    // Initialize buffer structure
    buffer = (List<Intensity>[][]) new List[w][h];
    for (int i=0; i<w; i++)
      for (int j=0; j<h; j++)
        buffer[i][j] = new ArrayList<Intensity>(10);
  }
  
  
  /**
   * Public methods to set input/output streams
   */
  public void pipeFrom(InputStream stream) {
    reader = new DataInputStream(stream);
  }
  public void pipeTo(OutputStream stream) {
    writer = new DataOutputStream(stream);
  }
  
  
  /**
   * Public method to start a new thread for the encoder
   */
  public void start() {
    thread = new Thread(this);
    thread.start();
  }
  
  
  /**
   * Method to run the encoder
   */
  public void run() {
    // Set up a PriorityQueue to schedule the FireEvents
    Queue<FireEvent> queue = new PriorityQueue<FireEvent>();
    
    // Fill the scheduler
    for (int i=0; i<w; i++)
      for (int j=0; j<h; j++)
        queue.add(new FireEvent(i, j, 0));
    
    // Use the scheduler to write pixels to the wire
    while (true) {
      FireEvent pfe = queue.remove();            // Remove pixel from queue
      Intensity pi = nextIncoming(pfe.x, pfe.y); // Read corresponding next intensity value from buffer
      writePixel(pi.dt);                         // Write pixel to wire
      pfe.t += pi.dt;                            // Update next firing time
      queue.add(pfe);                            // Add back to queue
    }
  }
  
  
  /**
   * Method to pop the next Intensity for a given pixel from the buffer
   */
  private Intensity nextIncoming(int x, int y) {
    while (buffer[x][y].isEmpty()) readNextPixel();
    return buffer[x][y].remove(0);
  }
  
  
  /**
   * Method to read a PixelFire from the wire into the buffer
   */
  private void readNextPixel() {
    try {
      int x  = reader.readInt(),
          y  = reader.readInt(),
          dt = reader.readInt(),
          d  = reader.readInt();
      buffer[x][y].add(new Intensity(dt, d));
    }
    catch (IOException e) {
      System.out.println("Encoder could not read from input stream. Thread terminated.");
      thread.interrupt();
    }
  }
  
  
  /**
   * Method to write a specified pixel to the wire
   */
  private void writePixel(int dt) {
    try {
      writer.writeInt(dt);
    } catch (IOException e) {
      System.out.println("Encoder could not write to output stream. Thread terminated.");
      thread.interrupt();
    }
  }
  
  
  /**
   * Internal class to store a pixel intensity value
   */
  private static class Intensity {
    public final int dt, d;
    
    public Intensity(int dt, int d) {
      this.dt = dt;
      this.d  = d;
    }
  }
  
  
  /**
   * Internal class representing a pixel firing at a specific time. Used by the
   *   internal PriorityQueue to determine which pixel's value to send next.
   */
  private static class FireEvent implements Comparable<FireEvent> {
    public final int x, y; // Pixel's spatial location
    public       int t;    // Next time this pixel should fire
    
    public static int Q = Integer.MAX_VALUE/2;
    
    public FireEvent(int x, int y, int t) {
      this.x = x;
      this.y = y;
      this.t = t;
    }
    
    public int compareTo(FireEvent o) {
      if (t == o.t) {
        if (x == o.x) return Integer.compare(y, o.y);
        else          return Integer.compare(x, o.x);
      }
      else if (t > Q && o.t < -Q || o.t > Q && t < -Q)
        return Integer.compare(o.t, t);
      else
        return Integer.compare(t, o.t);
    }
  }
  
  
  /*
  private static final int clock = 1000000000;
  private static final double tol   = 6;
  public static int setNextD(int t1, int d1, int t2, int d2) {
    int d = d2;
    if (Math.abs((0x1 << d1)/((double) t1/clock) - (0x1 << d2)/((double) t2/clock)) < tol)
      d++;
    else
      d--;
    
    if (d < 0)  d = 0;
    if (d > 15) d = 15;
    
    System.out.println(d);
    return d;
  }
  */
}
