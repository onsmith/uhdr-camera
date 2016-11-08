package com.onsmith.unc.uhdr.scripts;

import java.io.File;
import java.io.IOException;

import org.jcodec.api.JCodecException;

import com.onsmith.unc.uhdr.FramedPlayer;
import com.onsmith.unc.uhdr.IntFrame;
import com.onsmith.unc.uhdr.MP4Reader;
import com.onsmith.unc.uhdr.Source;

public class DecodeAndPlayFramedUHDRStream {
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
				new File("out3.mp4"),
				new File("out2.mp4"),
				new File("out1.mp4")
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
