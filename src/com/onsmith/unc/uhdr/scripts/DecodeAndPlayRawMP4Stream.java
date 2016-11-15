package com.onsmith.unc.uhdr.scripts;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.jcodec.api.JCodecException;

import com.onsmith.unc.uhdr.RawMP4Reader;
import com.onsmith.unc.uhdr.BufferedImagePlayer;
import com.onsmith.unc.uhdr.Source;

public class DecodeAndPlayRawMP4Stream {
  private static final int fps = 30; // Initial frame rate of player, in hertz
  
  public static void main(String[] args) throws IOException {
    try {
      // Source<IntFrame>
      Source<BufferedImage> framedStream = new RawMP4Reader(new File("out/out1.mp4"));
      
      // Player
      BufferedImagePlayer player = new BufferedImagePlayer(framedStream, fps);
      player.start();
    } catch (JCodecException e) {
      e.printStackTrace();
    }
  }
}