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
    	byte[]temp = null;
		synchronized(INLOCK){
			while(!isInDirty()){
	    		try{
	    			INLOCK.wait();
	    		}catch(InterruptedException e){
	    			System.err.println("failed to wait on INLOCK");
	    		}
	    	}
			temp = null;
			if (inBuffer!=null) {
				temp = inBuffer;
				inBuffer = null;
   	     	}
			INLOCK.notifyAll();
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
    	//TODO: blokkeer op outDirty vanaf applicatie
    	boolean suc = false;
    	
    	synchronized(OUTLOCK){
    		while(isOutDirty()){
        		try{
        			synchronized(OUTLOCK){
        				OUTLOCK.wait();
        			}
        		}catch(InterruptedException e){
        			System.err.println("failed to wait on OUTLOCK");
        		}
        	}
    		System.out.println("lock done, writing");
            if (outBuffer==null) {
                if (bytes.length <= 96 && outBuffer == null) {
                    outBuffer = bytes;
                    suc = true;
                }
            }
            OUTLOCK.notifyAll();
    	}
        return suc;
    }

    // door trans aangeroepen voor data van app
    public byte[] readOut() {
    	//TODO: spin op read?
    	byte[] temp = null;
    	synchronized(OUTLOCK){
    		try{
    			while(!isOutDirty())
    				OUTLOCK.wait();
    		}catch(InterruptedException e){
    			
    		}
            temp = outBuffer;
            outBuffer = null;
    		OUTLOCK.notifyAll();
    	}
        return temp;
    }

    // aangeroepen door trans voor data naar app
    public boolean writeIn(byte[] bytes) {
    	synchronized (INLOCK) {
            while (isInDirty()) {
                try {
                    INLOCK.wait();
                } catch (InterruptedException ex) {
                    Logger.getLogger(TPSocket.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (bytes.length <= 96) {
                inBuffer = bytes;
            }
            INLOCK.notifyAll();
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
