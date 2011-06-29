package tp.link;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import tp.trans.Route;
import tp.trans.Segment;
import tp.trans.Trans;

public class Tunnel extends Thread implements Link {
	public static final long CONNECTION_TIMEOUT = 120000;//2min

    private boolean isConnected;
    private boolean listening;
    private InetAddress addr;
    private int port;
    private Route route = Trans.getTrans().getRoute();
    private DataOutputStream os;
    private DataInputStream is;

    public Tunnel(String addr, int port, boolean listen) throws TunnelTimeoutException {
        Socket sock = null;
        this.port = port;
        this.listening = listen;
        try {
    		InetAddress ia = InetAddress.getByName(addr);
    		this.addr = ia;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
		if(listen){
			ServerSocket serv;
			try {
				serv = new ServerSocket(port);
				String servAddr;
				do{
	    			sock = serv.accept();
	    			serv.close();
	    			servAddr = sock.getRemoteSocketAddress().toString().split(":")[0].substring(1);
	    			System.out.println(addr.toString());
	    			System.out.println(servAddr);
	    		}while(!servAddr.equals(addr.toString()));
				isConnected = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}else{
    		long startTime = System.currentTimeMillis(); 
    		while((System.currentTimeMillis()<(startTime+CONNECTION_TIMEOUT) && sock==null)){
    			try {
					sock = new Socket(addr,port);
					isConnected = true;
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					try{
						Thread.sleep(100);
					}catch(InterruptedException ie){
						e.printStackTrace();
					}
				}
    		}
    		if(sock == null)
    			throw new TunnelTimeoutException("Timeout while trying to connect to: " + addr + " " + port);
    	}
    	try {
			os = new DataOutputStream(sock.getOutputStream());
			is = new DataInputStream(sock.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    @Override
    public void run() {
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

            } catch (IOException e) {
                System.err.println("Error whilst reading from the stream @ " + addr.getHostAddress() + ":" + port + "");
                e.printStackTrace();
                break;
            }
            route.rcvSegment(new Segment(data));
        }
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