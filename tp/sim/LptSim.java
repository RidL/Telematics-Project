package tp.sim;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.io.InputStreamReader;
import java.net.Socket;

public class LptSim implements Runnable{
	private BufferedReader in;
	private BufferedWriter out;
	private Socket sock;
	
	private boolean cont;
	private Object WRITELOCK;
	private byte curr;
	
	public LptSim(int port, InetAddress address){
		curr = 0;
		WRITELOCK = new Object();
		cont = true;
		
		sock = null;
		ServerSocket serv;
		try {
			sock = new Socket(address, port);
		} catch (IOException e) {
			try {
				serv = new ServerSocket(port);
				sock = serv.accept();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		try {
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		new Thread(this).start();
	}
	
	public void writeLPT(int i){
		int c = i;
		try {
			out.write(c);
			out.flush();
		} catch (IOException e) {
			System.out.println("Sending of" + i + " failed TT");
		}
		
	}
	
	public byte readLPT(){
		synchronized(WRITELOCK){
			return curr;
		}
	}

	@Override
	public void run() {
		while(cont){
			try {
				int read = in.read();
				synchronized(WRITELOCK){
					curr = toByte(read);
				}
			} catch (IOException e) {
				cont = false;
				shutdown();
			}
		}
		
	}
	
	public static byte toByte(int send){
		byte b = (byte)(send&0x000000FF); // select last two nibbles
		b = (byte)((b<<3));
		return b;
	}
	
	private void shutdown(){
		try {
			sock.close();
		} catch (IOException e) {
			System.out.println("Could not close Socket");
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		for(int i=0; i<64; i++){
			System.out.print(i + " " );
			int read = toByte(i);
			System.out.print(read + " - ");
			read = ((read>>3)&0x1f)^0x10;
			System.out.println( read + " " + Integer.toBinaryString(read));
		}
	}
}
