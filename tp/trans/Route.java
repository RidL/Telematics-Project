package tp.trans;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import tp.link.Link;
import tp.link.Tunnel;

public class Route extends Thread {
	private ArrayList<Segment> routableSegs;
	private Map<Integer,Link> routingTable;
	private Trans trans;
	
	public Route(Trans trans){
		initRoutingTable();
		routableSegs = new ArrayList<Segment>();
		this.trans = trans;
	}
	
	@Override
	public void run(){
		while(true){
			//code
		}
	}
	
	public void rcvSegment(Segment s){
		//add to routing  | trans.rcvSegment(s)
	}
	
	public void initRoutingTable(){
		//ADDRESS LNK_TYPE(LPT|TUNNEL) TUN_ADD TUN_PRT
		BufferedReader read;
		routingTable = new HashMap<Integer, Link>();
		try {
			read = new BufferedReader(new FileReader("routing.conf"));
			String s = read.readLine();
			while(s != null){
				Scanner scan = new Scanner(s);
				int addr;
				Link l = null;
				addr = Integer.parseInt(scan.next());
				if(scan.next().equals("LPT")){
					//TODO: INIT RCV, INIT SENDER
				}else{
					String IPAdd = scan.next();
					l = new Tunnel(IPAdd, Integer.parseInt(scan.next()));
				}
				routingTable.put(addr, l);
			}
		} catch (FileNotFoundException e) {
			System.out.println("Could not open routing file");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		routingTable = new HashMap<Integer, Link>();
	}
}
