package tp.test;

import tp.link.*;
import lpt.Lpt;

public class LLUnitTest {

	private static TestHLR hlr;
	private static LLSender lls;
	public static final byte[] PAYLOAD_5BYTE = new byte[]{30, 1, 5, 12};

	
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
		f.reset();
		if(first){
			lls.pushFirstFrame(f);
			first = false;
		}else{
			lls.pushFrame(f, true);
		}
		System.out.println("LLUT: frame sent");
		long time = System.currentTimeMillis();
		hlr.setExpectingAck();
		rcvdFrame =  null;
		while(true){
			if(hlr.receivedFrame()){
				 rcvdFrame = hlr.retriveFrame();
				break;
			}else if(System.currentTimeMillis() - time >= 1000 ){
				System.out.println("LLUT: timeout bij ontvangen ack ");
				break;
			}
		}
		if(rcvdFrame!=null){
			System.out.println("LLUT: frame received");
		}
		}
	}
}
