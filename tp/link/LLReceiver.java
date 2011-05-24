package tp.link;

import lpt.Lpt;

public class LLReceiver {

    private static final int INITIAL_VALUE = 10;
    private Lpt lpt;
    private HLReceiver hlr;
    private boolean alt;
    private boolean frameReceived;
    private int tmp;
    private boolean validFrame;
    private boolean readingFrame;
    private Frame f;
    private byte[] data;
    private int offset;
    private byte header;

    public LLReceiver(HLReceiver hlr) {
        this.hlr = hlr;
        lpt = new Lpt();
        alt = true;
        f = null;
        frameReceived = false;
        validFrame = false;
        readingFrame = false;
        data = new byte[7];
        offset = 0;
        header = 0;
        lpt.writeLPT(INITIAL_VALUE);
        tmp = lpt.readLPT();
        System.out.println("LLR: Write initial value>: 10");
        int i = ((tmp >> 3) & 0x1f) ^ 0x10;
        System.out.println("LLR: Read initial value: " + i);
    }

    public Frame read() {
        f = null;
        frameReceived = false;


        while (!frameReceived) {
            if (lpt.readLPT() != tmp) {
                for (int z = 0; z < 2; z++) {
                    @SuppressWarnings("unused")
                    int y = 3;
                    y++;
                }
                tmp = lpt.readLPT();
                System.out.println("LLR: INC: " + (((tmp >> 3) & 0x1f) ^ 0x10));
                //   System.out.println((tmp == Frame.ONES) +"-" + !validFrame  +"-" + readThisFrame());
                if ((tmp == Frame.ONES) && !validFrame && readThisFrame()) {
                    System.out.println("LLR: set FrameValid!");
                    validFrame = true;
                }
                if (validFrame) {
                    sendResponse();
                    bitInterpret(tmp);
                }
            }
        }
        System.out.println("LLR:<!--Frame received--!>");
        System.out.println("LLR: " + Frame.toBinaryString(f.getBytes()));
        return f;

    }

    private void sendResponse() {
        if (alt) {
            lpt.writeLPT(4);
            System.out.println("LLR: OUT: 4");
            alt = false;
        } else {
            lpt.writeLPT(10);
            System.out.println("LLR: OUT: 10");
            alt = true;
        }

    }

    private void bitInterpret(int i) {
        if ((i == Frame.ONES) && readingFrame) {
            if (offset < 51) {
                System.out.println("LLR: new Frame offset: " + offset + " data: " + data.length);
                f = new Frame(data, header);
            }
            frameReceived = true;
            offset = 0;
            readingFrame = false;
        } else if ((i != Frame.ZEROS) && (i != Frame.ONES) && offset <= 50) { //shift(i)!=0 && shift(i)!=31
            if (!readingFrame) {
                System.out.println("Setting Header");
                header = (byte) (i^0x80);
                readingFrame = true;
            } else {
                System.out.println(Frame.toBinaryString((byte)i));
                System.out.println(Frame.toBinaryString(data));
                Frame.bitConcat(data, (byte) (i^0x80), offset);
                System.out.println(Frame.toBinaryString(data));
                offset = offset + 5;
            }
        }
    }

    public void setInvalidFrame() {
        validFrame = false;
        System.out.println("LLR: Frame got set invalid");
    }

    public boolean readThisFrame() {
        boolean ret = false;
        if (hlr.inSenderActiveMode()) {
            if (hlr.expectingAck()) {
                ret = true;
            } else {
                ret = false;
            }
        } else {
            hlr.setReceivingMode(true);
            ret = true;
        }
        return ret;
    }
}

