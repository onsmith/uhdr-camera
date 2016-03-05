package com.onsmith.aar;

import java.io.IOException;
import java.io.FileOutputStream;


public class CreateOutput {
  /**
   * Intrinsic properties of camera
   */
  public static final int    w     = 200;        // Width of camera, in pixels
  public static final int    h     = 200;        // Height of camera, in pixels
  public static final int    clock = 20000;      // Camera clock speed, in hertz
  public static final String fname = "data.txt"; // Output file name
  public static final int    timer = 2*60;       // Number of seconds to run
  
  
  public static void main(String [] args) throws IOException, InterruptedException {
    // Create file on disk for transferring data across threads
    FileOutputStream pipeOut = new FileOutputStream(fname);
    
    // Camera thread
    CameraEmulator camera = new CameraEmulator(w, h, clock);
    camera.pipeTo(pipeOut);
    camera.startThread();
    
    // Wait and then terminate
    Thread.sleep(timer*1000);
    System.exit(0);
  }
}
