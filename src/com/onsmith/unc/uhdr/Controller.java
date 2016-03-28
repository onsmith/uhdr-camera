package com.onsmith.unc.uhdr;

import java.io.FileNotFoundException;
import java.util.Iterator;


public class Controller {
  /**
   * Intrinsic properties of camera
   */
  public static final int w     = 100;        // Width of camera, in pixels
  public static final int h     = 100;        // Height of camera, in pixels
  public static final int clock = 1000000000; // Camera clock speed, in hertz
  public static final int iD    = 5;          // Initial value for D
  
  
  public static void main(String [] args) throws FileNotFoundException {
    // Get data from disk
    //Iterator<PixelFire> file = new CameraFileReader(w, h, "data/fixed_D_Output/1wave/D_2/outFrameLess.txt");
    
    // Get data from emulator
    Iterator<PixelFire> camera = new OrderedEmulator(w, h, clock, iD);
    
    // Pipe data through encoder
    //Iterator<Integer> encoder = new Encoder(w, h, camera);
    Iterator<Integer> encoder = new PeriodicEncoder(w, h, clock, camera);
    
    // Pipe data through decoder
    //Iterator<PixelFire> decoder = new Decoder(w, h, iD, encoder);
    Iterator<PixelFire> decoder = new PeriodicDecoder(w, h, iD, clock, encoder);
    
    // Pipe data to video player
    Player player = new Player(w, h, clock, 30, 0, 600, decoder); // width, height, clock speed, fps, iMin, iMax, input
    player.start();
  }
}
