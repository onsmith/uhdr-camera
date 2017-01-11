package com.onsmith.unc.uhdr.scripts;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.onsmith.unc.uhdr.CanonicalYUVWriter;
import com.onsmith.unc.uhdr.IntFrame;
import com.onsmith.unc.uhdr.PixelFire;
import com.onsmith.unc.uhdr.PixelFireReader;
import com.onsmith.unc.uhdr.Source;
import com.onsmith.unc.uhdr.UhdrFrameStream;

public class FramelessToCanonicalYUV {
  private static final int w = 1500, // 717
                           h = 1046; // 500
  
  
  public static void main(String[] args) throws IOException {
    // Source<PixelFire>
    Source<PixelFire> diskStream = new PixelFireReader(new FileInputStream(new File("out/rawHD.data")), w, h);
    
    // Source<IntFrame>
    Source<IntFrame> imageStream = new UhdrFrameStream(diskStream, w, h);
    
    // Sink<IntFrame>
    CanonicalYUVWriter imageWriter = new CanonicalYUVWriter(new File[] {
      new File("out/out1.yuv"),
      new File("out/out2.yuv"),
      new File("out/out3.yuv")
    });
    
    // Pipe source to sink
    int numFrames = (int) 1024*10;
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
