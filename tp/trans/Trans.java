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
    private ArrayList<ArrayList<Segment>> sendBuffer;

    private Trans(int address) {
        route = new Route(this);
        this.address = address;
        sockList = new ArrayList<TPSocket>();
        sendBuffer = new ArrayList<ArrayList<Segment>>();
        route.start();
    }

    public static Trans getTrans(int addr) {
        if (ref == null || ref.getAddress()!=addr) {
            ref = new Trans(addr);
            ref.start();
        }
        return ref;
    }
    
    public static Trans getTrans() {
		if (ref == null ) {
            ref = new Trans(0);
            ref.start();
        }
        return ref;
	}
    
    @Override
    public void run() {
    	byte[] data;
        TPSocket sock;
        while (true) {
            for (int i = 0; i < sockList.size(); i++) {
                 sock = sockList.get(i);
                 //TODO: checken of er wel data is ?
                 //TODO: robin en martijn kloten in deze methode
                 if((sock.getCurrentSeq() - sock.getLastAcked() < WINDOW_SIZE) ||
                         (sock.getCurrentSeq() + WINDOW_SIZE) - sock.getLastAcked() < WINDOW_SIZE) {
                    data = sock.readOut();
                    Segment s = createSegment(data, sock, false);

                    System.out.println("TP-SENDING DATA: " + s.getSEQ());
                    sendBuffer.get(i).add(s);
                    route.pushSegment(s);
                 }
            }
        }
    }

    public int getAddress() {
        return address;
    }

    public TPSocket createSocket(int dstAddress, int srcPort, int dstPort) {
        //TODO:IS PORT TAKEN?
        TPSocket sock = new TPSocket(dstAddress, srcPort, dstPort);
        sockList.add(sock);
        sendBuffer.add(new ArrayList<Segment>(WINDOW_SIZE));
        return sock;
    }

    public void closeSocket(TPSocket sock) {
        int index = sockList.indexOf(sock);
        sockList.remove(sock);
        sendBuffer.remove(index);
    }
    
    public Route getRoute(){
    	return route;
    }
    
    /**
     * Voor ontvangen data shit van Route
     * @param seg
     */
    public void rcvSeg(Segment seg) {
        // rcvBuff.add(seg);
    	TPSocket sock;
        for (int i = 0; i < sockList.size(); i++) {
        	sock = sockList.get(i);                System.out.println("Kom ik hierrr?");
            if (sock.getSourcePort() == seg.getDestinationPort()) {
                //if (seg.isValidSegment()) {

                    if(seg.isACK()) {
                        if(sock.getLastAcked() == seg.getSEQ()-1 ||
                            (sock.getLastAcked() + WINDOW_SIZE) == seg.getSEQ()-1 ) {
                            System.out.println("TP-ACK RECEIVED: " +  seg.getSEQ());
                            sock.incrLastAcked();
                            sendBuffer.remove(0);
                        }
                        else {
                            // retransmit
                            route.pushSegment(sendBuffer.get(i).get(sock.getCurrentSeq()-sock.getLastAcked()));
                        }
                    }
                    else {
                        System.out.println("TP-DATA RECEIVED: " + seg.getSEQ());
                        System.out.println("write succeeded " + (sock.writeIn(seg.getData())));
                        
                        // send ACK
                        Segment s = new Segment(new byte[0], getAddress(), sock.getSourcePort(), sock.getDestinationAddress(), sock.getDesintationPort(), true, sock.getCurrentAck());
                        route.pushSegment(s);
                    }
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
