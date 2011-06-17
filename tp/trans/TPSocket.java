/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tp.trans;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author STUDENT\s1012886
 */
public class TPSocket {

    private final static int SEQ_NR_LIMIT = 256;

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

    public TPSocket(int dstAddress, int srcPort, int dstPort) {
        seq_nr = -1;
        ack_nr = 0;
        lastAcked = -1;
        this.dstAddress = dstAddress;
        this.srcPort = srcPort;
        this.dstPort = dstPort;
    }

    // aangeroepen door app voor data van trans
    public byte[] readIn() {
    	byte[] temp = null;
        synchronized (INLOCK) {
            if (!isInDirty()) {
                try {
                    System.out.println("WAITING FOR INLOCK");
                    INLOCK.wait();
                } catch (InterruptedException ex) {
                    Logger.getLogger(TPSocket.class.getName()).log(Level.SEVERE, null, ex);
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
                    Logger.getLogger(TPSocket.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (bytes.length <= 96) {
                outBuffer = bytes;
                seq_nr++;
                if(seq_nr == SEQ_NR_LIMIT) {
                    seq_nr = 0;
                }
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
                    Logger.getLogger(TPSocket.class.getName()).log(Level.SEVERE, null, ex);
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
                    Logger.getLogger(TPSocket.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (bytes.length <= 96) {
                inBuffer = bytes;
                ack_nr++;
                if(ack_nr == SEQ_NR_LIMIT) {
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
        return seq_nr;
    }

    public int getCurrentAck() {
        return ack_nr;
    }

    /**
     * @return the outDirty
     */
    public boolean isOutDirty() {
        return outBuffer!=null;
    }

    /**
     * @return the inDirty
     */
    public boolean isInDirty() {
        return inBuffer!=null;
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
    }

    public int getLastAcked() {
        return lastAcked;
    }
}
