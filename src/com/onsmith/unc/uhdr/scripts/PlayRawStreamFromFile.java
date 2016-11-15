package com.onsmith.unc.uhdr.scripts;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.onsmith.unc.uhdr.PixelFirePlayer;
import com.onsmith.unc.uhdr.PixelFire;
import com.onsmith.unc.uhdr.PixelFireReader;
import com.onsmith.unc.uhdr.Source;

public class PlayRawStreamFromFile {
  private static final int clock = (0x1 << 10), // Camera clock speed, in hertz
                           fps   = 25,          // Initial frame rate of player, in hertz
                           iMin  = 0,           // Minimum for player intensity range
                           iMax  = 1000000;     // Maximum for player intensity range
  
  
  public static void main(String[] args) throws IOException {
    // Width, height
    int w = 717,
        h = 500;
    
    // Source<PixelFire>
    Source<PixelFire> rawStreamFromDisk = new PixelFireReader(new FileInputStream(new File("out/raw.data")), w, h);
    
    // Player
    PixelFirePlayer player = new PixelFirePlayer(
      w, h,
      clock, fps, iMin, iMax,
      rawStreamFromDisk
    );
    player.start();
  }
}
