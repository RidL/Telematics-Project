package tp.link;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import tp.trans.Route;
import tp.trans.Segment;
import tp.util.Log;

public class Tunnel extends Thread implements Link {

    private Socket sock;
    private BufferedReader read;
    private BufferedWriter write;
    private boolean isConnected;
    private InetAddress addr;
    private int port;
    private Route route;

    public Tunnel(String addr, int port, Route route) {
        try {
            Log.writeLog(" TUN", "trying new tunnel @ " + addr + ":" + port, true);
            this.addr = InetAddress.getByName(addr);
            sock = new Socket(addr, port);
            isConnected = true;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Could not connect, host might not be looking for tunnel.");
        }
        this.port = port;
        this.route = route;
    }

    @Override
    public void run() {
        if (sock == null) {
            try {
                System.out.println("waiting on socket request;");
                ServerSocket serv = new ServerSocket(port);
                sock = serv.accept();
                isConnected = true;
                System.out.println("socket request accepted;");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            read = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            write = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
        } catch (IOException e) {
            System.err.println("Could not construct socket I/O @ " + addr.getHostAddress() + ":" + port + "");
            e.printStackTrace();
        }
        while (true) {
            byte[] data = new byte[103];
            int in;
            int length;

            try {
                in = read.read();
                for (int i = 0; (in != -1) && (i < 5); in = read.read(), i++) {
                    data[i] = (byte) in;
                    Log.writeLog("TUN", "read " + data[i], false);
                }
                data[5] = (byte)in;
                data[6] = (byte) read.read();
                System.out.println("TUN length" + in);
                for (int i = 0; i < in; i++) {
                    data[7 + i] = (byte) read.read();
                }

            } catch (IOException e) {
                System.err.println("Error whilst reading from the stream @ " + addr.getHostAddress() + ":" + port + "");
                e.printStackTrace();
                break;
            }
            Log.writeLog("TUN", "end read, going on!", true);
            Segment seg = new Segment(data);
            System.out.println("null segment? " + (seg == null) + " ");
            route.rcvSegment(seg);
        }
    }

    @Override
    public void pushSegment(Segment s) {
        System.out.println("TUN PUSHED");
        byte[] bytes = s.getBytes();
        try {
            for (int i = 0; i < bytes.length; i++) {
            	//TODO test dis shite
            	System.out.println(Frame.toBinaryString(bytes[i]));
                write.write(bytes[i]);
            }
            write.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean readyToPushSegment() {
        return isConnected;
    }
}
