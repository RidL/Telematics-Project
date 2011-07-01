package tp.ui;

public class RouteOptions {
	private String name;
	private String TP;
	private String IP;
	private String port;
	private boolean listen;
	private boolean isConnected;
	
	public RouteOptions(){
		
	}
	
	public RouteOptions(String name, String TP, String IP, String port, boolean listen, boolean conn){
		this.name = name;
		this.TP = TP;
		this.IP = IP;
		this.port = port;
		this.listen = listen;
		this.isConnected = conn;
	}
	
	public boolean isConnected() {
		return isConnected;
	}
	public void setConnected(boolean isConnected) {
		this.isConnected = isConnected;
	}
	public String getName() {
		return name;
	}
	public String getTP() {
		return TP;
	}
	public String getIP() {
		return IP;
	}
	public String getPort() {
		return port;
	}
	public boolean isListen() {
		return listen;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setTP(String tP) {
		TP = tP;
	}
	public void setIP(String iP) {
		IP = iP;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public void setListen(boolean listen) {
		this.listen = listen;
	}
	
}
