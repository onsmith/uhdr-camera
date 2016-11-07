package com.onsmith.unc.uhdr;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.jcodec.api.JCodecException;
import org.jcodec.api.awt.FrameGrab;

public class PlayMP4EncodedAquariumOutput {
	private static final int clock = (0x1 << 10), // Camera clock speed, in hertz
	                         fps   = 30,          // Initial frame rate of player, in hertz
	                         iMin  = 0,           // Minimum for player intensity range
	                         iMax  = 1000000;     // Maximum for player intensity range
	
	
	public static void main(String[] args) throws IOException {
		try {
			int w = 717;
			int h = 500;
			
			// Source<IntFrame>
			Source<IntFrame> framedStream = new MP4Reader(new File[] {
				new File("out1.mp4"),
				new File("out2.mp4"),
				new File("out3.mp4")
			}, w, h);
			
			// Player
			FramedPlayer player = new FramedPlayer(
				w, h,
				clock, fps, iMin, iMax,
				framedStream
			);
			player.start();
		} catch (JCodecException e) {
			e.printStackTrace();
		}
	}
}
