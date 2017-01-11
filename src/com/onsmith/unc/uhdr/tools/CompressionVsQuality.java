package com.onsmith.unc.uhdr.tools;

import java.io.File;
import java.io.IOException;

public class CompressionVsQuality {
  static final String[] SOURCEVIDS = new String[] {"out1.yuv", "out2.yuv", "out3.yuv"};
  static final int[]    QFACTORS   = new int[] {0, 14, 28, 42, 69};
  static final int      GOPSIZE    = 1024;
  static final String   TEMPFILE   = "tmpvid.mkv";
  
  public static void main(String[] args) throws IOException, InterruptedException {
    Runtime rt = Runtime.getRuntime();
    for (int qf : QFACTORS) {
      for (int gop=0; gop<10; gop++) {
        int compressedSize = 0;
        for (String source : SOURCEVIDS) {
          Process pr = rt.exec("out/x264 --seek " + gop*GOPSIZE + " --frames " + GOPSIZE + " --qp " + qf + " --input-res 1500x1046 --keyint infinite --fps 24 -o " + TEMPFILE + " out/" + source);
          pr.waitFor();
          File video = new File(TEMPFILE);
          compressedSize += video.length();
          video.delete();
        }
        System.out.printf("%d\t%d\n", qf, compressedSize);
      }
    }
  }
}
