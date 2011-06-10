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
    private boolean inDirty;
    private boolean outDirty;
    public Object OUTLOCK = 0, INLOCK = 1;

    public TPSocket(int dstAddress, int srcPort, int dstPort) {
        seq_nr = 0;
        ack_nr = 0;
        this.dstAddress = dstAddress;
        this.srcPort = srcPort;
        this.dstPort = dstPort;
        inDirty = false;
        outDirty = false;
    }

    // aangeroepen door app voor data van trans
    public byte[] readIn() {
        byte[] temp = null;
        synchronized (INLOCK) {
            if (inDirty) {
                temp = inBuffer;
                inBuffer = null;
                inDirty = false;
            }
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
        //System.out.println("ik probeer echt wel die shit op true te zette");

        boolean suc = false;
        synchronized (OUTLOCK) {
            if (!outDirty) {
                if (bytes.length <= 96) {
                    outBuffer = bytes;
                    outDirty = true;
                    suc = true;
                 //   System.out.println("Data verzonden");
                }
            }
        }

        //while (outDirty){
        //System.out.println("spinwait, wachten op !outdirty");
        // }
        // System.out.println("is !outdirty");
        return suc;
    }

    // door trans aangeroepen voor data van app
    public byte[] readOut() {

        byte[] temp = null;
        synchronized (OUTLOCK) {
            if (outDirty) {
                // System.out.println("new datas");
                temp = outBuffer;
                // System.out.println(Frame.toBinaryString(temp) + "gelezen van outbuf");
                outDirty = false;
            }
        }
     //   if(temp!=null)
     //   System.out.println("Data gegeven aan trans");
        return temp;
    }

    // aangeroepen door trans voor data naar app
    public boolean writeIn(byte[] bytes) {
        boolean suc = false;
        synchronized (INLOCK) {
            if (!inDirty) {
                if (bytes.length <= 96) {
                    inBuffer = bytes;
                    inDirty = true;
                    suc = true;
                }
            } else {
                suc = false;
            }
        }
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
        return outDirty;
    }

    /**
     * @return the inDirty
     */
    public boolean isInDirty() {
        return inDirty;
    }
}
