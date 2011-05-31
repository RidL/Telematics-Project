package tp.test;

import tp.link.*;
import tp.util.Log;
import lpt.Lpt;
import junit.framework.*;

 

public class LLUT {
	
	private static Lpt lpt;
	public static final byte[] PAYLOAD_8BYTE = new byte[]{10,4,10,4,10,4,10,4};
	private static int changeNr;
	
	private static void initialize() {
		lpt = new Lpt();
		lpt.writeLPT(10);
		
		
	}
	
	public static void main(String arg[]){
		initialize();
		
		testNormalFrame();
	}

	private static void testNormalFrame() {
		
		pushFrame((byte)1,PAYLOAD_8BYTE);
		readAck();
	}
	
	private static void readAck() {
		if(changeNr!=Frame.ONES)
			getNextRead();
		if(changeNr!=Frame.ONES){
			
		}
		
	}

	private static void pushFrame(byte head, byte[] data){
		changeNr = lpt.readLPT();
		lpt.writeLPT(31);
		getNextRead();
		lpt.writeLPT(head);
		int lastNr = head;
		for(int i=0;i<8;i++){
			getNextRead();
			if(lastNr!=data[i]){
				lpt.writeLPT(data[i]);
			}else{
				lpt.writeLPT(0);
				getNextRead();
				lpt.writeLPT(data[i]);
			}
			lastNr=data[i];
		}
		getNextRead();
		lpt.writeLPT(31);
    	getNextRead();
    	lpt.writeLPT(0);
    	getNextRead();
	}

    private static void getNextRead() {
        int nr;
        while (true) {
            nr = lpt.readLPT();
            if (nr != changeNr) {
            	int i = (int) Math.random() * 9;
            	i++;
                changeNr = lpt.readLPT();
                break;
            }
        }
    }
    
    
}
