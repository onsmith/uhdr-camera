package com.onsmith.unc.uhdr.scripts;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.onsmith.unc.uhdr.CanonicalMP4Writer;
import com.onsmith.unc.uhdr.IntFrame;
import com.onsmith.unc.uhdr.PixelFire;
import com.onsmith.unc.uhdr.PixelFireReader;
import com.onsmith.unc.uhdr.Source;
import com.onsmith.unc.uhdr.UhdrFrameStream;

public class GenerateAndEncodeFramedUHDRStream {
  private static final int fps   = 1024,        // Frame rate for encoded video
                           q     = 51,          // Q-factor for encoded video
                           w     = 1500, // 717
                           h     = 1046; // 500
  
  
  public static void main(String[] args) throws IOException {
    // Source<PixelFire>
    Source<PixelFire> diskStream = new PixelFireReader(new FileInputStream(new File("out/rawHD.data")), w, h);
    
    // Source<BufferedImage>
    Source<IntFrame> imageStream = new UhdrFrameStream(diskStream, w, h);
    
    // Sink<BufferedImage>
    CanonicalMP4Writer imageWriter = new CanonicalMP4Writer(new File[] {
      new File("out/out1.mp4"),
      new File("out/out2.mp4"),
      new File("out/out3.mp4")
    }, 25, q);
    
    // Pipe source to sink
    int numFrames = (int) (10 * fps);
    for (int i=0; i<numFrames; i++) {
      imageWriter.send(imageStream.next());
      if (i%10 == 9) {
        System.out.println("Frame " + (i+1) + " of " + numFrames + ".");
      }
    }
    imageWriter.close();
    System.out.println("Finished.");
  }
}
