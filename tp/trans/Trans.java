package tp.trans;

import java.util.ArrayList;
import java.util.List;

public class Trans extends Thread {

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
        while (true) {
            for (int i = 0; i < sockList.size(); i++) {
                byte[] data = sockList.get(i).readOut();
                // System.out.println(socksList.get(i).isOutDirty());//app heeft data die naar route moet
                if (data != null) {
                 //   System.out.println("Upcoming segment...");
                    Segment seg = createSegment(data, sockList.get(i), false);
                     System.out.println("Segment aangemaakt");
                    int o = 0;
                    for (int p = 0; p < seg.getBytes().length; p++) {
                        o++;
                      //  System.out.println(Frame.toBinaryString(seg.getBytes()[p]));
                    }

                   // System.out.println("Segment ended: length: " + o + " bytes");
                    boolean suc = false;
                    do {
                        suc = sockList.get(i).writeIn(seg.getData());
                    } while (!suc);
                //route.rcvSegment(seg);
                } else {
                    //  System.out.println("outdirty is false@" + i);
                }
            }

            //TODO: handle incoming segs from rcvBuff
        }
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
    
    public void closeSocket(TPSocket sock){
    	sockList.remove(sock);
    }

    /**
     * Voor ontvangen data shit van Route
     * @param seg
     */
    public void rcvSeg(Segment seg) {
        // rcvBuff.add(seg);
        for (int i = 0; i < sockList.size(); i++) {
            if (sockList.get(i).getSourcePort() == seg.getDestinationPort()) {
                if (seg.isValidSegment()) {
                    sockList.get(i).writeIn(seg.getData());
                }
            //else: wait for retransmit
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
