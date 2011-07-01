package tp.trans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RouteStats {
	ArrayList<Segment> routes;
	
	private int inNum;
	private Map<Integer,Integer> inPort;
	
	private int outNum;
	private Map<Integer,Integer> outAddr;
		
	private int routedNum;
	private Map<Integer,Integer> routedAddr;
	
	public RouteStats(ArrayList<Segment> routes){
		this.routes = routes;
		inPort = new HashMap<Integer, Integer>();
		outAddr = new HashMap<Integer, Integer>();
		routedAddr  = new HashMap<Integer, Integer>();
	}
	
	public void reset(){
		outNum = 0;
		inNum = 0;
		inPort.clear();
		routedNum = 0;
		routedAddr.clear();
	}
	
	public void addIn(Segment s){
		inNum++;
		int prt = s.getDestinationPort();
		if(inPort.get(prt)==null){
			inPort.put(prt, 1);
		}else{
			int num = inPort.get(prt);
			inPort.put(prt, num+1);
		}
	}
	
	public void addOut(Segment s){
		outNum++;
		int addr = s.getDestinationAddress();
		if(outAddr.get(addr)==null){
			outAddr.put(addr, 1);
		}else{
			int num = outAddr.get(addr);
			outAddr.put(addr, num+1);
		}
	}
	
	public void addRouted(Segment s){
		routedNum++;
		int addr = s.getDestinationAddress();
		if(routedAddr.get(addr)==null){
			routedAddr.put(addr, 1);
		}else{
			int num = routedAddr.get(addr);
			routedAddr.put(addr, num+1);
		}
	}
	
	public ArrayList<Segment> getRoutes() {
		return routes;
	}

	public int getInNum() {
		return inNum;
	}

	public Map<Integer, Integer> getInPort() {
		return inPort;
	}

	public int getOutNum() {
		return outNum;
	}

	public Map<Integer, Integer> getOutAddr() {
		return outAddr;
	}

	public int getRoutedNum() {
		return routedNum;
	}

	public Map<Integer, Integer> getRoutedAddr() {
		return routedAddr;
	}
}
