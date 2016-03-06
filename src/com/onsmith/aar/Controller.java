package com.onsmith.aar;

import java.io.IOException;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;


public class Controller {
  /**
   * Intrinsic properties of camera
   */
  public static final int w     = 100;        // Width of camera, in pixels
  public static final int h     = 100;        // Height of camera, in pixels
  public static final int clock = 20000; // Camera clock speed, in hertz
  
  
  public static void main(String [] args) throws IOException {
    // Create pipe for transferring data across threads
    PipedOutputStream pipeOut = new PipedOutputStream();
    PipedInputStream  pipeIn  = new PipedInputStream(pipeOut);
    
    // Pipe data from emulator
    DataSource camera = new CameraEmulator(w, h, clock);
    camera.pipeTo(pipeOut);
    camera.startThread();
    
    // Pipe data from disk
    //DataSource file = new CameraFileReader(w, h, "data/fixed_D_Output/3wave/D_3/outFrameLess.txt");
    //file.pipeTo(pipeOut);
    //file.startThread();
    
    // Pipe data to video player
    DataSink player = new CameraPlayer(w, h, clock, 30, -30, 230); // width, height, clock speed, fps, iMin, iMax
    player.pipeFrom(pipeIn);
    player.startThread();
  }
}
