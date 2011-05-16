package tp.trans;

import java.util.ArrayList;

public class Trans extends Thread{
	private static Trans ref;
	private static Route route;
	private static int address;
	
	private ArrayList<Segment> rcvBuff;
	
	private Trans(){
		route = new Route(this);
		route.start();
	}
	
	@Override
	public void run(){
		while(true){
			
		}
	}
	
	public static Trans getTrans(){
		if(ref==null){
			ref = new Trans();
		}
		return ref;
	}
	
	public void rcvSeg(Segment seg){
		rcvBuff.add(seg);
	}
}
