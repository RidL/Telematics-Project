package tp.link;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import tp.trans.Segment;
import tp.trans.Trans;

public class Tunnel extends Thread implements Link {
    private boolean isConnected;
    private boolean listening;
    private String addr;
    private int port;
    private DataOutputStream os;
    private DataInputStream is;

    public Tunnel(String addr, int port, boolean listen){
    	setName("TUNNEL @ " + addr);
        this.port = port;
        this.listening = listen;
        this.addr = addr;
    }

    @Override
    public void run() {
    	connect();
        while (true) {
            byte[] data = new byte[103];
            byte in;
            try {
                in = is.readByte();
                for (int i = 0; (i < 5); in = is.readByte(), i++) {
                    data[i] = (byte) in;
                }
                data[5] = in;
                data[6] = is.readByte();
                for (int i = 0; i < in; i++) {
                    data[7 + i] = is.readByte();
                }
                Trans.getTrans().getRoute().rcvSegment(new Segment(data));
            } catch (IOException e) {
                System.out.println("Connection closed @ " + addr);
                isConnected = false;
                Trans.getTrans().getRoute().changed();
                connect();
            }
        }
    }
    
    private void connect(){
    	Socket sock = null;
    	InetAddress ia = null;
        try {
    		ia = InetAddress.getByName(addr);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
		if(listening){
			ServerSocket serv;
			try {
				//will throw exeption if already listning on this port
				serv = new ServerSocket(port);
    			sock = serv.accept();
				serv.close();
				isConnected = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}else{
    		while(sock==null){
    			try {
					sock = new Socket(ia,port);
					isConnected = true;
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					try{
						System.out.println("Failed to connect to " + addr + " retrying");
						Thread.sleep(500);
					}catch(InterruptedException ie){
						e.printStackTrace();
					}
				}
    		}
    	}
    	try {
			os = new DataOutputStream(sock.getOutputStream());
			is = new DataInputStream(sock.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
    	Trans.getTrans().getRoute().changed();
    }
    
    public String getAddress(){
    	return this.addr.toString();
    }
    
    public String getPort(){
    	return Integer.toString(port);
    }
    
    public boolean isListening() {
		return listening;
	}
    
    public boolean isConnected(){
    	return isConnected;
    }
    
    @Override
    public void pushSegment(Segment s) {
    	try {
			os.write(s.getBytes());
		} catch (IOException e) {
			System.err.println("Error writing data @ TUN " + addr.toString());
			e.printStackTrace();
		}
    }

    @Override
    public boolean readyToPushSegment() {
        return isConnected;
    }

	
}