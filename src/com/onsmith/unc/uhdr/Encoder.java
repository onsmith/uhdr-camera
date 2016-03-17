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
  private final int clock; // Incoming stream clock speed
  private final int w, h;  // Video width and height
  
  private final List<FireEvent>[][] buffer; // Structure to buffer pixels
  
  private final long[][] t;
  
  private DataInputStream  reader; // DataInputStream object for reading input
  private DataOutputStream writer; // DataOutputStream object for writing output
  
  private Thread thread; // Every Encoder runs its own thread
  
  
  /**
   *  Constructor
   */
  @SuppressWarnings("unchecked")
  public Encoder(int w, int h, int clock) {
    this.w     = w;
    this.h     = h;
    this.clock = clock;
    
    // Initialize t structure
    t = new long[w][h];
    
    // Initialize buffer structure
    buffer = (List<FireEvent>[][]) new List[w][h];
    for (int i=0; i<w; i++)
      for (int j=0; j<h; j++)
        buffer[i][j] = new ArrayList<FireEvent>(10);
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
    
    // Write the first FireEvent for every pixel to the wire
    for (int i=0; i<w; i++) {
      for (int j=0; j<h; j++) {
        FireEvent pfe = nextIncoming(i, j);
        queue.add(pfe);
        writePixel(pfe);
      }
    }
    
    // Use the scheduler to write pixels to the wire
    while (true) {
      FireEvent pfe = queue.remove();   // Remove from queue
      pfe = nextIncoming(pfe.x, pfe.y); // Read next value from wire
      writePixel(pfe);                  // Write to wire
      queue.add(pfe);                   // Add back to queue
    }
  }
  
  
  /**
   * Method to pop the next PixelFire for a given pixel from the buffer
   */
  private FireEvent nextIncoming(int x, int y) {
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
      t[x][y] += dt;
      buffer[x][y].add(new FireEvent(x, y, dt, d, t[x][y]));
    }
    catch (IOException e) {
      System.out.println("Encoder could not read from input stream. Thread terminated.");
      thread.interrupt();
    }
  }
  
  
  /**
   * Method to write a specified PixelFire to the wire
   */
  private void writePixel(FireEvent pfe) {
    try {
      //writer.writeInt(pfe.x);
      //writer.writeInt(pfe.y);
      writer.writeInt(pfe.dt);
      //writer.writeInt(pfe.d);
    } catch (IOException e) {
      System.out.println("Encoder could not write to output stream. Thread terminated.");
      thread.interrupt();
    }
  }
  
  
  /**
   * Internal class representing a single pixel firing at a specific time. Used
   *   by the internal PriorityQueue to determine which pixel to send next.
   */
  private static class FireEvent implements Comparable<FireEvent> {
    public final int  x, y;  // Pixel's spatial location
    public       int  dt, d; // Pixel's intensity
    public       long t;     // Next time this pixel should fire
    
    public static long Q = Long.MAX_VALUE/2;
    
    public FireEvent(int x, int y, int dt, int d, long t) {
      this.x  = x;
      this.y  = y;
      this.d  = d;
      this.dt = dt;
      this.t  = t;
    }
    
    public int compareTo(FireEvent o) {
      if (t > Q && o.t < -Q || o.t > Q && t < -Q)
        return Long.compare(o.t, t);
      else
        return Long.compare(t, o.t);
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
