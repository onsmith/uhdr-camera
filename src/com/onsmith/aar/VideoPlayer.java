package com.onsmith.aar;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import java.io.InputStream;
import java.util.Scanner;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;


public class VideoPlayer extends TimerTask {
  private int x, y, dt, d;
  private int tNow, w, h, fps, clock;
  private int[][] tNext;
  
  private Scanner scanner;
  
  private JFrame frame;
  private BufferedImage image;
  
  
  /**
   * Constructor
   */
  public VideoPlayer(int w, int h, int clock) {
    this(w, h, clock, 30);
  }
  public VideoPlayer(int w, int h, int clock, int fps) {
    this.w = w;
    this.h = h;
    this.clock = clock;
    this.fps = fps;
    
    tNext = new int[w][h];
  }
  
  
  /**
   * Method to convert a (dt, d) pair to an intensity value
   */
  public byte intensityTransform(int dt, int d) {
    return (byte) (2450*Math.pow(2, d)/dt);
  }
  
  
  /**
   * Method to set the input stream
   */
  public void pipeFrom(InputStream stream) {
    this.scanner = new Scanner(stream);
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
    byte i;
    while (tNext[x][y] < tNow) {
      i = intensityTransform(dt, d);
      image.setRGB(2*x,   2*y,   (i << 16) | (i << 8) | i);
      image.setRGB(2*x,   2*y+1, (i << 16) | (i << 8) | i);
      image.setRGB(2*x+1, 2*y,   (i << 16) | (i << 8) | i);
      image.setRGB(2*x+1, 2*y+1, (i << 16) | (i << 8) | i);
      tNext[x][y] += dt;
      
      x  = scanner.nextInt();
      y  = scanner.nextInt();
      dt = scanner.nextInt();
      d  = scanner.nextInt();
    }
  }
  
  
  /**
   * Method to start a new thread to run the video player
   */
  public void startThread() {
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
    
    // Begin running the timer
    (new Timer(true)).scheduleAtFixedRate(this, new Date(), 1000/fps);
  }
}
