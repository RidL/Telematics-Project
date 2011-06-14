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
	
	public Tunnel(String addr, int port, Route route){
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
	public void run(){
		if(sock == null){
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
		while(true){
			byte[] data = new byte[104];
			int len = 7;
			int in;
			
			try {
				in = read.read();
				System.out.println("int read: " + in);
				for(int i=0; (in!=-1)&&len>0; in=(byte)read.read(), i++, len--){
					data[i] = (byte)in;
					if(i==5){
						len += data[i];
					}
					if(len==1)
						break;
					Log.writeLog("TUN", len + " " + Integer.toString((byte)in), true);
				}
			} catch (IOException e) {
				System.err.println("Error whilst reading from the stream @ " + addr.getHostAddress() + ":" + port + "");
				e.printStackTrace();
			}
			Log.writeLog("TUN", "-1 read, going on!", true);
			route.rcvSegment(new Segment(data));
		}
	}
	
	@Override
	public void pushSegment(Segment s) {
		byte[] bytes = s.getBytes();
		try {
			for(int i=0; i<bytes.length; i++){
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
