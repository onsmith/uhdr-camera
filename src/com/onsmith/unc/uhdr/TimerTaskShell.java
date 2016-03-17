package com.onsmith.unc.uhdr;

import java.util.TimerTask;


public class TimerTaskShell extends TimerTask {
  private final Runnable task;
  
  public TimerTaskShell(Runnable task) {
    super();
    this.task = task;
  }
  
  public void run() {
    task.run();
  }
}
