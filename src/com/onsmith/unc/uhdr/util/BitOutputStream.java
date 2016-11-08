package com.onsmith.unc.uhdr.util;

import java.io.IOException;
import java.io.OutputStream;

public class BitOutputStream extends OutputStream {
    private static final byte[] bitMasks = new byte[8];
    
    
    static {
        for (byte i = 0; i < 8; i++)
            bitMasks[i] = (byte) (~(0xFF >>> i));
    }
    
    private final OutputStream out;
    
    private int bufferByte = 0;
    private int bufferedNumberOfBits = 0;
    
    
    public BitOutputStream(final OutputStream delegate) {
        this.out = delegate;
    }
    
    
    public void write(final byte b) throws IOException {
        out.write((int) b);
    }
    
    
    @Override
    public void write(final int value) throws IOException {
        out.write(value);
    }
    
    
    @Override
    public String toString() {
        return "DefaultBitOutputStream: "
                + Integer.toBinaryString(bufferByte).substring(0, bufferedNumberOfBits);
    }
    
    
    public void write(final long bitContainer, final int nofBits) throws IOException {
        if (nofBits == 0)
            return;

        if (nofBits < 1 || nofBits > 64)
            throw new IOException("Expecting 1 to 64 bits, got: value=" + bitContainer + ", nofBits=" + nofBits);

        if (nofBits <= 8)
            write((byte) bitContainer, nofBits);
        else {
            for (int i = nofBits - 8; i >= 0; i -= 8) {
                final byte v = (byte) (bitContainer >>> i);
                writeByte(v);
            }
            if (nofBits % 8 != 0) {
                final byte v = (byte) bitContainer;
                write(v, nofBits % 8);
            }
        }
    }
    
    
    void write_int_LSB_0(final int value, final int nofBitsToWrite) throws IOException {
        if (nofBitsToWrite == 0)
            return;

        if (nofBitsToWrite < 1 || nofBitsToWrite > 32)
            throw new IOException("Expecting 1 to 32 bits.");

        if (nofBitsToWrite <= 8)
            write((byte) value, nofBitsToWrite);
        else {
            for (int i = nofBitsToWrite - 8; i >= 0; i -= 8) {
                final byte v = (byte) (value >>> i);
                writeByte(v);
            }
            if (nofBitsToWrite % 8 != 0) {
                final byte v = (byte) value;
                write(v, nofBitsToWrite % 8);
            }
        }
    }
    
    
    public void write(final int bitContainer, final int nofBits) throws IOException {
        write_int_LSB_0(bitContainer, nofBits);
    }
    
    
    private void writeByte(final int value) throws IOException {
        if (bufferedNumberOfBits == 0)
            out.write(value);
        else {
            bufferByte = ((value & 0xFF) >>> bufferedNumberOfBits) | bufferByte;
            out.write(bufferByte);
            bufferByte = (value << (8 - bufferedNumberOfBits)) & 0xFF;
        }
    }
    
    
    public void write(byte bitContainer, final int nofBits) throws IOException {
        if (nofBits < 0 || nofBits > 8)
            throw new IOException("Expecting 0 to 8 bits.");

        if (nofBits == 8)
            writeByte(bitContainer);
        else {
            if (bufferedNumberOfBits == 0) {
                bufferByte = (bitContainer << (8 - nofBits)) & 0xFF;
                bufferedNumberOfBits = nofBits;
            } else {
                bitContainer = (byte) (bitContainer & ~bitMasks[8 - nofBits]);
                int bits = 8 - bufferedNumberOfBits - nofBits;
                if (bits < 0) {
                    bits = -bits;
                    bufferByte |= (bitContainer >>> bits);
                    out.write(bufferByte);
                    bufferByte = (bitContainer << (8 - bits)) & 0xFF;
                    bufferedNumberOfBits = bits;
                } else if (bits == 0) {
                    bufferByte = bufferByte | bitContainer;
                    out.write(bufferByte);
                    bufferedNumberOfBits = 0;
                } else {
                    bufferByte = bufferByte | (bitContainer << bits);
                    bufferedNumberOfBits = 8 - bits;
                }
            }
        }
    }
    
    
    public void write(final boolean bit) throws IOException {
        write(bit ? (byte) 1 : (byte) 0, 1);
    }
    
    
    public void write(final boolean bit, final long repeat) throws IOException {
        for (long i = 0; i < repeat; i++)
            write(bit);
    }
    
    
    @Override
    public void close() throws IOException {
        flush();
        out.close();
    }
    
    
    @Override
    public void flush() throws IOException {
        if (bufferedNumberOfBits > 0)
            out.write(bufferByte);

        bufferedNumberOfBits = 0;
        out.flush();
    }
    
    
    @Override
    public void write(final byte[] b) throws IOException {
        out.write(b);
    }
    
    
    @Override
    public void write(final byte[] b, final int off, final int length) throws IOException {
        out.write(b, off, length);
    }
}