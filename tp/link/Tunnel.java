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

import tp.trans.Segment;
import tp.util.Log;

public class Tunnel extends Thread implements Link {
	Socket sock;
	BufferedReader read;
	BufferedWriter write;
	
	InetAddress addr;
	int port;
	
	
	public Tunnel(String addr, int port){
		try {
			Log.writeLog(" TUN", "trying new tunnel @ " + addr + ":" + port, true);
			this.addr = InetAddress.getByName(addr);
			sock = new Socket(addr, port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Could not connect, host might not be looking for tunnel.");
		}
		this.port = port;
	}
	
	@Override
	public void run(){
		if(sock == null){
			try {
				System.out.println("waiting on socket request;");
				ServerSocket serv = new ServerSocket(port);
				sock = serv.accept();
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
			Segment s;
			byte[] data = new byte[103];
			
			int in;
			try {
				in = read.read();
				for(int i=0; in>0; in = read.read(), i+=4){
					data[i] = (byte)in;
				}
			} catch (IOException e) {
				System.err.println("Error whilst reading from the stream @ " + addr.getHostAddress() + ":" + port + "");
				e.printStackTrace();
			}
			s = new Segment(data);
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
		return true;
	}

}
