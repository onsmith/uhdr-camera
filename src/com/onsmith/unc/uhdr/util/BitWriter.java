package com.onsmith.unc.uhdr.util;

import java.io.IOException;
import java.io.OutputStream;

public class BitWriter {
  private final OutputStream stream;
  private int buffer;
  private int bitsInBuffer;
  
  public BitWriter(OutputStream stream) {
    this.stream = stream;
    this.buffer = 0;
    this.bitsInBuffer   = 0;
  }
  
  public void write(int bits, int numBits) throws IOException {
    if (numBits + bitsInBuffer == 8) {
      buffer = (buffer << numBits) | (bits & ((0x1 << numBits) - 1));
      flush();
    }
    else if (numBits + bitsInBuffer < 8) {
      buffer = (buffer << numBits) | (bits & ((0x1 << numBits) - 1));
      bitsInBuffer = numBits + bitsInBuffer;
    }
    else {
      // Write (8 - size) bits to empty buffer
      numBits -= (8 - bitsInBuffer);
      write(bits, 8 - bitsInBuffer);
      bits >>= (8 - bitsInBuffer);
      
      // Write full bytes
      while (numBits > 8) {
        stream.write(bits);
        bits >>= 8;
        numBits -= 8;
      }
      
      // Write remaining bits
      write(bits, numBits);
    }
  }
  
  public void flush() throws IOException {
    stream.write(buffer);
    this.buffer = 0;
    this.bitsInBuffer   = 0;
  }
  
  public void close() throws IOException {
    stream.close();
  }
}