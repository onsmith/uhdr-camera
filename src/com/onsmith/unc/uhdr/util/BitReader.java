package com.onsmith.unc.uhdr.util;

import java.io.InputStream;

class BitReader {
  private final InputStream stream;
  private int buffer;
  private int size;
  
  public BitReader(InputStream stream) {
    this.stream = stream;
    this.buffer = 0;
    this.size   = 0;
  }
  
  public int read(int numBits) {
    if (numBits <= size) {
      
    }
  }
}