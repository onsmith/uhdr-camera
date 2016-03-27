package com.onsmith.unc.uhdr;

import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class ThreadedIterator<E> implements Iterator<E>, Runnable {
  private final Iterator<E>      iterator;
  private final BlockingQueue<E> queue;
  
  private final static int BUFFER_SIZE = 10000;
  
  
  public ThreadedIterator(Iterator<E> iterator) {
    this.iterator = iterator;
    queue = new LinkedBlockingQueue<E>(BUFFER_SIZE);
    
    Thread thread = new Thread(this, "RunnableIterator");
    thread.start();
  }
  
  
  @Override
  public void run() {
    try {
      while (iterator.hasNext()) {
        queue.put(iterator.next());
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
  
  
  @Override
  public boolean hasNext() {
    return iterator.hasNext() || !queue.isEmpty();
  }
  
  
  @Override
  public E next() {
    try {
      return queue.take();
    } catch (InterruptedException e) {
      e.printStackTrace();
      return null;
    }
  }
}
