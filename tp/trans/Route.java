package tp.trans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Observable;
import java.util.Set;

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
                    System.out.println("Should be addr: " + addr);
                    Link destLink = routingTable.get(addr);
                    if(destLink.readyToPushSegment()) {
                        destLink.pushSegment(s);
                        System.out.println("ROUTE =====pushing=====\n" + s);
                        break;
                    }
                }
            	routableSegs.remove(s);
//            	setChanged();
//        		notifyObservers();
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
            stats.addOut(s);
//            setChanged();
//    		notifyObservers();
            LOCK.notifyAll();
        }
	}
	
	public void rcvSegment(Segment s){
		Log.writeLog("ROUT", "===== Received =====", true);
		Log.writeLog("ROUT", s.toString(), true);
		
		if(s.getDestinationAddress()==trans.getAddress()){
			trans.rcvSeg(s);
			stats.addIn(s);
		}else{
            synchronized(LOCK) {
                routableSegs.add(s);
                stats.addRouted(s);
                LOCK.notifyAll();
            }
		}
//		setChanged();
//		notifyObservers();
	}
}
