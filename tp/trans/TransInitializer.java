package tp.trans;

import tp.util.Log;

public class TransInitializer {
	public static boolean sysout = true;
	public static TPSocket sock;
	public static void main(String[] args){
		Log.getInstance("RMS");
		Log l = Log.getInstance("RMS");
		sock = Trans.getTrans().createSocket(1, 1, 1);
		System.out.println("started");
		String s = new String("robin doet helemaal niets");
		
		boolean b = sock.writeOut(s.getBytes());
		Log.writeLog("  TI", "succes in writing stuff to socket " + b, sysout);
		b = sock.writeOut(s.getBytes());
		Log.writeLog("  TI", "succes in writing stuff to socket " + b, sysout);
//		boolean b = sock.writeOut(s.getBytes());
//		Log.writeLog("  TI", "succes in writing stuff to socket " + b, sysout);
	}
	public static void readLine(){
		byte[] data = sock.readIn();
		System.out.println(new String(data));
		String rez = "";
		for(int i=0; i<data.length; i++){
			rez.concat(Character.toString((char)data[i]));
		}
		System.out.println(rez);
	}
}
