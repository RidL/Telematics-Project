/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tp.trans;

import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author STUDENT\s1012886
 */
public class TPSocket {

    private final static int SEQ_NR_LIMIT = 256;
    private final static int WINDOW_SIZE = 128;
    private int seq_nr;
    private int ack_nr;
    private int lastAcked;
    private int dstAddress;
    private int srcPort;
    private int dstPort;
    private byte[] inBuffer;
    private byte[] outBuffer;
    private final Object OUTLOCK = new Object();
    private final Object INLOCK = new Object();
    //--------------------
    private ArrayList<Segment> sndBuffer;
    private ArrayList<Segment> rcvBuffer;
    private int sndWindowPtr;
    private int rcvWindowPtr;

    public TPSocket(int dstAddress, int srcPort, int dstPort) {
        seq_nr = -1;
        ack_nr = 0;
        lastAcked = -1;
        this.dstAddress = dstAddress;
        this.srcPort = srcPort;
        this.dstPort = dstPort;

        //-----------------

        sndBuffer = new ArrayList<Segment>(WINDOW_SIZE);
        rcvBuffer = new ArrayList<Segment>(WINDOW_SIZE);

        for (int i = 0; i < WINDOW_SIZE; i++) {
            rcvBuffer.add(null);
        }

        sndWindowPtr = 0;
        rcvWindowPtr = 0;
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
                ack_nr++;
                if (ack_nr == SEQ_NR_LIMIT) {
                    ack_nr = 0;
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
    		return seq_nr;
		}
    }

    public int getCurrentAck() {
        return ack_nr;
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
        seq_nr++;
        if (seq_nr == SEQ_NR_LIMIT) {
            seq_nr = 0;
        }
    }

    public int getLastAcked() {
        return lastAcked;
    }

    //--------------------------
    public void addSegmentToSNDBuffer(Segment s) {
        sndBuffer.add(s);
    }

    public void updateBuffer(int seq_nr) {
        //sndBuffer.set((seq_nr-lastAcked)-1, null);

        for (int i = 0; i < sndBuffer.size(); i++) {
            if (sndBuffer.get(i).getSEQ() == seq_nr) {
                sndBuffer.set(i, null);
                break;
            }
        }

        Iterator<Segment> it = sndBuffer.listIterator();
        while (it.hasNext()) {
            if (it.next() == null) {
                it.remove();
                incrLastAcked();
            } else {
                break;
            }
        }
    }
    
    public boolean isSNDBufferFull() {
    	return sndBuffer.size() == 128;
    }

    public Segment getSegmentFromSNDBuffer() {
        if (sndBuffer.size() > 0) {
            return sndBuffer.get(0);
        } else {
            return null;
        }
    }

    public Segment getSegmentFromRCVBuffer() {
        if (rcvBuffer.get(0) != null) {
            Segment temp = rcvBuffer.get(0);
            rcvBuffer.remove(0);
            rcvBuffer.add(null);
            rcvWindowPtr = (rcvWindowPtr + 1) % 256;
            return temp;
        } else {
            return null;
        }
    }
    
    public String printRCVB() {
    	String s = "";
    	for (int i = 0; i < rcvBuffer.size(); i++) {
    		s += rcvBuffer.get(i) + "\n";
    	}
    	return s;
    }

    public void fillrcvBuffer(Segment seg, int seq) {
        //rcvBuffer.set((seq-rcvWindowPtr), seg);
        if (seq >= rcvWindowPtr) {
        	  rcvBuffer.set((seq % WINDOW_SIZE) - (rcvWindowPtr % WINDOW_SIZE), seg);
        } else if(seq < rcvWindowPtr - WINDOW_SIZE){
        	  rcvBuffer.set(256 - rcvWindowPtr + seq - 1, seg);
        }else {
            System.out.println("seg already rcvd");
        }
//        Iterator<Segment> it = rcvBuffer.listIterator();
//        while (it.hasNext()) {
//            if (it.next() == null) {
//                it.remove();
//                rcvWindowPtr++;
//                if (rcvWindowPtr == 128) {
//                    rcvWindowPtr = 0;
//                }
//            } else {
//                break;
//            }
//        }
    }
}
//TPSocket sock;
//for (int i = 0; i < sockList.size(); i++) {
//	sock = sockList.get(i);                System.out.println("Kom ik hierrr?");
//    if (sock.getSourcePort() == seg.getDestinationPort()) {
//        //if (seg.isValidSegment()) {
//
//            if(seg.isACK()) {
//                System.out.println("lastAck" + sock.getLastAcked());
//                System.out.println("currSeq" + seg.getSEQ());
//                if(sock.getLastAcked() == seg.getSEQ()-1 ||
//                    (sock.getLastAcked() + WINDOW_SIZE) == seg.getSEQ()-1 ) {
//                    System.out.println("TP-ACK RECEIVED: " +  seg.getSEQ());
//                    sock.incrLastAcked();
//                    //sendBuffer.get(i).remove(0);
//                }
//                else {
//                    // retransmit
//                    route.pushSegment(sendBuffer.get(i).get(sock.getCurrentSeq()-sock.getLastAcked()));
//                }
//            }
//            else {
//                System.out.println("TP-DATA RECEIVED: " + seg.getSEQ());
//                System.out.println("write succeeded " + (sock.writeIn(seg.getData())));
//                
//                // send ACK
//                Segment s = new Segment(new byte[0], getAddress(), sock.getSourcePort(), sock.getDestinationAddress(), sock.getDesintationPort(), true, sock.getCurrentAck());
//                route.pushSegment(s);
//            }
//        //}
//    //TODO: else: wait for retransmit
//    }
//}

