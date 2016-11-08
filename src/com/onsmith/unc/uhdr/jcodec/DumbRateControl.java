package com.onsmith.unc.uhdr.jcodec;

import org.jcodec.codecs.h264.encode.RateControl;


public class DumbRateControl implements RateControl {
    private final int QP;

    public DumbRateControl(int QP) {
        this.QP = QP;
    }

    @Override
    public int getInitQp() {
        return QP;
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
