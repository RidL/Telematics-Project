/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tp.trans;

import tp.link.Frame;

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
    public static final int LOCK = 0;

    public TPSocket(int dstAddress, int srcPort, int dstPort) {
        seq_nr = 0;
        ack_nr = 0;
        this.dstAddress = dstAddress;
        this.srcPort = srcPort;
        this.dstPort = dstPort;
        inDirty = false;
        outDirty = false;
    }

    // app
    public byte[] readIn() {
        byte[] temp = null;
        synchronized (this) {
            if (inDirty) {
                // System.out.println("new datas");
                temp = inBuffer;
                // System.out.println(Frame.toBinaryString(temp) + "gelezen van outbuf");
                inDirty = false;
            }
        }
        return temp;
    }

    /**
     *
     * @param bytes
     * @require bytes.length <= 96
     */ // app
    public boolean writeOut(byte[] bytes) {
        //System.out.println("ik probeer echt wel die shit op true te zette");
        boolean suc = false;
        synchronized (this) {
            if (!outDirty) {
                if (bytes.length <= 96) {
                    outBuffer = bytes;
                    outDirty = true;
                    suc = true;
                }
            } else {
                suc = false;
            }
        }
        //while (outDirty){
        //System.out.println("spinwait, wachten op !outdirty");
        // }
        // System.out.println("is !outdirty");
        return suc;
    }

    // vanuit trans naar app
    public byte[] readOut() {
        //  System.out.println("imma be outReading");
        byte[] temp = null;
        synchronized (this) {
            if (outDirty) {
                // System.out.println("new datas");
                temp = outBuffer;
                // System.out.println(Frame.toBinaryString(temp) + "gelezen van outbuf");
                outDirty = false;
            }
        }
        return temp;
    }

    // trans
    public boolean writeIn(byte[] bytes) {
        boolean suc = false;
        synchronized (this) {
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
