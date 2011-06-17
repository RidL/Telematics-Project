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

    private int seq_nr;
    private int ack_nr;
    private int dstAddress;
    private int srcPort;
    private int dstPort;
    private byte[] inBuffer;
    private byte[] outBuffer;
    private final Object OUTLOCK = new Object();
    private final Object INLOCK = new Object();

    public TPSocket(int dstAddress, int srcPort, int dstPort) {
        seq_nr = 0;
        ack_nr = 0;
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
                    INLOCK.wait();
                } catch (InterruptedException ex) {
                    Logger.getLogger(TPSocket.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
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
}
