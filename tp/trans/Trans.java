package tp.trans;

import java.util.ArrayList;
import java.util.List;

public class Trans extends Thread {

    private static final int WINDOW_SIZE = 128;

    private static Trans ref;
    private static Route route;
    private int address;
    private List<TPSocket> sockList;
    private ArrayList<Segment> rcvBuff;

    private Trans(int address) {
        route = new Route(this);
        this.address = address;
        sockList = new ArrayList<TPSocket>();
        route.start();
    }

    public static Trans getTrans() {
        if (ref == null) {
            ref = new Trans(0);
            ref.start();
        }
        return ref;
    }

    @Override
    public void run() {
    	byte[] data;
    	TPSocket sock;
    	while(true){
    		for (int i = 0; i < sockList.size(); i++) {
    			sock = sockList.get(i);
    		    if(sock.isOutDirty()){
                /*
                 * REPLACE WITH WHEN ACK IS IMPLEMENTED
                 * if(sock.isOutDirty() && sock.getCurrentSeq() - sock.getLastAcked() < WINDOW_SIZE){
                 */
    		    	//MAYBE SYNC?
		    		data = sock.readOut();
		    		route.pushSegment(createSegment(data, sock, false));
    		    }
    	    }
    	}
        //TODO: handle incoming segs from rcvBuff
    }

    public int getAddress() {
        return address;
    }

    public TPSocket createSocket(int dstAddress, int srcPort, int dstPort) {
        //TODO:IS PORT TAKEN?
        TPSocket sock = new TPSocket(dstAddress, srcPort, dstPort);
        sockList.add(sock);
        return sock;
    }

    public void closeSocket(TPSocket sock) {
        sockList.remove(sock);
    }

    /**
     * Voor ontvangen data shit van Route
     * @param seg
     */
    public void rcvSeg(Segment seg) {
        // rcvBuff.add(seg);
    	TPSocket sock;
        for (int i = 0; i < sockList.size(); i++) {
        	sock = sockList.get(i);
            if (sock.getSourcePort() == seg.getDestinationPort()) {
                //if (seg.isValidSegment()) {
                    System.out.println("write succeeded " + (sock.writeIn(seg.getData())));

                    /*
                     * ENABLE THIS FOR ACK
                     * if(seg.isACK() && sock.getLastAcked() == seg.getSEQ()-1) {
                     *      sock.incrLastAcked();
                     * }
                     */
                //}
            //TODO: else: wait for retransmit
            }
        }
    }

    private Segment createSegment(byte[] data, TPSocket sock, boolean isAck) {
        int srcAddr = this.getAddress();
        int scrPort = sock.getSourcePort();
        int destAddr = sock.getDestinationAddress();
        int destPort = sock.getDesintationPort();
        int ackseq;
        if (isAck) {
            ackseq = (byte) sock.getCurrentAck();
        } else {
            ackseq = (byte) sock.getCurrentSeq();
        }
        return new Segment(data, srcAddr, scrPort, destAddr, destPort, isAck, ackseq);
    }
}
