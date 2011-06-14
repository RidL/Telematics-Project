package tp.trans;

import tp.util.Log;

public class TransInitializer {
	public static boolean sysout = true;
	
	public static void main(String[] args){
<<<<<<< HEAD
		Log.getInstance("RMS");
=======
		Log l = Log.getInstance("RMS");
		System.out.println("starting");
>>>>>>> 3385d33f31d0ddd138c8a44488d7062459a65557
		TPSocket sock = Trans.getTrans().createSocket(1, 1, 1);
		System.out.println("started");
		String s = new String("robin doet helemaal niets");
		boolean b = sock.writeOut(s.getBytes());
		Log.writeLog("  TI", "succes in writing stuff to socket " + b, sysout);
	}
}
