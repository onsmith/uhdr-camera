package com.onsmith.unc.uhdr;


import java.io.File;
import java.io.IOException;

import org.jcodec.api.SequenceEncoder;
import org.jcodec.common.model.Picture;

import static org.jcodec.common.model.ColorSpace.GREY;


public class GrayscaleSequenceEncoder extends SequenceEncoder {
  public GrayscaleSequenceEncoder(File out) throws IOException {
    super(out);
  }
  
  
  public void encodeGrayscaleImage(int[][] frame, int offset) throws IOException {
    encodeNativeFrame(makeGrayscalePicture(frame, offset));
  }
  
  
  private Picture makeGrayscalePicture(int[][] frame, int offset) {
    int width  = frame.length,
        height = frame[0].length;
    Picture picture = Picture.create(width, height, GREY);
    int[] pictureData = picture.getPlaneData(0);
    
    int cursor = 0;
    for (int i=0; i<height; i++) {
      for (int j=0; j<width; j++) {
        pictureData[cursor++] = (frame[j][i] >> offset) & 0xff;
      }
    }
    
    return picture;
  }
}
