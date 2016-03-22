package com.onsmith.unc.uhdr;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.util.NoSuchElementException;
import java.util.Scanner;


public class CameraFileReader implements Runnable, Source {
  private int tLast[][]; // Time when each pixel last fired
  
  private Scanner          reader; // Scanner object to handle reading input from text file
  private DataOutputStream writer; // DataOutputStream object to handle output
  
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
    writer = new DataOutputStream(stream);
  }
  
  
  /**
   * Method to start/stop the file reader
   */
  public void start() {
    thread = new Thread(this);
    thread.start();
  }
  public void stop() {
    if (thread != null) thread.interrupt();
  }
  
  
  /**
   * Method to run the file reader
   */
  public void run() {
    int x, y, t, d, dt;
    try {
      while (true) {
        y = reader.nextInt();
        x = reader.nextInt();
        d = reader.nextInt();
        t = reader.nextInt();
        
        dt = t - tLast[x][y];
        
        writer.writeInt(x);
        writer.writeInt(y);
        writer.writeInt(dt);
        writer.writeInt(d);
        
        tLast[x][y] = t;
      }
    }
    catch (IOException e) {
      System.out.println("CameraFileReader could not write to output stream. Thread terminated.");
      stop();
      return;
    }
    catch (NoSuchElementException e) {
      System.out.println("CameraFileReader could not read from given file. Thread terminated.");
      stop();
      return;
    }
  }
}
