package com.onsmith.unc.uhdr.jcodec;

import org.jcodec.codecs.h264.encode.RateControl;
import org.jcodec.codecs.h264.io.model.SliceType;

/**
 * This class is part of JCodec ( www.jcodec.org ) This software is distributed
 * under FreeBSD License
 *
 * Dumb rate control policy, always maintains the same QP for the whole video
 * 
 * @author The JCodec project
 * 
 */
public class DumbRateControl implements RateControl {
    private final int qp;
    
    public DumbRateControl(int qp) {
      this.qp = qp;
    }
    
    @Override
    public int getInitQp(SliceType sliceType) {
        return qp + (sliceType == SliceType.P ? 6 : 0);
    }

    @Override
    public int getQpDelta() {
        return 0;
    }

    @Override
    public boolean accept(int bits) {
        return true;
    }

    @Override
    public void reset() {
        // Do nothing, remember we are dumb
    }
}