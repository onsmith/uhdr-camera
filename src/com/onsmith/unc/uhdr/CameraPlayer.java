package com.onsmith.unc.uhdr;

import java.util.Date;
import java.util.Timer;

import java.io.IOException;
import java.io.InputStream;
import java.io.DataInputStream;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class CameraPlayer implements Runnable, DataSink, ChangeListener {
  private static final int    MIN_FPS = 1;   // Minimum allowed FPS
  private static final int    MAX_FPS = 120; // Maximum allowed FPS
  
  private static final int SCALE_FACTOR = 8; // Scale factor for the displayed video
  
  private final double iMin, iMax; // Minimum and maximum allowable intensities (for intensity scaling)
  
  private int fps;       // Current fps value
  private int fpsUpdate; // When the UI updates the fps, this property is changed

  private final int w, h, clock; // Intrinsic camera properties
  
  private int x, y, dt, d; // Stores the last pixel read
  
  private long     tNow;  // Current time
  private long[][] tNext; // Next time that each pixel needs to be changed
  
  private DataInputStream reader; // Input stream object

  private JFrame frame;        // JFrame to house the player
  private JLabel iconLabel;    // Label to house the image
  private JSlider fpsControl,  // Slider that controls the player fps
                  iMinControl, // Slider that controls the largest intensity that should map to 0
                  iMaxControl; // Slider that controls the smallest intensity that should map to 255
  
  private final BufferedImage image; // BufferedImage to display the current frame on the screen
  
  private Timer timer; // Timer to periodically call run()
  
  private IntensityTransform intensityTransform; // Delegate intensity transformation to an IntensityTransform object
  
  private static final long Q = Long.MAX_VALUE/2; // Factor used for dealing with integer overflow
  
  
  /**
   * Constructors
   */
  public CameraPlayer(int w, int h, int clock, int fps, double iMin, double iMax) {
    this.w     = w;
    this.h     = h;
    this.clock = clock;
    this.fps   = fps;
    this.iMin  = iMin;
    this.iMax  = iMax;
    
    tNext = new long[w][h];
    intensityTransform = new LinearIntensityTransform(clock, iMin, iMax);
    image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
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
    // Handle changes in fps
    if (fpsUpdate != fps) {
      fps = fpsUpdate;
      restartTimer();
      return;
    }
    
    // Show prepared frame
    iconLabel.setIcon(new ImageIcon(image.getScaledInstance(SCALE_FACTOR*h, SCALE_FACTOR*w, Image.SCALE_FAST)));
    //frame.repaint();
    
    // Advance current time
    tNow += ticksPerFrame();
    
    // Prepare next frame
    //   Keep sending pixels to the image while (tNext[x][y] < tNow)
    //   Check for possible overflow
    while (tNext[x][y] < tNow && (tNext[x][y] > -Q || tNow < Q) || tNext[x][y] > Q && tNow < -Q) {
      image.setRGB(x, y, asGrayscale(intensityTransform.toInt(dt, d)));
      tNext[x][y] += dt;
      readPixel();
    }
  }
  
  
  /**
   * Method to start a new thread to run the video player
   */
  public void start() {
    // Show the player window
    startPlayerWindow();
    
    // Priming read
    readPixel();
    
    // Begin running the timer
    fpsUpdate = fps;
    restartTimer();
  }
  
  
  /**
   * Internal method to read in the next pixel
   */
  private void readPixel() {
    try {
      x  = reader.readInt();
      y  = reader.readInt();
      dt = reader.readInt();
      d  = reader.readInt();
    }
    catch (IOException e) {
      System.out.println("CameraPlayer could not read from input stream. Thread terminated.");
      timer.cancel();
      return;
    }
  }
  
  
  /**
   * Starts or restarts the timer
   */
  private void restartTimer() {
    if (timer != null) timer.cancel();
    timer = new Timer(true);
    timer.scheduleAtFixedRate(new TimerTaskShell(this), new Date(), msPerFrame());
  }
  
  
  /**
   * Method to build and display the player JFrame Window
   */
  private void startPlayerWindow() {
    // Create and set JLabel to hold the image
    iconLabel = new JLabel(new ImageIcon(image.getScaledInstance(SCALE_FACTOR*h, SCALE_FACTOR*w, Image.SCALE_FAST)));
    iconLabel.setBorder(new EmptyBorder(20, 20, 20, 20)); // top, left, bottom, right
    iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    
    // Create and set JLabel to hold the FPS slider
    fpsControl = new JSlider(JSlider.HORIZONTAL, MIN_FPS, MAX_FPS, fps);
    fpsControl.setMinorTickSpacing(5);
    fpsControl.setLabelTable(fpsControl.createStandardLabels(10, 10));
    fpsControl.setPaintTicks(true);
    fpsControl.setPaintLabels(true);
    fpsControl.setBorder(new EmptyBorder(0, 20, 20, 20)); // top, left, bottom, right
    fpsControl.setFont(new Font("Serif", Font.ITALIC, 30));
    fpsControl.setAlignmentX(Component.CENTER_ALIGNMENT);
    fpsControl.addChangeListener(this);
    
    // Create and set JLabel to hold the min intensity slider
    iMinControl = new JSlider(JSlider.HORIZONTAL, (int) iMin, (int) iMax, (int) iMin);
    iMinControl.setMajorTickSpacing((int) (iMax - iMin)/10);
    iMinControl.setMinorTickSpacing((int) (iMax - iMin)/50);
    iMinControl.setPaintTicks(true);
    iMinControl.setPaintLabels(true);
    iMinControl.setBorder(new EmptyBorder(0, 20, 20, 20)); // top, left, bottom, right
    iMinControl.setFont(new Font("Serif", Font.ITALIC, 30));
    iMinControl.setAlignmentX(Component.CENTER_ALIGNMENT);
    iMinControl.addChangeListener(this);
    
    // Create and set JLabel to hold the max intensity slider
    iMaxControl = new JSlider(JSlider.HORIZONTAL, (int) iMin, (int) iMax, (int) iMax);
    iMaxControl.setMajorTickSpacing((int) (iMax - iMin)/10);
    iMaxControl.setMinorTickSpacing((int) (iMax - iMin)/50);
    iMaxControl.setPaintTicks(true);
    iMaxControl.setPaintLabels(true);
    iMaxControl.setBorder(new EmptyBorder(0, 20, 20, 20)); // top, left, bottom, right
    iMaxControl.setFont(new Font("Serif", Font.ITALIC, 30));
    iMaxControl.setAlignmentX(Component.CENTER_ALIGNMENT);
    iMaxControl.addChangeListener(this);
    
    // Create, configure, and show JFrame
    frame = new JFrame("UHDR Player");
    frame.setMinimumSize(new Dimension(800, 0)); // w, h
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
    frame.getContentPane().add(iconLabel);
    frame.getContentPane().add(fpsControl);
    frame.getContentPane().add(iMinControl);
    frame.getContentPane().add(iMaxControl);
    frame.pack();
    frame.setLocationRelativeTo(null); // Passing a null component causes the window to be placed in the center of the screen
    frame.setVisible(true);
  }
  
  
  /**
   * Calculates the number of ticks that should elapse between frames
   */
  private int ticksPerFrame() {
    return clock/fps;
  }
  
  
  /**
   * Calculates the number of milliseconds that should elapse between frames
   */
  private int msPerFrame() {
    return 1000/fps;
  }
  
  
  /**
   * Static method to package three color bytes as a single integer
   */
  private static int getIntFromColor(int r, int g, int b) {
    r = (r << 16) & 0x00FF0000; // Shift red 16-bits and mask out other stuff
    g = (g << 8)  & 0x0000FF00; // Shift Green 8-bits and mask out other stuff
    b =  b        & 0x000000FF; // Mask out anything not blue.
    
    return 0xFF000000 | r | g | b; // 0xFF000000 for 100% Alpha. Bitwise OR everything together.
  }
  
  
  /**
   * Static method to transform an intensity into a greyscale value
   */
  private static int asGrayscale(int intensity) {
    return getIntFromColor(intensity, intensity, intensity);
  }
  
  
  /**
   * Method to handle UI updates
   */
  public void stateChanged(ChangeEvent e) {
    fpsUpdate = fpsControl.getValue();
    intensityTransform = new LinearIntensityTransform(clock, iMinControl.getValue(), iMaxControl.getValue());
  }
}