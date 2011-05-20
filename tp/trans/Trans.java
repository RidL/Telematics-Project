package tp.trans;

import java.util.ArrayList;

public class Trans extends Thread{
	private static Trans ref;
	private static Route route;
	private int address;
	
	private ArrayList<Segment> rcvBuff;
	
	private Trans(int address){
		route = new Route(this);
		this.address = address;
		route.start();
	}
	
	@Override
	public void run(){
		while(true){
			
		}
	}
	
	public int getAddress(){
		return address;
	}
	
	public static Trans getTrans(){
		if(ref==null){
			ref = new Trans(0);
		}
		return ref;
	}
	
	public void rcvSeg(Segment seg){
		rcvBuff.add(seg);
	}
}
