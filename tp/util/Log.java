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
		file.delete();
		try {
			file.createNewFile();
			write = new BufferedWriter(new FileWriter(file));
		} catch (IOException e) {
			System.err.println("Could not create buffers for writing log");
			e.printStackTrace();
		}
	}
	
	public static synchronized void writeLog(String mod, String msg, boolean sysout){
		String logString = mod + " - " + msg;
		if(sysout){
			System.out.println(logString);
		}
		try {
			write.write(pref + " - " + logString);
			write.newLine();
			write.flush();
		} catch (IOException e) {
			System.err.println("failed writing " + logString);
			e.printStackTrace();
		}
	}
	
	public static Log getInstance(String pref){
		if(instance==null){
			instance = new Log(pref);
		}
		return instance;
	}
}
