package tp.trans;

import tp.link.Frame;
import tp.util.SHA1Hash;

import java.security.NoSuchAlgorithmException;

public class Segment {

    private byte[] bytes;
    public static final int HEADER_LENGTH = 7;
    private byte[] data;

    public Segment(byte[] data, int scrAddr, int scrPort, int destAddr, int destPort, boolean isAck, int ackSeq) {
        byte length = (byte) data.length;
        bytes = new byte[data.length + HEADER_LENGTH];

        byte scrAddrPort = (byte) ((scrAddr << 4) | scrPort);
        byte destAddrPort = (byte) ((destAddr << 4) | destPort);
        byte options;

        if (isAck) {
            options = 8;
        } else {
            options = 0;
        }

        byte ack_Seq = (byte) ackSeq;

        bytes[0] = scrAddrPort;
        bytes[1] = destAddrPort;
        bytes[2] = 0;
        bytes[3] = 0;
        bytes[4] = options;
        bytes[5] = length;
        bytes[6] = ack_Seq;
        
        for (int d = 0; d < data.length; d++) {
            bytes[d+7] = data[d];
        }

        System.out.println("Segment length: "+length);
        
        this.data = data;
        calculateHash();
    }

    public Segment(byte[] bytes) {
    	System.out.println(bytes[5]+" "+Frame.toBinaryString(bytes[5]));
        byte[] tmp = new byte[(bytes[5]+7)];
        byte[] dat = new byte[(bytes[5])];
        
        for(int i=0; i<tmp.length; i++){
            tmp[i] = bytes[i];
        }
        for(int i=0; i<bytes[5]; i++){
            dat[i] = bytes[i+7];
        }
        this.bytes = tmp;
        this.data = dat;
    }
    
    public Segment(byte[] bytes, boolean doh){
    	this.bytes = bytes;
    	this.data = new byte[bytes[5]+7];
    	for(int i=0; i<data.length; i++){
    		this.data[i+7] = bytes[i];
    	}
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
    	int ret;
    	if (bytes[6] < 0) {
    		ret = bytes[6] + 256;
    	}
    	else {
    		ret = bytes[6];
        }
    	return ret;
    }
    
    public String toString(){
    	String retString = "SRC: ";
    	retString += this.getSourceAddress() + ":" + this.getSourcePort();
    	retString += "\nDST: ";
    	retString += this.getDestinationAddress() + ":" + this.getDestinationPort();
    	retString += "\nHSH: ";
    	retString += new String(this.getHash());
    	retString += "\nACK: ";
    	retString += new String(Boolean.toString(this.isACK()));
    	retString += "\nSEQ: ";
    	retString += new String(Integer.toString(this.getSEQ()));
    	return retString;

    }
}
