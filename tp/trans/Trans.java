package tp.trans;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Trans extends Thread {

    private static final int WINDOW_SIZE = 128;
    private static Trans ref;
    private static Route route;
    private static int address;
    private List<TPSocket> sockList;
    private Timer timer;
    private TimeoutHandler tHandler;
    private long time_out = 500;

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
            //	synchronized (sockList) {
            for (int i = 0; i < sockList.size(); i++) {
                sock = sockList.get(i);
                //TODO: checken of er wel data is ?
                if (sock.isOutDirty()) {
                    if ((sock.getCurrentSeq() - sock.getLastAcked() < WINDOW_SIZE)
                            || (sock.getCurrentSeq() + WINDOW_SIZE) - sock.getLastAcked() < WINDOW_SIZE) {
                        data = sock.readOut();
                        Segment s = createSegment(data, sock, false);

                        System.out.println("TP-SENDING DATA: " + s.getSEQ());
                        System.out.println(s.blaat());
                        sock.addSegmentToSNDBuffer(s);
                        route.pushSegment(s);
                        if (timer == null) {
                            timer = new Timer(true);
                            tHandler = new TimeoutHandler(sock);
                            timer.schedule(tHandler, time_out);
                        }
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
        //sendBuffer.add(new ArrayList<Segment>(WINDOW_SIZE));
        return sock;
    }

    public void closeSocket(TPSocket sock) {
        sockList.remove(sock);
        //sendBuffer.remove(index);
    }

    public Route getRoute() {
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
            sock = sockList.get(i);
            if ((sock.getSourcePort() == seg.getDestinationPort())&&(seg.getDestinationAddress()==address)) {
                //if (seg.isValidSegment()) {
                if (seg.isACK()) {
                    System.out.println("ACK RCVD!!!!!!");
                    //  System.out.println("lastAck: " + sock.getLastAcked());
                    System.out.println("Ack has seqnr: " + seg.getSEQ());
                    if (sock.getLastAcked() == seg.getSEQ() - 1
                            || sock.getLastAcked() == (seg.getSEQ() + WINDOW_SIZE) - 1) {
                        System.out.println("ACK is in order");
                        //sock.incrLastAcked();
                        //sendBuffer.get(i).remove(0);
                        if (timer != null) {
                            timer.cancel();
                            timer = null;
                            tHandler = null;
                            if (sock.getSegmentFromSNDBuffer() != null) {
                                timer = new Timer(true);
                                tHandler = new TimeoutHandler(sock);
                                timer.schedule(tHandler, time_out);
                            }
                        }
                    } else if (sock.getLastAcked() >= seg.getSEQ()) {
                        return;
                    }

                    else {
                        // retransmit
                        route.pushSegment(sock.getSegmentFromSNDBuffer());
                        if (timer != null) {
                            timer.cancel();
                            timer = new Timer(true);
                            tHandler = new TimeoutHandler(sock);
                            timer.schedule(tHandler, time_out);
                        } else {
                            timer = new Timer(true);
                            tHandler = new TimeoutHandler(sock);
                            timer.schedule(tHandler, time_out);
                        }
                    }
                    sock.updateBuffer(seg.getSEQ());

                } else {
                    System.out.println("TP-DATA RECEIVED, seqnr = " + seg.getSEQ());
                    // System.out.println("seg bytes length" + seg.getBytes().length);
                    // System.out.println("write succeeded " + (sock.writeIn(seg.getData())));

                    sock.fillrcvBuffer(seg, seg.getSEQ());
                    Segment s;
                    while ((s = sock.getSegmentFromRCVBuffer()) != null) {
                        System.out.println("A segment was written to the application " + (sock.writeIn(s.getData())));
                    }
                    // send ACK
                    System.out.println("Sending ack for data with seq: " + seg.getSEQ());
                    Segment ack = new Segment(new byte[0], getAddress(), sock.getSourcePort(), sock.getDestinationAddress(), sock.getDesintationPort(), true, /*sock.getCurrentAck() - 1*/ seg.getSEQ());
                    route.pushSegment(ack);
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

    class TimeoutHandler extends TimerTask {

        private TPSocket sock;

        public TimeoutHandler(TPSocket sock) {
            this.sock = sock;
        }

        @Override
        public void run() {
            
            if (sock.getSegmentFromSNDBuffer() != null) {
                System.out.println("Timer started for seq." + sock.getCurrentSeq());
                route.pushSegment(sock.getSegmentFromSNDBuffer());
                timer.cancel();
                timer = new Timer(true);
                timer.schedule(new TimeoutHandler(sock), time_out);
            }
        }
    }
}
