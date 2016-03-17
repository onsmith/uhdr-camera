package com.onsmith.unc.uhdr;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.PriorityQueue;
import java.util.Queue;


public class Decoder implements Runnable, DataTransform {
  private final int clock; // Incoming stream clock speed
  private final int w, h;  // Video width and height
  
  private DataInputStream  reader; // DataInputStream object for reading input
  private DataOutputStream writer; // DataOutputStream object for writing output
  
  private Thread thread; // Every Decoder runs its own thread
  
  private final int iD; // Initial value of D
  
  
  /**
   *  Constructor
   */
  public Decoder(int w, int h, int clock, int iD) {
    this.w     = w;
    this.h     = h;
    this.clock = clock;
    this.iD    = iD;
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
   * Public method to start a new thread for the decoder
   */
  public void start() {
    thread = new Thread(this);
    thread.start();
  }
  
  
  /**
   * Method to run the decoder
   */
  public void run() {
    // Set up a PriorityQueue to schedule pixel fires
    Queue<FireEvent> queue = new PriorityQueue<FireEvent>();
    
    // Read every pixel once, in order
    for (int i=0; i<w; i++) {
      for (int j=0; j<h; j++) {
        int dt = readNextPixel();
        FireEvent pfe = new FireEvent(i, j, dt, iD, dt); // x, y, dt, d, t
        writePixel(pfe);
        queue.add(pfe);
      }
    }
    
    // Use the scheduler to read pixels
    while (true) {
      FireEvent pfe = queue.remove(); // Remove from queue
      pfe.dt = readNextPixel();       // Read next value from wire
      pfe.t += pfe.dt;                // Update t
      writePixel(pfe);                // Write to wire
      queue.add(pfe);                 // Add back to queue
    }
  }
  
  
  /**
   * Method to read a pixel from the wire
   */
  private int readNextPixel() {
    try {
      return reader.readInt();
    }
    catch (IOException e) {
      System.out.println("Decoder could not read from input stream. Thread terminated.");
      thread.interrupt();
      return -1;
    }
  }
  
  
  /**
   * Method to write a specified PixelFire to the wire
   */
  private void writePixel(FireEvent pfe) {
    try {
      writer.writeInt(pfe.x);
      writer.writeInt(pfe.y);
      writer.writeInt(pfe.dt);
      writer.writeInt(pfe.d);
    } catch (IOException e) {
      System.out.println("Decoder could not write to output stream. Thread terminated.");
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
}
