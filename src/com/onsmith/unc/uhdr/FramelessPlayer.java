package com.onsmith.unc.uhdr;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Date;
import java.util.Timer;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class FramelessPlayer implements Runnable, ChangeListener {
  private static final int MIN_FPS = 1,   // Minimum allowed FPS
                           MAX_FPS = 120; // Maximum allowed FPS
  
  private final double iMin, // Minimum allowed intensity for intensity range
                       iMax; // Maximum allowed intensity for intensity range
  
  private int fps;       // Current fps value
  private int fpsUpdate; // When the UI updates the frame rate, this property is changed

  private final int clock; // Clock speed
  
  private PixelFire pf; // Stores the next pixel to display
  
  private int tNow; // Current time
  
  private final GrayBufferedImage image;
  
  private JFrame  frame;       // JFrame to house the player
  private JLabel  iconLabel;   // Label to house the image
  private JSlider fpsControl,  // Slider that controls the player fps
                  iMinControl, // Slider that controls the largest intensity that should map to 0
                  iMaxControl; // Slider that controls the smallest intensity that should map to 255
  
  private Timer timer; // Timer to periodically call run()
  
  private final Source<PixelFire> input; // Source of PixelFire objects to play
  
  private IntensityTransform intensityTransform; // Delegate intensity transformation to an IntensityTransform object
  
  private static final int Q = Integer.MAX_VALUE/2; // Factor used for dealing with integer overflow
  
  
  
  /**
   * Constructor
   */
  public FramelessPlayer(int w, int h, int clock, int fps, double iMin, double iMax, Source<PixelFire> input) {
    this.clock = clock;
    this.fps   = fps;
    this.iMin  = iMin;
    this.iMax  = iMax;
    this.input = input;
    
    intensityTransform = new LinearIntensityTransform(clock, iMin, iMax);
    image              = new GrayBufferedImage(w, h, GrayBufferedImage.TYPE_INT_RGB);
  }
  
  
  
  /**
   * Runnable run() method. Displays the prepared frame and prepares the next
   *   one.
   */
  @Override
  public void run() {
    // Handle changes in fps
    if (fpsUpdate != fps) {
      fps = fpsUpdate;
      restartTimer();
      return;
    }
    
    // Advance current time
    tNow += ticksPerFrame();
    
    // Prepare next frame
    //   Keep sending pixels to the image while (tShow < tNow)
    //   Check for possible overflow
    while (pf.getTShow() < tNow && (pf.getTShow() > -Q || tNow < Q) || pf.getTShow() > Q && tNow < -Q) {
      image.setGray(pf.getX(), pf.getY(), intensityTransform.toInt(pf.getDt(), pf.getD()));
      pf = input.next();
    }
    
    // Show prepared frame
    frame.repaint();
  }
  
  
  
  /**
   * ChangeListener stateChanged() method; executed when UI elements such as
   *   sliders are changed
   */
  @Override
  public void stateChanged(ChangeEvent e) {
    fpsUpdate = fpsControl.getValue();
    intensityTransform = new LinearIntensityTransform(clock, iMinControl.getValue(), iMaxControl.getValue());
  }
  
  
  /**
   * Starts the video player
   */
  public void start() {
    // Show the player window
    makePlayerWindow();
    
    // Priming read
    pf = input.next();
    
    // Begin running the timer
    fpsUpdate = fps;
    restartTimer();
  }
  public void stop() {
    if (timer != null) timer.cancel();
  }
  
  
  /**
   * Starts or restarts the timer
   */
  private void restartTimer() {
    if (timer != null) timer.cancel();
    timer = new Timer("Player", true);
    timer.scheduleAtFixedRate(new TimerTaskShell(this), new Date(), msPerFrame());
  }
  
  
  /**
   * Builds and displays the player JFrame Window
   */
  private void makePlayerWindow() {
    // Create and set JLabel to hold the image
    iconLabel = new JLabel(new ImageIcon(image));
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
    iMinControl.setMajorTickSpacing((int) (iMax - iMin)/5);
    iMinControl.setMinorTickSpacing((int) (iMax - iMin)/50);
    iMinControl.setPaintTicks(true);
    iMinControl.setPaintLabels(true);
    iMinControl.setBorder(new EmptyBorder(0, 20, 20, 20)); // top, left, bottom, right
    iMinControl.setFont(new Font("Serif", Font.ITALIC, 30));
    iMinControl.setAlignmentX(Component.CENTER_ALIGNMENT);
    iMinControl.addChangeListener(this);
    
    // Create and set JLabel to hold the max intensity slider
    iMaxControl = new JSlider(JSlider.HORIZONTAL, (int) iMin, (int) iMax, (int) iMax);
    iMaxControl.setMajorTickSpacing((int) (iMax - iMin)/5);
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
}
