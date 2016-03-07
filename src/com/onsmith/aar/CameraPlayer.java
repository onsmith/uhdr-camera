package com.onsmith.aar;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.io.IOException;
import java.io.InputStream;
import java.io.DataInputStream;
import java.awt.BorderLayout;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;


public class CameraPlayer extends TimerTask implements DataSink {
  private int x, y, dt, d;
  private int tNow, w, h, fps, clock;
  private int[][] tNext;
  
  private DataInputStream reader;
  
  private JFrame frame;
  private BufferedImage image;
  
  private Timer timer;
  
  private IntensityTransform intensityTransform;
  
  
  /**
   * Constructors
   */
  public CameraPlayer(int w, int h, int clock) {
    this(w, h, clock, 30);
  }
  
  public CameraPlayer(int w, int h, int clock, int fps, double iMin, double iMax) {
    this.w = w;
    this.h = h;
    this.clock = clock;
    this.fps = fps;
    tNext = new int[w][h];
    intensityTransform = new LinearIntensityTransform(clock, iMin, iMax);
  }
  
  public CameraPlayer(int w, int h, int clock, int fps) {
    this.w = w;
    this.h = h;
    this.clock = clock;
    this.fps = fps;
    tNext = new int[w][h];
    intensityTransform = new AutoLinearIntensityTransform(clock);
  }
  
  
  /**
   * Method to set the input stream
   */
  public void pipeFrom(InputStream stream) {
    reader = new DataInputStream(stream);
  }
  
  
  /**
   * Method to show the last frame and prepare the next one
   */
  public void run() {
    // Show prepared frame
    frame.repaint();
    
    // Advance current time
    tNow += clock/fps;
    
    // Prepare next frame
    int intensity, gray;

    while (tNext[x][y] < tNow) {
      intensity = intensityTransform.toInt(dt, d);
      gray = getIntFromColor(intensity, intensity, intensity);
      image.setRGB(2*x,   2*y,   gray);
      image.setRGB(2*x,   2*y+1, gray);
      image.setRGB(2*x+1, 2*y,   gray);
      image.setRGB(2*x+1, 2*y+1, gray);
      tNext[x][y] += dt;
      
      try {
        x  = reader.readInt();
        y  = reader.readInt();
        dt = reader.readInt();
        d  = reader.readInt();
      }
      catch (IOException e) {
        System.out.println("CameraPlayer could not read from input stream. Thread terminated.");
        stopThread();
      }
    }
  }
  
  
  /**
   * Method to start a new thread to run the video player
   */
  public void startThread() {
    // If a thread is already running, stop it.
    stopThread();
    
    // Initialize image object
    image = new BufferedImage(2*w, 2*h, BufferedImage.TYPE_INT_RGB);
    
    // Create and set JLabel
    JLabel label = new JLabel(new ImageIcon(image));
    
    // Create, configure, and show JFrame
    frame = new JFrame();
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.getContentPane().add(label, BorderLayout.CENTER);
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
    
    // Priming read
    try {
      x  = reader.readInt();
      y  = reader.readInt();
      dt = reader.readInt();
      d  = reader.readInt();
    }
    catch (IOException e) {
      System.out.println("CameraPlayer could not read from input stream. Thread terminated.");
      stopThread();
    }
    
    // Begin running the timer
    timer = new Timer(true);
    timer.scheduleAtFixedRate(this, new Date(), 1000/fps);
  }
  
  
  /**
   * Method to stop the current thread
   */
  public void stopThread() {
    if (timer != null) timer.cancel();
  }
  
  
  /**
   * Static method to package three color bytes as a single integer
   */
  public static int getIntFromColor(int r, int g, int b) {
    r = (r << 16) & 0x00FF0000; // Shift red 16-bits and mask out other stuff
    g = (g << 8)  & 0x0000FF00; // Shift Green 8-bits and mask out other stuff
    b =  b        & 0x000000FF; // Mask out anything not blue.

    return 0xFF000000 | r | g | b; // 0xFF000000 for 100% Alpha. Bitwise OR everything together.
  }
}
