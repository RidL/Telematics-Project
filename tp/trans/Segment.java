package tp.trans;

import tp.link.Frame;
import java.security.NoSuchAlgorithmException;

public class Segment {

    private byte[] bytes;
    public static final int HEADER_LENGTH = 7;
    private byte[] data;

    /**
     * Header is already filled by the transport layer with the hash-field zeroed.
     * @param data
     * @param head
     */
    public Segment(byte[] data, byte[] head) {
        int i = 0;
        bytes = new byte[data.length + head.length];
        for (int h = 0; h < head.length; h++, i++) {
            bytes[i] = head[h];
        }
        for (int d = 0; d < data.length; d++, i++) {
            bytes[i] = data[d];
        }

        this.data = data;
        calculateHash();
    }

    public Segment(byte[] data, int scrAddr, int scrPort, int destAddr, int destPort, boolean isAck, int ackSeq) {
        byte length = (byte) data.length;
        bytes = new byte[data.length + HEADER_LENGTH];
        byte[] header = new byte[HEADER_LENGTH];

        byte scrAddrPort = (byte) ((scrAddr << 4) | scrPort);
        byte destAddrPort = (byte) ((destAddr << 4) | destPort);
        byte options;

        if (isAck) {
            options = 8;
        } else {
            options = 0;
        }

        byte ack_Seq = (byte) ackSeq;

        header[0] = scrAddrPort;
        header[1] = destAddrPort;
        header[2] = 0;
        header[3] = 0;
        header[4] = options;
        header[5] = length;
        header[6] = ack_Seq;

        int i = 0;
        for (int h = 0; h < header.length; h++, i++) {
            bytes[i] = header[h];
        }
        for (int d = 0; d < data.length; d++, i++) {
            bytes[i] = data[d];
        }

        this.data = data;

        calculateHash();
    }

    public Segment(byte[] bytes) {
        byte[] tmp = new byte[(bytes[5]+7)];
        for(int i=0; i<tmp.length; i++){
            tmp[i] = bytes[i];
        }
        this.data = tmp;
    }

    public int getSourcePort() {
        return (int) (bytes[0] & 0x0f);
    }

    public int getSourceAddress() {
        return (int) ((bytes[0] >> 4) & 0x0f);
    }

    public int getDestinationAddress() {
        return (int) ((bytes[1] >> 4) & 0x0f);
    }

    public int getDestinationPort() {
        return (int) (bytes[1] & 0x0f);
    }

    public byte[] getBytes() {
        return bytes;
    }

    public byte[] getData() {
        return data;
    }

    public boolean isValidSegment() {
        byte[] hashBytes = new byte[]{bytes[2], bytes[3], (byte) (bytes[4] & 0xf0)};
        bytes[2] = 0;
        bytes[3] = 0;
        bytes[4] = (byte) (bytes[4] & 0x0f);
        byte[] calculatedHash = calculateHash();

        boolean equal = true;

        for (int i = 0; i < calculatedHash.length && equal; i++) {
            if (calculatedHash[i] != hashBytes[i]) {
                equal = false;
            }
        }

        return equal;
    }

    public byte[] getHash() {
        return new byte[]{bytes[2], bytes[3], (byte) (bytes[4] & 0xf0)};
    }

    private byte[] calculateHash() {
        byte[] hash = null;

        bytes[2] = 0;
        bytes[3] = 0;
        bytes[4] = (byte) (bytes[4] & 0x0f);

        try {
            hash = SHA1Hash.SHA1(bytes);
        } catch (NoSuchAlgorithmException nsae) {
        }

        bytes[2] = hash[0];
        bytes[3] = hash[1];
        bytes[4] = (byte) (bytes[4] | (hash[2] & 0xf0));

        return new byte[]{hash[0], hash[1], (byte) (hash[2] & 0xf0)};
    }

    public boolean isACK() {
        byte ack = (byte) (((bytes[4] & 0x0f) >> 3) & 0x0f);    // redundant shit
        return (ack == 1);
    }

    public int getLength() {
        return bytes[5];
    }

    public int getSEQ() {
        return bytes[6];
    }
    
    public String toString(){
    	String retString = "SRC: ";
    	retString += this.getSourceAddress() + ":" + this.getSourcePort();
    	retString += "\nDST: ";
    	retString += this.getDestinationAddress() + ":" + this.getDestinationPort();
    	retString += "\nHSH: ";
    	retString += new String(this.getHash());
    	return retString;
    }
    
    public static void main(String[] args) {
        byte[] header = new byte[]{113, 67, 34, 88, 12, 12, 98};
        byte[] shitLoadAanData = new byte[]{9, 5, 34, 3, 1, 4, 6, 8, 5, 3, 6, 89};
        Segment seg = new Segment(shitLoadAanData, header);
        System.out.println("Suws adwess: " + seg.getSourceAddress());
        System.out.println("Suws puwt: " + seg.getSourcePort());
        System.out.println("Length: " + seg.getLength());
        System.out.println("SEQ" + seg.getSEQ());
        for (int i = 0; i < seg.getData().length; i++) {
            System.out.println(seg.getData()[i]);
            System.out.println("Martijn is een pussy!");
        }
        for (int j = 0; j < seg.getHash().length; j++) {
            System.out.println("HASH: " + seg.getHash()[j]);
        }

        System.out.println(seg.isValidSegment());
        System.out.println("Dest adwess: " + seg.getDestinationAddress());
        System.out.println("Dest puwt: " + seg.getDestinationPort());

        Segment seg2 = new Segment(shitLoadAanData, 5, 2, 4, 8, true, 111);
        for (int p = 0; p < seg2.getBytes().length; p++) {
            System.out.println(Frame.toBinaryString(seg2.getBytes()[p]));
        }
        System.out.println(seg2.isValidSegment());
    }
}
