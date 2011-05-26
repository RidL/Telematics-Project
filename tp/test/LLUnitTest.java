package tp.test;

import tp.link.*;
import lpt.Lpt;

public class LLUnitTest {

	private static TestHLR hlr;
	private static LLSender lls;
	public static final byte[] PAYLOAD_5BYTE = new byte[]{10,4,10,4,10};

	
	public static void main(String[] args){
	        hlr = new TestHLR();
	        hlr.start();
	        lls = new LLSender();
	        
	        testOneFrameSegment();
	}
	
	public static void testOneFrameSegment(){
		
		hlr.setSenderActive(true);
		Frame f = new Frame(PAYLOAD_5BYTE,false, true);
		Frame rcvdFrame = null;
		boolean first = true;

		
		while(true){
			System.out.println("LLUT: Starting new run");
		f.reset();
		if(first){
			lls.pushFirstFrame(f);
			first = false;
		}else{
			lls.pushFrame(f, true);
		}
		long time = System.currentTimeMillis();
		hlr.setExpectingAck();
		rcvdFrame =  null;
		System.out.println("Ik kom hier!");
		while(true){
			if(hlr.receivedFrame()){
				System.out.println("LLUT: Frame received");
				 rcvdFrame = hlr.retriveFrame();
				 System.out.println("LLUT: Frame retrived");
				break;
			}
//			else if(System.currentTimeMillis() - time >= 1000 ){
//				System.out.println("LLUT: timeout bij ontvangen ack ");
//				break;
//			}
		}
		 System.out.println("LLUT: End of run");
		}
	}
}
