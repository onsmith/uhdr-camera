package com.onsmith.unc.uhdr.scripts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.jcodec.api.JCodecException;

import com.onsmith.unc.uhdr.CanonicalMP4Reader;
import com.onsmith.unc.uhdr.IntFrame;
import com.onsmith.unc.uhdr.PixelFire;
import com.onsmith.unc.uhdr.PixelFireReader;
import com.onsmith.unc.uhdr.Source;
import com.onsmith.unc.uhdr.UhdrFrameStream;

public class CalculatePSNR {
  public static void main(String[] args) throws FileNotFoundException, IOException, JCodecException {
    int w = 716,
        h = 500;
    
    
    // Source from MP4 file
    Source<IntFrame> mp4Stream = new CanonicalMP4Reader(new File[] {
      new File("out/out3.mp4"),
      new File("out/out2.mp4"),
      new File("out/out1.mp4"),
    }, w, h);
    
    
    // Source from data file
    Source<PixelFire> rawStreamFromDisk = new PixelFireReader(new FileInputStream(new File("out/raw.data")), w, h);
    Source<IntFrame>  rawStream = new UhdrFrameStream(rawStreamFromDisk, w, h);
    
    
    for (int i=0; i<1023; i++) {
      IntFrame rawFrame = rawStream.next(),
               mp4Frame = mp4Stream.next();
      double mse = 0;
      for (int x=0; x<w; x++) {
        for (int y=0; y<h; y++) {
          double err = toIntensity(rawFrame.getPixel(x,y), 15) - toIntensity(mp4Frame.getPixel(x,y) >>> 1, 15);
          mse += err*err;
          //if (err != 0) {
            //System.out.println(err);
            //System.out.printf("%8s %8s\n", Integer.toBinaryString(rawFrame.getPixel(x,y)), Integer.toBinaryString(mp4Frame.getPixel(x,y) >>> 1));
          //}
          //System.out.println(rawFrame.getPixel(x,y) - mp4Frame.getPixel(x,y));
          //System.out.printf("%s %s\n", Integer.toBinaryString(rawFrame.getPixel(x,y)), Integer.toBinaryString(mp4Frame.getPixel(x,y)));
        }
      }
      mse /= w*h;
      double psnr = 20*Math.log10(1000000) - 10*Math.log10(mse);
      System.out.printf("%4d: %10f\t%10f\n", i, psnr, mse);
    }
  }
  
  
  private static double toIntensity(int dt, int d) {
    return dt != 0 ? ((double) (0x1 << d)) / dt : 0;
  }
}
