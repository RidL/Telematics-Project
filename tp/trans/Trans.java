package tp.trans;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        System.out.println("nou, twetje is gestawt");
        int temp = 0;
        byte[] data = null;
        while (true) {
            // System.out.println(sockList.size());

            for (int i = 0; i < sockList.size(); i++) {
                data = sockList.get(i).readOut();

                // System.out.println(socksList.get(i).isOutDirty());//app heeft data die naar route moet
                if (data != null) {
                    temp = 0;
                 //   System.out.println("Upcoming segment...");
                    Segment seg = createSegment(data, sockList.get(i), false);
                    //System.out.println("Segment aangemaakt");
                    int o = 0;
                    for (int p = 0; p < seg.getBytes().length; p++) {
                        o++;
                    //  System.out.println(Frame.toBinaryString(seg.getBytes()[p]));
                    }

                    // System.out.println("Segment ended: length: " + o + " bytes");
                    boolean suc = false;
                    do {
                        try {

                            Thread.sleep(10);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Trans.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        suc = sockList.get(i).writeIn(seg.getData());
                    //  System.out.println("returning data to fileReceiver");
                    } while (!suc);
                  //  System.out.println("segment weer teruggerost");
                //route.rcvSegment(seg);
                } else {
                    
                    temp++;
                    try {
                        Thread.sleep(10);
                        System.out.println("wakker geworre");
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Trans.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    if (temp % 100 == 0) {
                        System.out.println("null gelezen");
                    }
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

    public void closeSocket(TPSocket sock) {
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
