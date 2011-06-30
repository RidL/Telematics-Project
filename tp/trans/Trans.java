package tp.trans;

import java.util.ArrayList;
import java.util.List;

public class Trans extends Thread {
    private static Trans ref;
    private static Route route;
    private static int address;
    private List<TPSocket> sockList;
//    private Timer timer;
//    private TimeoutHandler tHandler;

    private Trans(int address) {
        route = new Route(this);
        Trans.address = address;
        sockList = new ArrayList<TPSocket>();
        new Thread(route).start();
    }

    public static Trans getTrans(int addr) {
        if (ref == null) {
            ref = new Trans(addr);
            ref.start();
            System.out.println("Setting new address as: " + addr);
        } else {
            System.out.println("Warning: Trans already exists, did not create "
                    + "a new Trans with address: " + addr + " current address still is " + address + "");
        }
        return ref;
    }

    public static Trans getTrans() {
        if (ref == null) {
            ref = new Trans(0);
            ref.start();
            System.out.println("Warning: trans address wasn't set, picking 0");
        }
        return ref;
    }

    public static boolean isInited() {
        return ref != null;
    }

    @Override
    public void run() {
        byte[] data;
        TPSocket sock = null;
        while (true) {
            for (int i = 0; i < sockList.size(); i++) {
                sock = sockList.get(i);
                if(sock.timeout()){
                	System.out.println("timeout");
                	Segment s = sock.getSegmentFromSNDBuffer();
                	if(s!=null){
                		route.pushSegment(s);
                		sock.resetTimer();
                	}
                }
                if (sock.isOutDirty()) {
                	sock.resetTimer();
                	if(sock.isValidACK(sock.getCurrentSeq()+1)){
                		 System.out.println("new data read");
                		 data = sock.readOut();
	                     Segment s = createSegment(data, sock, false);
	                     sock.addSegmentToSNDBuffer(s);
	                     route.pushSegment(s);
	                     sock.incrSeq();
                	}
                }
            }
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public int getAddress() {
        return address;
    }

    public TPSocket createSocket(int dstAddress, int srcPort, int dstPort) throws SocketTakenException, UnkownTPHostException {
        for (TPSocket s : sockList) {
            if ((s.getSourcePort() == srcPort) && (s.getDestinationAddress() == Trans.getTrans().getAddress())) {
                throw new SocketTakenException("Port: " + srcPort + " is taken, connection to " + dstAddress);
            }
        }
        if (!route.hasDst(dstAddress)) {
            throw new UnkownTPHostException("Unknown route for " + dstAddress);
        }
        TPSocket sock = new TPSocket(dstAddress, srcPort, dstPort);
        sockList.add(sock);
        return sock;
    }

    public void closeSocket(TPSocket sock) {
        sockList.remove(sock);
    }

    public Route getRoute() {
        return route;
    }

    /**
     * Voor ontvangen data shit van Route
     * @param seg
     */
    public void rcvSeg(Segment seg) {
        TPSocket sock;
        for (int i = 0; i < sockList.size(); i++) {
            sock = sockList.get(i);
            if ((sock.getSourcePort() == seg.getDestinationPort())&&(seg.getDestinationAddress()==address)) {
                //if (seg.isValidSegment()) {
                if (seg.isACK()) {
                	int seq = seg.getSEQ();
                	System.out.println("ACK RCV: " + seq + "");
                	if(sock.isValidACK(seq)){// not before window base!
                		sock.processAck(seg.getSEQ());
                	}else{
                		System.out.println("ERROR: rcv'd ack out of window");
                	}
                    
                } else {
                    if(sock.isValidSEQ(seg.getSEQ())){
                    	sock.fillrcvBuffer(seg);
                    	//send ack
                    	route.pushSegment(new Segment(new byte[0], getAddress(), 
                    			sock.getSourcePort(), sock.getDestinationAddress(), 
                    			sock.getDesintationPort(), true, seg.getSEQ()));
                    }else{
                    	System.out.println("ERROR: rcv'd seq out of window");
                    }
                    
                }
                //}
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
