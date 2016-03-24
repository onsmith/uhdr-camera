package com.onsmith.unc.uhdr;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.PriorityQueue;
import java.util.Queue;


public class Decoder implements Runnable, Transform {
  protected final int w, h;  // Video width and height
  
  private DataInputStream  reader; // DataInputStream object for reading input
  private DataOutputStream writer; // DataOutputStream object for writing output
  
  private Thread thread; // Every Decoder runs its own thread
  
  protected final int iD; // Initial value of D
  
  
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
   * Public methods to start/stop the decoder
   */
  public void start() {
    thread = new Thread(this, "Decoder");
    thread.start();
  }
  public void stop() {
    if (thread != null) thread.interrupt();
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
        queue.add(new FireEvent(i, j));
    
    // Use the scheduler to read pixels
    while (true) {
      FireEvent pfe = queue.remove();       // Remove pixel from queue
      pfe.dt += readNextPixel();            // Read pixel value change from wire
      pfe.t  += pfe.dt;                     // Update next firing time
      writePixel(pfe.x, pfe.y, pfe.dt, iD); // Write to wire
      queue.add(pfe);                       // Add pixel back to queue
    }
  }
  
  
  /**
   * Method to read a pixel from the wire
   */
  protected int readNextPixel() {
    try {
      return reader.readInt();
    }
    catch (IOException e) {
      System.out.println("Decoder could not read from input stream. Thread terminated.");
      stop();
      return -1;
    }
  }
  
  
  /**
   * Method to write a specified PixelFire to the wire
   */
  protected void writePixel(int x, int y, int dt, int d) {
    try {
      writer.writeInt(x);
      writer.writeInt(y);
      writer.writeInt(dt);
      writer.writeInt(d);
    } catch (IOException e) {
      System.out.println("Decoder could not write to output stream. Thread terminated.");
      stop();
    }
  }
}
