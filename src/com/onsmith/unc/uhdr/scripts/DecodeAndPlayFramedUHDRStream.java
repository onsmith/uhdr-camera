package com.onsmith.unc.uhdr.scripts;

import java.io.File;
import java.io.IOException;

import org.jcodec.api.JCodecException;

import com.onsmith.unc.uhdr.IntFramePlayer;
import com.onsmith.unc.uhdr.IntFrame;
import com.onsmith.unc.uhdr.CanonicalMP4Reader;
import com.onsmith.unc.uhdr.Source;

public class DecodeAndPlayFramedUHDRStream {
	private static final int clock = (0x1 << 10), // Camera clock speed, in hertz
	                         fps   = 120,         // Initial frame rate of player, in hertz
	                         iMin  = 0,           // Minimum for player intensity range
	                         iMax  = 1000000;     // Maximum for player intensity range
	
	
	public static void main(String[] args) throws IOException {
		try {
			int w = 716;
			int h = 500;
			
			// Source<IntFrame>
			Source<IntFrame> framedStream = new CanonicalMP4Reader(new File[] {
				new File("out/out3.mp4"),
				new File("out/out2.mp4"),
				new File("out/out1.mp4"),
			}, w, h);
			
			// Player
			IntFramePlayer player = new IntFramePlayer(
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
