package tp.trans;

import tp.util.Log;

public class TransInitializer {
	public static boolean sysout = true;
	public static void main(String[] args){
		TPSocket sock = Trans.getTrans().createSocket(1, 1, 1);
		String s = new String("martijnishomo");
		Log.writeLog("  TI", "succes in writing stuff to socket " + sock.writeOut(s.getBytes()), sysout);
	}
}
