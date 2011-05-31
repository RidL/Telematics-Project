package tp.link;

import tp.util.Log;
import lpt.Lpt;

public class LLReceiver {
    private static final int INITIAL_VALUE = 10;
    private static final int LL_SLEEP_TIME = 200;
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
    private boolean rcvedAck;
    private long timeoutCount;
    
    private boolean sysoutLog = false;
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
        Log.writeLog(" LLR", Integer.toString(((tmp >> 3) & 0x1f) ^ 0x10), sysoutLog);
        rcvedAck = false;
    }


	public Frame read() {
        f = null;
        frameReceived = false;
        timeoutCount = System.currentTimeMillis();
        while (!frameReceived) {
            if (lpt.readLPT() != tmp || (hlr.expectingAck()&&tmp==Frame.ONES&&!rcvedAck) || (((timeoutCount+LL_SLEEP_TIME)<System.currentTimeMillis())&&validFrame)) {
            	timeoutCount = System.currentTimeMillis();
//            	if((((timeoutCount+LL_SLEEP_TIME)<System.currentTimeMillis())&&validFrame)){
//            		Log.writeLog(" LLR", "LL TIMEOUT", sysoutLog);
//            		timeoutCount+=500;
//            	}
            	microSleep();
                tmp = lpt.readLPT();
                Log.writeLog(" LLR", "INC: " + Integer.toString(((tmp >> 3) & 0x1f) ^ 0x10), sysoutLog);
                if ((tmp == Frame.ONES) && !validFrame && readThisFrame()) {
                    validFrame = true;
                    rcvedAck = true;
                    hlr.resetTimer();
                }
                if (validFrame) {
                    sendResponse();
                    bitInterpret(tmp);
                }
            }
            if(hlr.timeOut()&&validFrame){
            	Log.writeLog(" LLR", "Timeout while waiting", sysoutLog);
            	break;
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
            Log.writeLog(" LLR", "OUT: 10", sysoutLog);
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
                     microSleep();
                     tmp = lpt.readLPT();
                     Log.writeLog(" LLR", "IN: " + (((tmp >> 3) & 0x1f) ^ 0x10), sysoutLog);
                     sendResponse();
                     temp =  false;
            	 }
            	}
            }else{
            	Log.writeLog("LLR",  "OVERFLOWAGE",sysoutLog);
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
    
    private static void microSleep(){
    	for (int z = 0; z < 2; z++) {
            @SuppressWarnings("unused")
			int y = 3;
            y++;
        }
    }
}

