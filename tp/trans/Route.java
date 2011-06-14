package tp.trans;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

import tp.link.Link;
import tp.link.Tunnel;
import tp.util.Log;

public class Route extends Thread {

    private final Object LOCK = new Object();
    
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
            Iterator it = routableSegs.iterator();
            while(it.hasNext()) {
                Segment s = (Segment) it.next();

                int addr = s.getDestinationAddress();
                Link destLink = routingTable.get(addr);
                if(destLink.readyToPushSegment()) {
                    destLink.pushSegment(s);
                    synchronized(LOCK) {
                        it.remove();
                    }
                }
            }
			//TODO:check routables
			//TODO:check links
		}
	}
	
	public void rcvSegment(Segment s){
		if(s.getDestinationAddress()==trans.getAddress()){
			trans.rcvSeg(s);
		}else{
            synchronized(LOCK) {
                routableSegs.add(s);
            }
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
					Tunnel t = new Tunnel(IPAdd, Integer.parseInt(scan.next()),this);
					l = t;
					t.start();
				}
				routingTable.put(addr, l);
				s = read.readLine();
			}
		} catch (FileNotFoundException e) {
			System.out.println("Could not open routing file");
			//e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    public Object getLock() {
        return LOCK;
    }

    public static void main(String[] args) {
        Log.getInstance("RT");
        Trans t = Trans.getTrans();
        Route r = new Route(t);
        for(Segment s: r.routableSegs) {
            System.out.println("Segment: " + s);
        }
        for(Integer i: r.routingTable.keySet()) {
            System.out.println("rt " + i + "--" + r.routingTable.get(i));
        }
        System.out.println("");
    }
}
