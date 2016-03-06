package com.onsmith.aar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.NoSuchElementException;
import java.util.Scanner;


public class CameraFileReader implements Runnable, DataSource {
  private int tLast[][]; // Time when each pixel last fired

  private Scanner     reader; // FileInputStream object to handle input
  private PrintWriter writer; // PrintWriter object to handle output
  
  private Thread thread; // Each instance gets its own thread
  
  
  /**
   * Constructor
   */
  public CameraFileReader(int w, int h, String filename) throws FileNotFoundException {
    reader = new Scanner(new File(filename));
    
    tLast = new int[w][h];
  }
  
  
  /**
   * Method to set the output stream
   */
  public void pipeTo(OutputStream stream) {
    this.writer = new PrintWriter(stream);
  }
  
  
  /**
   * Method to run the emulator
   */
  public void run() {
    int x, y, t, d, dt;
    try {
      while (true) {
        x = reader.nextInt();
        y = reader.nextInt();
        d = reader.nextInt();
        t = reader.nextInt();
        dt = t - tLast[x][y];
        if (dt > 0) writer.printf("%d %d %d %d\n", x, y, dt, d); // Ax Ay dt D
        tLast[x][y] = t;
      }
    }
    catch (NoSuchElementException e) {
      System.out.println("End of input.");
      reader.close();
      stopThread();
    }
  }
  
  
  /**
   * Method to start a new thread to run the camera emulator
   */
  public void startThread() {
    stopThread();
    thread = new Thread(this);
    thread.start();
  }
  
  
  /**
   * Method to stop the current thread
   */
  public void stopThread() {
    if (thread != null) thread.interrupt();
  }
}
