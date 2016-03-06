package com.onsmith.aar;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Scanner;
import java.io.InputStream;
import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;


public class VideoPlayer extends TimerTask {
  private double iMin = Double.POSITIVE_INFINITY,
                 iMax = Double.NEGATIVE_INFINITY; 
  
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
  public int intensityTransform(int dt, int d) {
    double iRaw = Math.pow(2, d)/dt;
    iMin = Math.min(iMin, iRaw);
    iMax = Math.max(iMax, iRaw);
    return (int) (255*(iRaw - iMin)/(iMax - iMin));
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
    int intensity, gray;
    while (tNext[x][y] < tNow) {
      intensity = intensityTransform(dt, d);
      gray = getIntFromColor(intensity, intensity, intensity);
      image.setRGB(2*x,   2*y,   gray);
      image.setRGB(2*x,   2*y+1, gray);
      image.setRGB(2*x+1, 2*y,   gray);
      image.setRGB(2*x+1, 2*y+1, gray);
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
    
    // Priming read
    x  = scanner.nextInt();
    y  = scanner.nextInt();
    dt = scanner.nextInt();
    d  = scanner.nextInt();
    
    // Begin running the timer
    (new Timer(true)).scheduleAtFixedRate(this, new Date(), 1000/fps);
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
