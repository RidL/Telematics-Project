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
        synchronized (INLOCK) {
            byte[] temp = null;
            if (!isInDirty()) {
                try {
                //    System.out.println("READ - INBUFFER - WAITING FOR INLOCK");
                    INLOCK.wait();
                //   System.out.println("READ - INBUFFER - INLOCK ACQUIRED");
                    temp = inBuffer;
                    inBuffer = null;
                //    System.out.println("READ - INBUFFER - BUFFER READ");
                    INLOCK.notify();
                } catch (InterruptedException e) {
                }
            } else {
              //  System.out.println("READ - INBUFFER - INLOCK ACQUIRED");
                temp = inBuffer;
                inBuffer = null;
              //  System.out.println("READ - INBUFFER - BUFFER READ");
                INLOCK.notify();
            }
            return temp;
        }
    }

    /**
     * Van app naar trans
     * @param bytes
     * @require bytes.length <= 96
     */
    // door app aangeroepen om data aan trans te geven
    public boolean writeOut(byte[] bytes) {
        synchronized (OUTLOCK) {
            boolean suc = false;
            if (isOutDirty()) {
                try {
                 //   System.out.println("WRITE - OUTBUFFER - WAITING FOR OUTLOCK");
                    OUTLOCK.wait();
                 //   System.out.println("WRITE - OUTBUFFER - OUTLOCK ACQUIRED");
                    if (bytes.length <= 96) {
                 //       System.out.println("WRITE - OUTBUFFER - OUTBUFFER WRITTEN");
                        outBuffer = bytes;
                        suc = true;
                    }
                    OUTLOCK.notify();

                } catch (InterruptedException e) {
                }
            } else {
               // System.out.println("WRITE - OUTBUFFER - OUTLOCK ACQUIRED");
                if (bytes.length <= 96) {
                //    System.out.println("WRITE - OUTBUFFER - OUTBUFFER WRITTEN");
                    outBuffer = bytes;
                    suc = true;
                }
                OUTLOCK.notify();
            }
            return suc;
        }
    }

    // door trans aangeroepen voor data van app
    public byte[] readOut() {
        synchronized (OUTLOCK) {
            byte[] temp = null;
            if (!isOutDirty()) {
                try {
                //    System.out.println("READ - OUTBUFFER - WAITING FOR OUTLOCK");
                    OUTLOCK.wait();
                //    System.out.println("READ - OUTBUFFER - OUTLOCK ACQUIRED");
                    temp = outBuffer;
                    outBuffer = null;
                //    System.out.println("READ - OUTBUFFER - BUFFER READ");
                    OUTLOCK.notify();
                } catch (InterruptedException e) {
                }
            } else {
              //  System.out.println("READ - OUTBUFFER - OUTLOCK ACQUIRED");
                temp = outBuffer;
                outBuffer = null;
              //  System.out.println("READ - OUTBUFFER - BUFFER READ");
                OUTLOCK.notify();
            }
            return temp;
        }
    }

    // aangeroepen door trans voor data naar app
    public boolean writeIn(byte[] bytes) {
        synchronized (INLOCK) {
            boolean suc = false;
            if (isInDirty()) {
                try {
                //    System.out.println("WRITE - INBUFFER - WAITING FOR INLOCK");
                    INLOCK.wait();
                //    System.out.println("WRITE - INBUFFER - INLOCK ACQUIRED");
                    if (bytes.length <= 96) {
                        inBuffer = bytes;
                //        System.out.println("WRITE - INBUFFER - BUFFER WRITTEN");
                        suc = true;
                    }
                    INLOCK.notify();
                } catch (InterruptedException e) {
                }
            } else {
             //   System.out.println("WRITE - INBUFFER - INLOCK ACQUIRED");
                if (bytes.length <= 96) {
                    inBuffer = bytes;
             //       System.out.println("WRITE - INBUFFER - BUFFER WRITTEN");
                    suc = true;
                }
                INLOCK.notify();
            }
            return suc;
        }
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
}
