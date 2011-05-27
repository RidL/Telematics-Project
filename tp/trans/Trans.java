package tp.trans;

import java.util.ArrayList;
import java.util.List;
import tp.link.Frame;

public class Trans extends Thread {

    private static Trans ref;
    private static Route route;
    private int address;
    private List<TPSocket> socksList;
    private ArrayList<Segment> rcvBuff;

    private Trans(int address) {
        //   route = new Route(this);
        //   this.address = address;
        socksList = new ArrayList<TPSocket>();
    //   route.start();
    }

    @Override
    public void run() {
        System.out.println("p0p");
        while (true) {
            for (int i = 0; i < socksList.size(); i++) {
                byte[] data = socksList.get(i).readOut();
               // System.out.println(socksList.get(i).isOutDirty());//app heeft data die naar route moet
                if (data != null) {
                    Segment seg = createSegment(data, socksList.get(i), false);
                   // System.out.println("hhh");
                    int o = 0;
                    for (int p = 0; p < seg.getBytes().length; p++) {
                        o++;
                        System.out.println(Frame.toBinaryString(seg.getBytes()[p]));
                    }
                    System.out.println("seg ended" + o + " bytes");
                //route.rcvSegment(seg);
                }
                else {
                    System.out.println("outdirty is false@" + i);
                }
            }
        }
    }

    public int getAddress() {
        return address;
    }

    public static Trans getTrans() {
        if (ref == null) {
            ref = new Trans(0);
        }
        return ref;
    }

    public TPSocket createSocket(int dstAddress, int srcPort, int dstPort) {
        TPSocket sock = new TPSocket(dstAddress, srcPort, dstPort);
        socksList.add(sock);
        return sock;
    }

    /**
     * Voor ontvangen data shit van Route
     * @param seg
     */
    public void rcvSeg(Segment seg) {
        // rcvBuff.add(seg);
        for (int i = 0; i < socksList.size(); i++) {
            if (socksList.get(i).getSourcePort() == seg.getDestinationPort()) {
                if (seg.isValidSegment()) {
                    socksList.get(i).writeIn(seg.getData());
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
