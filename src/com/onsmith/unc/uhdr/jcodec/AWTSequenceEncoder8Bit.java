package com.onsmith.unc.uhdr.jcodec;
import org.jcodec.codecs.h264.H264Encoder;
import org.jcodec.codecs.h264.H264Utils;
import org.jcodec.codecs.h264.io.model.NALUnit;
import org.jcodec.codecs.h264.io.model.NALUnitType;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.io.SeekableByteChannel;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture8Bit;
import org.jcodec.common.model.Rational;
import org.jcodec.containers.mp4.Brand;
import org.jcodec.containers.mp4.MP4Packet;
import org.jcodec.containers.mp4.TrackType;
import org.jcodec.containers.mp4.muxer.FramesMP4MuxerTrack;
import org.jcodec.containers.mp4.muxer.MP4Muxer;
import org.jcodec.scale.AWTUtil;
import org.jcodec.scale.ColorUtil;
import org.jcodec.scale.Transform8Bit;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * This class is part of JCodec ( www.jcodec.org ) This software is distributed
 * under FreeBSD License
 * 
 * @author The JCodec project
 */
public class AWTSequenceEncoder8Bit {
    private SeekableByteChannel ch;
    private Picture8Bit toEncode;
    private Transform8Bit transform;
    private H264Encoder encoder;
    private ArrayList<ByteBuffer> spsList;
    private ArrayList<ByteBuffer> ppsList;
    private FramesMP4MuxerTrack outTrack;
    private ByteBuffer _out;
    private int frameNo;
    private MP4Muxer muxer;
    private ByteBuffer sps;
    private ByteBuffer pps;
    private int timestamp;
    private Rational fps;
    
    
    public void encodeImage(BufferedImage bi) throws IOException {
        encodeNativeFrame(AWTUtil.fromBufferedImageRGB8Bit(bi));
    }

    public static AWTSequenceEncoder8Bit createSequenceEncoder8Bit(File out, int fps, int q) throws IOException {
        return new AWTSequenceEncoder8Bit(NIOUtils.writableChannel(out), Rational.R(fps, 1), q);
    }
    
    public AWTSequenceEncoder8Bit(SeekableByteChannel ch, Rational fps, int q) throws IOException {
        this.ch = ch;
        this.fps = fps;

        // Muxer that will store the encoded frames
        muxer = MP4Muxer.createMP4Muxer(ch, Brand.MP4);

        // Add video track to muxer
        outTrack = muxer.addTrack(TrackType.VIDEO, fps.getNum());

        // Allocate a buffer big enough to hold output frames
        _out = ByteBuffer.allocate(1920 * 1080 * 6);

        // Create an instance of encoder
        encoder = new H264Encoder(new DumbRateControl(q));

        // Transform to convert between RGB and YUV
        transform = ColorUtil.getTransform8Bit(ColorSpace.RGB, encoder.getSupportedColorSpaces()[0]);

        // Encoder extra data ( SPS, PPS ) to be stored in a special place of
        // MP4
        spsList = new ArrayList<ByteBuffer>();
        ppsList = new ArrayList<ByteBuffer>();
    }
    
    /**
     * Encodes a frame into a movie.
     * @param pic
     * @throws IOException
     */
    public void encodeNativeFrame(Picture8Bit pic) throws IOException {
        if (toEncode == null) {
            toEncode = Picture8Bit.create(pic.getWidth(), pic.getHeight(), encoder.getSupportedColorSpaces()[0]);
        }

        // Perform conversion
        transform.transform(pic, toEncode);

        // Encode image into H.264 frame, the result is stored in '_out' buffer
        _out.clear();
        ByteBuffer result = encoder.encodeFrame8Bit(toEncode, _out);

        // Based on the frame above form correct MP4 packet
        spsList.clear();
        ppsList.clear();
        H264Utils.wipePSinplace(result, spsList, ppsList);
        NALUnit nu = NALUnit.read(NIOUtils.from(result.duplicate(), 4));
        H264Utils.encodeMOVPacket(result);

        // We presume there will be only one SPS/PPS pair for now
        if (sps == null && spsList.size() != 0)
            sps = spsList.get(0);
        if (pps == null && ppsList.size() != 0)
            pps = ppsList.get(0);

        // Add packet to video track
        outTrack.addFrame(MP4Packet.createMP4Packet(result, timestamp, fps.getNum(), fps.getDen(), frameNo,
                nu.type == NALUnitType.IDR_SLICE, null, 0, timestamp, 0));
        
        timestamp += fps.getDen();
        frameNo++;
    }

    public H264Encoder getEncoder() {
        return encoder;
    }

    public void finish() throws IOException {
        if (sps == null || pps == null)
            throw new RuntimeException(
                    "Somehow the encoder didn't generate SPS/PPS pair, did you encode at least one frame?");
        // Push saved SPS/PPS to a special storage in MP4
        outTrack.addSampleEntry(H264Utils.createMOVSampleEntryFromBuffer(sps, pps, 4));

        // Write MP4 header and finalize recording
        muxer.writeHeader();
        NIOUtils.closeQuietly(ch);
    }
}