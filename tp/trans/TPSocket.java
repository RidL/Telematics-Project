/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tp.trans;

import tp.util.Log;

/**
 *
 * @author STUDENT\s1012886
 */
public class TPSocket {

    public final static int SEQ_NR_LIMIT = 256;
    public final static int WINDOW_SIZE = 128;
    public final static int ACK_TIMEOUT = 500;
    
    private int seqNr;
    private int ackNr;
    private int lastAcked;
    private int dstAddress;
    private int srcPort;
    private int dstPort;
    private byte[] inBuffer;
    private byte[] outBuffer;
    private final Object OUTLOCK = new Object();
    private final Object INLOCK = new Object();
    //--------------------
    private Segment[] sndBuffer;
    private Segment[] rcvBuffer;
    private int sndWindowBase;
    private int rcvWindowBase;
    
    private long timeCount;
    
    public TPSocket(int dstAddress, int srcPort, int dstPort) {
        seqNr = 0;
        ackNr = 0;
        lastAcked = -1;
        this.dstAddress = dstAddress;
        this.srcPort = srcPort;
        this.dstPort = dstPort;

        timeCount = Long.MAX_VALUE-500;

        sndBuffer = new Segment[WINDOW_SIZE];
        rcvBuffer = new Segment[WINDOW_SIZE];
    }

    // aangeroepen door app voor data van trans
    public byte[] readIn() {
        byte[] temp = null;
        System.out.println("HANG");
        synchronized (INLOCK) {
            if (!isInDirty()) {
                try {
                       System.out.println("WAITING FOR INLOCK");
                    INLOCK.wait();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            System.out.println("INLOCK ACQUIRED");
            temp = inBuffer;
            inBuffer = null;
            INLOCK.notify();
        }
        return temp;
    }

    /**
     * Van app naar trans
     * @param bytes
     * @require bytes.length <= 96
     */
    // door app aangeroepen om data aan trans te geven
    public boolean writeOut(byte[] bytes) {
        synchronized (OUTLOCK) {
            if (isOutDirty()) {
                try {
                    OUTLOCK.wait();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            if (bytes.length <= 96) {
                outBuffer = bytes;
            }
            OUTLOCK.notify();
        }
        return true;
    }

    // door trans aangeroepen voor data van app
    public byte[] readOut() {
        byte[] temp = null;
        synchronized (OUTLOCK) {
            if (!isOutDirty()) {
                try {
                    OUTLOCK.wait();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            temp = outBuffer;
            outBuffer = null;
            OUTLOCK.notify();
        }
        return temp;
    }

    // aangeroepen door trans voor data naar app
    public boolean writeIn(byte[] bytes) {
        synchronized (INLOCK) {
            if (isInDirty()) {
                try {
                    INLOCK.wait();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            if (bytes.length <= 96) {
                inBuffer = bytes;
                ackNr++;
                if (ackNr == SEQ_NR_LIMIT) {
                    ackNr = 0;
                }
            }
            INLOCK.notify();
        }
        return true;
    }

    /**
     * @return the address
     */
    public int getDestinationAddress() {
        return dstAddress;
    }

    /**
     * @return the port
     */
    public int getSourcePort() {
        return srcPort;
    }

    public int getDesintationPort() {
        return dstPort;
    }

    public int getCurrentSeq() {
    	synchronized (OUTLOCK) {
    		return seqNr;
		}
    }

    public int getCurrentAck() {
        return ackNr;
    }

    /**
     * @return the outDirty
     */
    public boolean isOutDirty() {
        return outBuffer != null;
    }

    /**
     * @return the inDirty
     */
    public boolean isInDirty() {
        return inBuffer != null;
    }

    /**
     * @return the OUTLOCK
     */
    public Object getOUTLOCK() {
        return OUTLOCK;
    }

    /**
     * @return the INLOCK
     */
    public Object getINLOCK() {
        return INLOCK;
    }

    public void incrLastAcked() {
        lastAcked++;
        if (lastAcked == SEQ_NR_LIMIT) {
            lastAcked = 0;
        }
    }
    
    public void incrSeq() {
        seqNr++;
        if (seqNr == SEQ_NR_LIMIT) {
            seqNr = 0;
        }
    }

    public int getLastAcked() {
        return lastAcked;
    }
    
    public void resetTimer(){
    	timeCount = System.currentTimeMillis();
    }
    
    public boolean timeout(){
    	return (timeCount+TPSocket.ACK_TIMEOUT)<System.currentTimeMillis();
    }
    
    //--------------------------
    public void addSegmentToSNDBuffer(Segment s) {
        sndBuffer[s.getSEQ()%(WINDOW_SIZE)] = s;
    }
    
    public boolean isValidACK(int seq){
    	boolean ret;
    	if(sndWindowBase<128){
    		ret = (seq>sndWindowBase)&&(seq<(sndWindowBase+TPSocket.WINDOW_SIZE));
    	}else{
    		ret = (seq<((sndWindowBase+TPSocket.WINDOW_SIZE)%TPSocket.SEQ_NR_LIMIT)) || (seq>sndWindowBase);
    	}
    	return ret;
    }
    
    public void processAck(int seqNr) {
//        for (int i = 0; i < sndBuffer.size(); i++) {
//            if (sndBuffer.get(i).getSEQ() == seq_nr) {
//                sndBuffer.set(i, null);
//                break;
//            }
//        }
//        Iterator<Segment> it = sndBuffer.listIterator();
//        while (it.hasNext()) {
//            if (it.next() == null) {
//                it.remove();
//                incrLastAcked();
//            } else {
//                break;
//            }
//        }
    	sndBuffer[seqNr%WINDOW_SIZE] = null;
    	int i;
    	for(i=sndWindowBase; i<(sndWindowBase+WINDOW_SIZE); i++){
    		if(sndBuffer[i%WINDOW_SIZE]!=null)
    			break;
    	}
    	
    	sndWindowBase = i%SEQ_NR_LIMIT;
    	Log.writeLog("TPS", "Processing ACK " + seqNr + "settubg sndwBase to " + sndWindowBase, true);
    }
    
    public int getSndWindowBase(){
    	return sndWindowBase;
    }
    
    public Segment getSegmentFromSNDBuffer() {
        return sndBuffer[sndWindowBase%WINDOW_SIZE];
    }

//    public Segment getSegmentFromRCVBuffer() {
//        if (rcvBuffer.get(0) != null) {
//            Segment temp = rcvBuffer.get(0);
//            rcvBuffer.remove(0);
//            rcvBuffer.add(null);
//            rcvWindowPtr = (rcvWindowPtr + 1) % 256;
//            return temp;
//        } else {
//            return null;
//        }
//    }
//
    public void fillrcvBuffer(Segment seg) {
        rcvBuffer[seg.getSEQ()%WINDOW_SIZE] = seg;
        int i;
    	for(i=rcvWindowBase; i<(rcvWindowBase+WINDOW_SIZE); i++){
    		if(rcvBuffer[i%WINDOW_SIZE]!=null){
    			writeIn(rcvBuffer[i%WINDOW_SIZE].getData());
    		}else{
    			break;
    		}    			
    	}
    	rcvWindowBase = i%SEQ_NR_LIMIT;
    	
    	
    }

	public boolean isValidSEQ(int ack) {
		boolean ret;
    	if(rcvWindowBase<128){
    		ret = (ack>=rcvWindowBase)&&(ack<(rcvWindowBase+TPSocket.WINDOW_SIZE));
    	}else{
    		ret = (ack<((rcvWindowBase+TPSocket.WINDOW_SIZE)%TPSocket.SEQ_NR_LIMIT)) || (ack>=rcvWindowBase);
    	}
    	return ret;
	}
}

