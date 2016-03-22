package com.onsmith.unc.uhdr;

import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;


public class Controller {
  /**
   * Intrinsic properties of camera
   */
  public static final int w     = 200;        // Width of camera, in pixels
  public static final int h     = 200;        // Height of camera, in pixels
  public static final int clock = 1000000000; // Camera clock speed, in hertz
  
  
  public static void main(String [] args) throws IOException {
    // Create pipe for transferring data across threads
    PipedOutputStream pipeOut1 = new PipedOutputStream();
    PipedInputStream  pipeIn1  = new PipedInputStream(pipeOut1);
    
    // Create pipe for transferring data across threads
    PipedOutputStream pipeOut2 = new PipedOutputStream();
    PipedInputStream  pipeIn2  = new PipedInputStream(pipeOut2);
    
    // Create pipe for transferring data across threads
    PipedOutputStream pipeOut3 = new PipedOutputStream();
    PipedInputStream  pipeIn3  = new PipedInputStream(pipeOut3);
    
    // Pipe data from disk
    //Source file = new CameraFileReader(w, h, "data/fixed_D_Output/1wave/D_2/outFrameLess.txt");
    //file.pipeTo(pipeOut1);
    //file.start();
    
    // Pipe data from emulator
    Source camera = new UnorderedEmulator(w, h, clock, 5);
    camera.pipeTo(pipeOut1);
    camera.start();
    
    // Pipe data through encoder
    Transform encoder = new Encoder(w, h);
    encoder.pipeFrom(new BufferedInputStream(pipeIn1)); // new BufferedInputStream(new FileInputStream("data/temp.data"))
    encoder.pipeTo(pipeOut2);  // new FileOutputStream("data/temp.data")
    encoder.start();
    
    // Pipe data through decoder
    Transform decoder = new Decoder(w, h, 5);
    decoder.pipeFrom(new BufferedInputStream(pipeIn2)); // new BufferedInputStream(new FileInputStream("data/temp.data"))
    decoder.pipeTo(pipeOut3);  // new FileOutputStream("data/temp.data")
    decoder.start();
    
    // Pipe data to video player
    Sink player = new Player(w, h, clock, 30, 0, 600); // width, height, clock speed, fps, iMin, iMax
    player.pipeFrom(new BufferedInputStream(pipeIn3)); // new BufferedInputStream(new FileInputStream("data/temp.data"))
    player.start();
  }
}
