/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tp.trans;

/**
 *
 * @author STUDENT\s1012886
 */
public class TPSocket {

    private final static int SEQ_NR_LIMIT = 256;

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
    public synchronized byte[] readIn() {
    	byte[]temp = null;
    	while(!isInDirty()){
    		try{
    			wait();
    		}catch(InterruptedException e){
    			
    		}
    	}
		System.out.println("WAITING ON INLOCK");
		synchronized(INLOCK){
			//System.out.println("have lock!");
			temp = null;
			if (inBuffer!=null) {
				temp = inBuffer;
				inBuffer = null;
   	     	}
		}
		System.out.println("LOCK DONE");
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
            if (outBuffer==null) {
                if (bytes.length <= 96 && outBuffer == null) {
                    outBuffer = bytes;
                    suc = true;
                    seq_nr++;
                    if(seq_nr == SEQ_NR_LIMIT) {
                        seq_nr = 0;
                    }
                }
            }
    	}

        return suc;
    }

    // door trans aangeroepen voor data van app
    public byte[] readOut() {
    	//TODO: spin op read?
    	byte[] temp = null;
    	synchronized(OUTLOCK){
    		if (outBuffer!=null) {
                temp = outBuffer;
                outBuffer = null;
            }
    	}
        return temp;
    }

    // aangeroepen door trans voor data naar app
    public synchronized boolean writeIn(byte[] bytes) {
        boolean suc;
        System.out.println("in ze function");
    	synchronized(INLOCK){
    		System.out.println("lock acquired ");
        	suc = false;
            if (inBuffer==null) {
                if (bytes.length <= 96) {
                	System.out.println("data put in sock");
                    inBuffer = bytes;
                    suc = true;
                    ack_nr++;
                    if(ack_nr == SEQ_NR_LIMIT) {
                        ack_nr = 0;
                    }
                }
            } else {
                suc = false;
            }
        }
    	notifyAll();
    	return suc;
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
