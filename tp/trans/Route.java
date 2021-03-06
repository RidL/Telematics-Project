package tp.trans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Observable;
import java.util.Set;

import tp.link.HLSender;
import tp.link.Link;
import tp.util.Log;

public class Route extends Observable implements Runnable{
	int calls;
    private final Object LOCK = new Object();
    private RouteStats stats;
	private ArrayList<Segment> routableSegs;
	private Map<Integer,Link> routingTable;
	private Trans trans;
	
	public Route(Trans trans){
		routableSegs = new ArrayList<Segment>();
		routingTable = new HashMap<Integer, Link>();
		stats = new RouteStats(routableSegs);
		this.trans = trans;
	}
	
	@Override
	public void run(){
		while(true){
			synchronized(LOCK){
				try{
					LOCK.wait();
				}catch(InterruptedException e){
					
				}
            	ArrayList<Segment> _routableSegs = new ArrayList<Segment>(routableSegs);
            	Segment s = null;
            	for(Iterator<Segment> it = _routableSegs.iterator(); it.hasNext();) {
                    s = it.next();
                    int addr = s.getDestinationAddress();
                    Link destLink = routingTable.get(addr);
                    
                    if(destLink instanceof HLSender)
                    	Log.writeLog("ROUT", "I WAS RIGHT< TWAS A HLSender", true);
                    
                    if(destLink.readyToPushSegment()) {
                        destLink.pushSegment(s);
                        break;
                    }
                }
            	routableSegs.remove(s);
            }
		}
	}
	
	public boolean hasDst(int dst){
		return routingTable.get(dst)!=null;
	}
	
	public ArrayList<Segment> getRoutableSegments(){
		return routableSegs;
	}
	
	public RouteStats getRouteStats(){
		return stats;
	}
	
	public Set<Map.Entry<Integer, Link>> getRoutes(){
		return routingTable.entrySet();
	}
	
	public void addRoute(int addr, int ref){
		Link l = routingTable.get(addr);
		if(l!=null){
			routingTable.put(ref, l);
			System.out.println("Tunnel added - " + routingTable.size());
			setChanged();
			notifyObservers();
		}else{
			System.out.println("Could not find address to tunnel over");
		}
	}
	
	public void addRoute(int addr, Link l){
		routingTable.put(addr, l);
		System.out.println("Route added - " + routingTable.size());
		setChanged();
		notifyObservers();
	}
	
	public void pushSegment(Segment s){
		Log.writeLog("ROUT", "===== Pushing =====", true);
		Log.writeLog("ROUT", s.toString(), true);
        synchronized(LOCK){
            routableSegs.add(s);
            LOCK.notifyAll();
        }
	}
	
	public void rcvSegment(Segment s){
		Log.writeLog("ROUT", "===== Received =====", true);
		System.out.println(Thread.currentThread().toString());
		Log.writeLog("ROUT", s.toString(), true);
		
		if(s.getDestinationAddress()==trans.getAddress()){
			trans.rcvSeg(s);
		}else{
            synchronized(LOCK) {
                routableSegs.add(s);
                LOCK.notifyAll();
            }
		}
	}

	public synchronized void changed() {
		setChanged();
		notifyObservers();
	}
}
