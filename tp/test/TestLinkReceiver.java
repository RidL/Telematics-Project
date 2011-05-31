package tp.test;

import tp.link.Frame;
import lpt.Lpt;
import junit.framework.*;

public class TestLinkReceiver extends TestCase{
	
	private static Lpt lpt = new Lpt();
	public static final byte[] PAYLOAD_8BYTE = new byte[]{10,4,10,4,10,4,10,4};
	private static int changeNr;
	private static boolean alt = false;

	public void testNormalFrame(){
		pushFrame((byte)8,PAYLOAD_8BYTE);
		readAck();
		
	}
	
	private static void readAck() {
		if(changeNr!=Frame.ONES)
			getNextRead();
		assertNotSame("readAck: ontvangt geen flag voor ack",Frame.ONES, changeNr);
		sendResponse();
		for(int i=0;i<10;i++){
			
		}
		
	}

	private static void pushFrame(byte head, byte[] data){
		changeNr = lpt.readLPT();
		assertNotSame("Bij begin verzenden frame geen 31 op kabel", changeNr, Frame.ONES);
		lpt.writeLPT(31);
		getNextRead();
		assertNotSame("Na eerste flag geen flag terug", changeNr, Frame.ONES);
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
		assertEquals("trololo", null, null);
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
    
    private static void sendResponse() {
        if (alt) {
            lpt.writeLPT(4);
        } else {
            lpt.writeLPT(10);
        }
        alt=!alt;
    }
}
