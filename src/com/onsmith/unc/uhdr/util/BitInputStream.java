package com.onsmith.unc.uhdr.util;

import java.io.InputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;

public class BitInputStream extends DataInputStream {
    private int nofBufferedBits = 0;
    private int byteBuffer = 0;
    private boolean throwEOF = false;
    private static final long[] masks = new long[]{0, (1L << 1) - 1, (1L << 2) - 1, (1L << 3) - 1, (1L << 4) - 1,
            (1L << 5) - 1, (1L << 6) - 1, (1L << 7) - 1, (1L << 8) - 1};
    
    
    public BitInputStream(final InputStream in) {
        super(in);
        this.throwEOF = true;
    }
    
    
    public final boolean readBit() throws IOException {
        if (--nofBufferedBits >= 0)
            return ((byteBuffer >>> nofBufferedBits) & 1) == 1;
        
        nofBufferedBits = 7;
        byteBuffer = in.read();
        if (byteBuffer == -1) {
            if (throwEOF)
                throw new EOFException("End of stream.");
        }
        
        return ((byteBuffer >>> 7) & 1) == 1;
    }
    
    
    public final int readBits(int n) throws IOException {
        if (n == 0)
            return 0;
        int x = 0;
        while (n > nofBufferedBits) {
            n -= nofBufferedBits;
            x |= rightBits(nofBufferedBits, byteBuffer) << n;
            byteBuffer = in.read();
            if (byteBuffer == -1) {
                throw new EOFException("End of stream.");
            }
            
            nofBufferedBits = 8;
        }
        nofBufferedBits -= n;
        return x | rightBits(n, byteBuffer >>> nofBufferedBits);
    }
    
    
    private static int rightBits(final int n, final int x) {
        return x & ((1 << n) - 1);
    }
    
    
    public final long readLongBits(int n) throws IOException {
        if (n > 64)
            throw new RuntimeException("More then 64 bits are requested in one read from bit stream.");
        
        if (n == 0)
            return 0;
        
        long x = 0;
        long byteBuffer = this.byteBuffer;
        if (nofBufferedBits == 0) {
            byteBuffer = in.read();
            if (byteBuffer == -1) {
                throw new EOFException("End of stream.");
            }
            nofBufferedBits = 8;
        }
        byteBuffer &= masks[nofBufferedBits];
        while (n > nofBufferedBits) {
            n -= nofBufferedBits;
            x |= byteBuffer << n;
            byteBuffer = in.read();
            if (byteBuffer == -1) {
                throw new EOFException("End of stream.");
            }
            nofBufferedBits = 8;
        }
        nofBufferedBits -= n;
        this.byteBuffer = (int) (byteBuffer & masks[nofBufferedBits]);
        return x | (byteBuffer >>> nofBufferedBits);
    }
    
    
    public void reset() {
        nofBufferedBits = 0;
        byteBuffer = 0;
    }
}