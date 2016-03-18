package com.onsmith.unc.uhdr;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.PriorityQueue;
import java.util.Queue;


public class Decoder implements Runnable, DataTransform {
  private final int w, h;  // Video width and height
  
  private DataInputStream  reader; // DataInputStream object for reading input
  private DataOutputStream writer; // DataOutputStream object for writing output
  
  private Thread thread; // Every Decoder runs its own thread
  
  private final int iD; // Initial value of D
  
  
  /**
   *  Constructor
   */
  public Decoder(int w, int h, int iD) {
    this.w  = w;
    this.h  = h;
    this.iD = iD;
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
    
    // Fill the scheduler
    for (int i=0; i<w; i++)
      for (int j=0; j<h; j++)
        queue.add(new FireEvent(i, j, 0));
    
    // Use the scheduler to read pixels
    while (true) {
      FireEvent pfe = queue.remove();   // Remove from queue
      int dt = readNextPixel();         // Read next value from wire
      pfe.t += dt;                      // Update t
      writePixel(pfe.x, pfe.y, dt, iD); // Write to wire
      queue.add(pfe);                   // Add back to queue
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
  private void writePixel(int x, int y, int dt, int d) {
    try {
      writer.writeInt(x);
      writer.writeInt(y);
      writer.writeInt(dt);
      writer.writeInt(d);
    } catch (IOException e) {
      System.out.println("Decoder could not write to output stream. Thread terminated.");
      thread.interrupt();
    }
  }
  
  
  /**
   * Internal class representing a pixel firing at a specific time. Used by the
   *   internal PriorityQueue to determine which pixel will come next.
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
}
