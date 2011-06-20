package tp.trans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Observable;
import java.util.Set;

import tp.link.Link;

public class Route extends Observable implements Runnable{
	int calls;
    private final Object LOCK = new Object();
    
	private ArrayList<Segment> routableSegs;
	private Map<Integer,Link> routingTable;
	private Trans trans;
	
	public Route(Trans trans){
		routableSegs = new ArrayList<Segment>();
		routingTable = new HashMap<Integer, Link>();
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
                    if(destLink.readyToPushSegment()) {
                        destLink.pushSegment(s);
                        System.out.println("ROUTE =====pushing=====\n" + s);
                        break;
                    }
                }
            	routableSegs.remove(s);
            }
			//TODO:check routables
			//TODO:check links
		}
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
		System.out.println("ROUT push number " + (++calls) + "");
        synchronized(LOCK){
            routableSegs.add(s);
            LOCK.notifyAll();
        }
	}
	
	public void rcvSegment(Segment s){
		if(s.getDestinationAddress()==trans.getAddress()){
			System.out.println("ROUTE =====received=====\n" + s);
			trans.rcvSeg(s);
		}else{
            synchronized(LOCK) {
                routableSegs.add(s);
                LOCK.notifyAll();
            }
		}
	}
    public Object getLock() {
        return LOCK;
    }
}
