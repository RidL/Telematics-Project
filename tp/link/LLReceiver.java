package tp.link;

import tp.test.TestHLR;
import tp.util.Log;
import lpt.Lpt;

public class LLReceiver {

    private static final int INITIAL_VALUE = 10;
    private Lpt lpt;
    private HLR hlr;
    private boolean alt;
    private boolean frameReceived;
    private int tmp;
    private boolean validFrame;
    private boolean readingFrame;
    private Frame f;
    private byte[] data;
    private int offset;
    private byte header;
    private boolean rcvedAck;
    
    private boolean sysoutLog = false;
    public LLReceiver(HLR hlr) {
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
        Log.writeLog(" LLR", Integer.toString(((tmp >> 3) & 0x1f) ^ 0x10), sysoutLog);
        rcvedAck = false;
    }


	public Frame read() {
        f = null;
        frameReceived = false;


        while (!frameReceived) {
            if (lpt.readLPT() != tmp || (hlr.expectingAck()&&tmp==Frame.ONES&&!rcvedAck)) {
                for (int z = 0; z < 2; z++) {
                    int y = 3;
                    y++;
                }
                tmp = lpt.readLPT();
                Log.writeLog(" LLR", "INC: " + Integer.toString(((tmp >> 3) & 0x1f) ^ 0x10), sysoutLog);
                if ((tmp == Frame.ONES) && !validFrame && readThisFrame()) {
                    validFrame = true;
                    rcvedAck = true;
                }
                if (validFrame) {
                    sendResponse();
                    bitInterpret(tmp);
                }
            }
        }
        Log.writeLog(" LLR", "frame received", sysoutLog);
        return f;

    }

    private void sendResponse() {
        if (alt) {
            lpt.writeLPT(4);
            Log.writeLog(" LLR", "OUT: 4", sysoutLog);
        } else {
            lpt.writeLPT(10);
            Log.writeLog(" LLR", "OUT: 4", sysoutLog);
        }
        alt = (!alt);
    }

    private void bitInterpret(int i) {
        if ((i == Frame.ONES) && readingFrame) {
            if (offset < 51) {
            	if(checkParity()){
            		Log.writeLog(" LLR", "new frame, offset: " + offset, sysoutLog);
            		f = new Frame(data, header);
            		data[5] = 0;
            		data[6] = 0;
            	}else{
            		Log.writeLog("LLR", "parity failed", sysoutLog);
            	}
            	boolean temp = true;
            	while(temp){
            	 if (lpt.readLPT() != tmp) {
                     for (int z = 0; z < 2; z++) {
                         int y = 3;
                         y++;
                     }
                     tmp = lpt.readLPT();
                     Log.writeLog(" LLR", "IN: " + (((tmp >> 3) & 0x1f) ^ 0x10), sysoutLog);
                     sendResponse();
                     temp =  false;
            	 }
            	}
            }
            frameReceived = true;
            offset = 0;
            readingFrame = false;
        } else if ((i != Frame.ZEROS) && (i != Frame.ONES) && offset <= 50) { //shift(i)!=0 && shift(i)!=31
            if (!readingFrame) {
                header = (byte) (i^0x80);
                readingFrame = true;
            } else {
                Frame.bitConcat(data, (byte) (i^0x80), offset);
                offset = offset + 5;
            }
        }
    }

    private boolean checkParity() {
		
    	byte[] tempData = new byte[8];
		tempData[0] = (byte)(header&-64);
		boolean ret = false;
		for(int i = 1; i<=data.length;i++){
			tempData[i]= data[i-1];
		}
		if(((byte)(header&32)>0) == (Frame.parity(tempData, 0, 4)==1)){
			ret =  true;
		}
		
		if((((byte)(header&16)>0) == (Frame.parity(tempData, 4,8)==0)) && ret){
			//in data 3 1s are appended (never removed) from readLPT(), hence parity calculated needs to be inverted
			ret =  true;
		}else{
			ret = false;
		}
		return ret;
	}

	public void setInvalidFrame() {
        validFrame = false;
        rcvedAck = false;
        Log.writeLog(" LLR", "invalid frame - now ignoring data", sysoutLog);
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

