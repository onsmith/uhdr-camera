package com.onsmith.unc.uhdr;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.image.BufferedImage;
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


public class BufferedImagePlayer implements Runnable, ChangeListener {
  private static final int MIN_FPS = 1,   // Minimum allowed FPS
                           MAX_FPS = 120; // Maximum allowed FPS
  
  private int fps;       // Current fps value
  private int fpsUpdate; // When the UI updates the frame rate, this property is changed
  
  private final BufferedImage image; // Image being displayed
  
  private JFrame  frame;       // JFrame to house the player
  private JLabel  iconLabel;   // Label to house the image
  private JSlider fpsControl;  // Slider that controls the player fps
  
  private Timer timer; // Timer to periodically call run()
  
  private final Source<BufferedImage> input; // Source of frames to play
  
  
  
  /**
   * Constructor
   */
  public BufferedImagePlayer(Source<BufferedImage> input, int fps) {
    this.fps   = fps;
    this.input = input;
    this.image = input.next();
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
    
    // Update frame
    image.setData(input.current().getData());
    input.next();
    
    // Show frame
    frame.repaint();
  }
  
  
  
  /**
   * ChangeListener stateChanged() method; executed when UI elements such as
   *   sliders are changed
   */
  @Override
  public void stateChanged(ChangeEvent e) {
    fpsUpdate = fpsControl.getValue();
  }
  
  
  /**
   * Starts the video player
   */
  public void start() {
    // Show the player window
    makePlayerWindow();
    
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
    
    // Create, configure, and show JFrame
    frame = new JFrame("BufferedImage Player");
    frame.setMinimumSize(new Dimension(800, 0)); // w, h
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
    frame.getContentPane().add(iconLabel);
    frame.getContentPane().add(fpsControl);
    frame.pack();
    frame.setLocationRelativeTo(null); // Passing a null component causes the window to be placed in the center of the screen
    frame.setVisible(true);
  }
  
  
  /**
   * Calculates the number of milliseconds that should elapse between frames
   */
  private int msPerFrame() {
    return 1000/fps;
  }
}
