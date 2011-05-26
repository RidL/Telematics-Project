package tp.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Log {
	private static Log instance;
	private static String pref;
	private static File file;
	private static BufferedWriter write;
	
	private Log(String pref){
		Log.pref = pref;
		file = new File("tp.log");
		try {
			write = new BufferedWriter(new FileWriter(file));
		} catch (IOException e) {
			System.err.println("Could not create buffers for writing log");
			e.printStackTrace();
		}
	}
	
	public static void writeLog(String mod, String msg, boolean sysout){
		String logString = mod + " - " + msg;
		if(sysout){
			System.out.println(logString);
		}
		try {
			write.write(pref + " - " + logString);
		} catch (IOException e) {
			System.err.println("failed writing " + logString);
			e.printStackTrace();
		}
	}
	
	public Log getInstance(String pref){
		if(instance==null){
			instance = new Log(pref);
		}
		return instance;
	}
}
