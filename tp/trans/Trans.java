package tp.trans;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import tp.util.Log;

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

        Log.getInstance("dd00");
        byte[] data = null;
        while (true) {
            for (int i = 0; i < sockList.size(); i++) {
                data = sockList.get(i).readOut();
               // if (data != null) {
                    Segment seg = createSegment(data, sockList.get(i), false);
                    boolean suc = false;
//                    do {
//                        try {
//                            Thread.sleep(5);
//                        } catch (InterruptedException ex) {
//                            Logger.getLogger(Trans.class.getName()).log(Level.SEVERE, null, ex);
//                        }
                        suc = sockList.get(i).writeIn(seg.getData());
//                    } while (!suc);
                //} else {
//                    try {
//                        Thread.sleep(5);
//                    } catch (InterruptedException ex) {
//                        Logger.getLogger(Trans.class.getName()).log(Level.SEVERE, null, ex);
//                    }
                //}
            }

//        byte[] data;
//        TPSocket sock;
//        while (true) {
//            for (int i = 0; i < sockList.size(); i++) {
//                sock = sockList.get(i);
//                if (sock.isOutDirty()) {
//                    //MAYBE SYNC?
//                    data = sock.readOut();
//                    route.pushSegment(createSegment(data, sock, false));
//                }
//            }
//        }

    //TODO: handle incoming segs from rcvBuff
    }}

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
        	System.out.println("searching for socket");
            if (sock.getSourcePort() == seg.getDestinationPort()) {
            	System.out.println("correct port is here");
                //if (seg.isValidSegment()) {
                	System.out.println("validseg!");
                    System.out.println("write succeeded " + (sock.writeIn(seg.getData())));
                    
                    System.out.println("done writing suc? " + sock.isInDirty());
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
