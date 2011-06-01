package tp.trans;

import java.io.BufferedReader;
import java.io.File;
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
			//TODO:check routables
			//TODO:check links
		}
	}
	
	public void rcvSegment(Segment s){
		if(s.getDestinationAddress()==trans.getAddress()){
			trans.rcvSeg(s);
		}else{
			routableSegs.add(s);
		}
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
					Tunnel t = new Tunnel(IPAdd, Integer.parseInt(scan.next()));
					l = t;
					t.start();
				}
				routingTable.put(addr, l);
				s = read.readLine();
			}
		} catch (FileNotFoundException e) {
			System.out.println("Could not open routing file");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
