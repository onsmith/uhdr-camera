package com.onsmith.aar;

import java.io.IOException;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;


public class Controller {
  /**
   * Intrinsic properties of camera
   */
  public static final int w     = 200;   // Width of camera, in pixels
  public static final int h     = 200;   // Height of camera, in pixels
  public static final int clock = 18000; // Camera clock speed, in hertz
  
  
  public static void main(String [] args) throws IOException {
    // Create pipe for transferring data across threads
    PipedOutputStream pipeOut = new PipedOutputStream();
    PipedInputStream  pipeIn  = new PipedInputStream(pipeOut);
    
    // Create file on disk for transferring data across threads
    //FileOutputStream pipeOut = new FileOutputStream("data.txt");
    //FileInputStream pipeIn   = new FileInputStream("data.txt");
    
    // Camera thread
    CameraEmulator camera = new CameraEmulator(w, h, clock);
    camera.pipeTo(pipeOut);
    camera.startThread();
    
    // Video player thread
    VideoPlayer player = new VideoPlayer(w, h, clock);
    player.pipeFrom(pipeIn);
    player.startThread();
  }
}
