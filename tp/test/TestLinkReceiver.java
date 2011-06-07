package tp.test;

import tp.link.Frame;
import lpt.Lpt;
import junit.framework.*;

public class TestLinkReceiver extends TestCase{
	
	private static Lpt lpt = new Lpt();
	public static final byte[] PAYLOAD_8BYTE = new byte[]{10,4,10,4,10,4,10,4};
	public static final byte[] PAYLOAD_9BYTE = new byte[]{10,4,10,4,10,4,10,4,10};
	private static int changeNr = -1;
	private static boolean alt = false;

	public void testStart(){
		lpt.writeLPT(10);
	}
	
	public void testOneFrameSegment(){
		pushFrame((byte)12,PAYLOAD_8BYTE);
		getNextRead();
		System.out.println(changeNr);
		if(changeNr!=Frame.ONES){
			getNextRead();
			System.out.println(changeNr);
		}
		assertEquals("readAck: ontvangt geen flag voor ack",Frame.ONES, changeNr);
		sendResponse();
		System.out.println("response send");
		getNextRead();
		sendResponse();
		assertTrue("Geen ack header ontvangen",changeNr>0);
		getNextRead();
		sendResponse();
		assertEquals("Eerste gedeelte ack fout",changeNr,-1);
		getNextRead();
		sendResponse();
		assertEquals("tweede gedeelte ack fout",changeNr,-9);
		while(changeNr!=Frame.ONES){
			getNextRead();
			sendResponse();
		}
	}
	
	public void testThreeFrameSegment(){
		for(int i=0;i<2;i++){
			pushFrame((byte)1,PAYLOAD_8BYTE);	
		}
		pushFrame((byte)12,PAYLOAD_8BYTE);
		getNextRead();
		System.out.println("TLR: IN:"+(((((byte)(changeNr)) >> 3) & 0x1f) ^ 0x10));
		if(changeNr!=Frame.ONES){
			getNextRead();
			System.out.println("TLR: IN:"+(((((byte)(changeNr)) >> 3) & 0x1f) ^ 0x10));
		}
		assertEquals("readAck: ontvangt geen flag voor ack",Frame.ONES, changeNr);
		sendResponse();
		System.out.println("TLR: OUT LOWLVLRESPONSE");
		getNextRead();
		System.out.println("TLR: IN:"+(((((byte)(changeNr)) >> 3) & 0x1f) ^ 0x10));
		sendResponse();
		System.out.println("TLR: OUT LOWLVLRESPONSE");
		assertTrue("Geen ack header ontvangen",changeNr>0);
		getNextRead();
		System.out.println("TLR: IN:"+(((((byte)(changeNr)) >> 3) & 0x1f) ^ 0x10));
		sendResponse();
		System.out.println("TLR: OUT LOWLVLRESPONSE");
		System.out.println("Eerste gedeelte ack: "+Frame.toBinaryString((byte)changeNr));
		assertTrue("Eerste gedeelte ack fout",changeNr==-97);
		getNextRead();
		System.out.println("TLR: IN:"+(((((byte)(changeNr)) >> 3) & 0x1f) ^ 0x10));
		sendResponse();
		System.out.println("TLR: OUT LOWLVLRESPONSE");
		System.out.println("Tweede gedeelte ack: "+Frame.toBinaryString((byte)changeNr));
		System.out.println("TLR: ChangeNR : "+changeNr);
		assertTrue("tweede gedeelte ack fout",changeNr==87);
		while(changeNr!=Frame.ONES){
			getNextRead();
			sendResponse();
		}
		
	}
	public void testEigthFrameSegment(){
		for(int i=0;i<7;i++){
			pushFrame((byte)1,PAYLOAD_8BYTE);	
		}
		pushFrame((byte)12,PAYLOAD_8BYTE);
		
		getNextRead();
		System.out.println("TLR: IN:"+(((((byte)(changeNr)) >> 3) & 0x1f) ^ 0x10));
		if(changeNr!=Frame.ONES){
			getNextRead();
			System.out.println("TLR: IN:"+(((((byte)(changeNr)) >> 3) & 0x1f) ^ 0x10));
		}
		System.out.println("TLR: IN:"+(((((byte)(changeNr)) >> 3) & 0x1f) ^ 0x10));
		assertEquals("readAck: ontvangt geen flag voor ack",Frame.ONES, changeNr);
		sendResponse();
		System.out.println("TLR: OUT LOWLVLRESPONSE");
		getNextRead();
		System.out.println("TLR: IN:"+(((((byte)(changeNr)) >> 3) & 0x1f) ^ 0x10));
		sendResponse();
		System.out.println("TLR: OUT LOWLVLRESPONSE");
		assertTrue("Geen ack header ontvangen",changeNr>0);
		getNextRead();
		System.out.println("TLR: IN:"+(((((byte)(changeNr)) >> 3) & 0x1f) ^ 0x10));
		sendResponse();
		System.out.println("TLR: OUT LOWLVLRESPONSE");
		assertTrue("Eerste gedeelte ack fout",changeNr==-113);
		getNextRead();
		System.out.println("TLR: IN:"+(((((byte)(changeNr)) >> 3) & 0x1f) ^ 0x10));
		sendResponse();
		System.out.println("TLR: OUT LOWLVLRESPONSE");
		getNextRead();
		System.out.println("TLR: IN:"+(((((byte)(changeNr)) >> 3) & 0x1f) ^ 0x10));
		sendResponse();
		System.out.println("TLR: OUT LOWLVLRESPONSE");
		assertTrue("tweede gedeelte ack fout",changeNr==-113);
		while(changeNr!=Frame.ONES){
			getNextRead();
			sendResponse();
		}
		
	}
	public void testNineFrameSegment(){
		for(int i=0;i<8;i++){
			pushFrame((byte)1,PAYLOAD_8BYTE);	
		}
		getNextRead();
		System.out.println("TLR: IN:"+(((((byte)(changeNr)) >> 3) & 0x1f) ^ 0x10));
		if(changeNr!=Frame.ONES){
			getNextRead();
			System.out.println("TLR: IN:"+(((((byte)(changeNr)) >> 3) & 0x1f) ^ 0x10));
		}
		System.out.println("TLR: IN:"+(((((byte)(changeNr)) >> 3) & 0x1f) ^ 0x10));
		assertEquals("readAck: ontvangt geen flag voor ack",Frame.ONES, changeNr);
		sendResponse();
		System.out.println("TLR: OUT LOWLVLRESPONSE");
		getNextRead();
		System.out.println("TLR: IN:"+(((((byte)(changeNr)) >> 3) & 0x1f) ^ 0x10));
		sendResponse();
		System.out.println("TLR: OUT LOWLVLRESPONSE");
		assertTrue("Geen ack header ontvangen",changeNr>0);
		getNextRead();
		System.out.println("TLR: IN:"+(((((byte)(changeNr)) >> 3) & 0x1f) ^ 0x10));
		sendResponse();
		System.out.println("TLR: OUT LOWLVLRESPONSE");
		assertTrue("Eerste gedeelte ack fout",changeNr==-113);
		getNextRead();
		System.out.println("TLR: IN:"+(((((byte)(changeNr)) >> 3) & 0x1f) ^ 0x10));
		sendResponse();
		System.out.println("TLR: OUT LOWLVLRESPONSE");
		getNextRead();
		System.out.println("TLR: IN:"+(((((byte)(changeNr)) >> 3) & 0x1f) ^ 0x10));
		sendResponse();
		System.out.println("TLR: OUT LOWLVLRESPONSE");
		assertTrue("tweede gedeelte ack fout",changeNr==-113);
		while(changeNr!=Frame.ONES){
			getNextRead();
			sendResponse();
		}
		
		pushFrame((byte)12,PAYLOAD_8BYTE);
		getNextRead();
		if(changeNr!=Frame.ONES){
			getNextRead();
			System.out.println(changeNr);
		}
		assertEquals("readAck: ontvangt geen flag voor ack",Frame.ONES, changeNr);
		sendResponse();
		getNextRead();
		sendResponse();
		assertTrue("Geen ack header ontvangen",changeNr>0);
		getNextRead();
		sendResponse();
		assertTrue("Eerste gedeelte ack fout",changeNr==-1);
		getNextRead();
		sendResponse();
		assertTrue("tweede gedeelte ack fout",changeNr==-9);
		while(changeNr!=Frame.ONES){
			getNextRead();
			sendResponse();
		}
		
	}
	
	public void testFirstParityBroken(){
		System.out.println("TLR:  start test FirstParityBroken");
		pushFrame((byte)8,PAYLOAD_8BYTE);
		System.out.println("TLR:  Verzonden frame");
		getNextRead();
		if(changeNr!=Frame.ONES){
			getNextRead();
			System.out.println(changeNr);
		}
		System.out.println("INC: "+ changeNr+ " = Frames.ONES = "+Frame.ONES);
		assertEquals("readAck: ontvangt geen flag voor ack",Frame.ONES, changeNr);
		sendResponse();
		getNextRead();
		sendResponse();
		assertTrue("Geen ack header ontvangen",changeNr>0);
		getNextRead();
		sendResponse();
		System.out.println("Eerste gedeelte ack: "+Frame.toBinaryString((byte)changeNr));
		assertTrue("Eerste gedeelte ack fout",changeNr==119);
		getNextRead();
		sendResponse();
		getNextRead();
		sendResponse();
		System.out.println("Eerste gedeelte ack: "+Frame.toBinaryString((byte)changeNr));
		assertTrue("tweede gedeelte ack fout",changeNr==119);
		System.out.println("TLR: beide ack's ontvangen");
		while(changeNr!=Frame.ONES){
			getNextRead();
			sendResponse();
		}
		
		pushFrame((byte)12,PAYLOAD_8BYTE);
		getNextRead();
		if(changeNr!=Frame.ONES){
			getNextRead();
			System.out.println(changeNr);
		}
		assertEquals("readAck: ontvangt geen flag voor ack",Frame.ONES, changeNr);
		sendResponse();
		getNextRead();
		sendResponse();
		assertTrue("Geen ack header ontvangen",changeNr>0);
		getNextRead();
		sendResponse();
		assertTrue("Eerste gedeelte ack fout",changeNr==-1);
		getNextRead();
		sendResponse();
		assertTrue("tweede gedeelte ack fout",changeNr==-9);
		while(changeNr!=Frame.ONES){
			getNextRead();
			sendResponse();
		}
	}
	
	public void testSecondParityBroken(){
		pushFrame((byte)14,PAYLOAD_8BYTE);
		getNextRead();
		if(changeNr!=Frame.ONES){
			getNextRead();
			System.out.println(changeNr);
		}
		assertEquals("readAck: ontvangt geen flag voor ack",Frame.ONES, changeNr);
		sendResponse();
		getNextRead();
		sendResponse();
		assertTrue("Geen ack header ontvangen",changeNr>0);
		getNextRead();
		sendResponse();
		assertTrue("Eerste gedeelte ack fout",changeNr==119);
		getNextRead();
		sendResponse();
		getNextRead();
		sendResponse();
		assertTrue("tweede gedeelte ack fout",changeNr==119);
		while(changeNr!=Frame.ONES){
			getNextRead();
			sendResponse();
		}
		pushFrame((byte)12,PAYLOAD_8BYTE);
		getNextRead();
		if(changeNr!=Frame.ONES){
			getNextRead();
			System.out.println(changeNr);
		}
		assertEquals("readAck: ontvangt geen flag voor ack",Frame.ONES, changeNr);
		sendResponse();
		getNextRead();
		sendResponse();
		assertTrue("Geen ack header ontvangen",changeNr>0);
		getNextRead();
		sendResponse();
		assertTrue("Eerste gedeelte ack fout",changeNr==-1);
		getNextRead();
		sendResponse();
		assertTrue("tweede gedeelte ack fout",changeNr==-9);
		while(changeNr!=Frame.ONES){
			getNextRead();
			sendResponse();
		}
		System.out.println("ACK ONTVANGE, VOLGENDE TEST!");
	}
	
	public void testTwiceSame5bits(){
		System.out.println("TLR:  start test FirstParityBroken");
		byte head = (byte)8;
		byte data[] = PAYLOAD_8BYTE;
		//--------
		getNextRead();
		System.out.println("New frame ga ik verzenden nu!");
		System.out.println("TLR: IN:"+(((((byte)(changeNr)) >> 3) & 0x1f) ^ 0x10));
		assertNotSame("Bij begin verzenden frame geen 31 op kabel", changeNr, Frame.ONES);
		lpt.writeLPT(31);
		System.out.println("TLR: OUT 31");
		getNextRead();
		System.out.println("TLR: IN:"+(((((byte)(changeNr)) >> 3) & 0x1f) ^ 0x10));
		assertNotSame("Na eerste flag geen flag terug", changeNr, Frame.ONES);
		lpt.writeLPT(head);
		int lastNr = head;
		for(int i=0;i<7;i++){
			getNextRead();
			System.out.println("TLR: IN:"+(((((byte)(changeNr)) >> 3) & 0x1f) ^ 0x10));
			if(i==5){
				lpt.writeLPT(data[i]);
				getNextRead();
			}
			if(lastNr!=data[i]){
				lpt.writeLPT(data[i]);
				System.out.println("TLR: OUT:"+data[i]);
			}else{
				lpt.writeLPT(0);
				System.out.println("TLR: OUT: 0");
				getNextRead();
				System.out.println("TLR: IN:"+(((((byte)(changeNr)) >> 3) & 0x1f) ^ 0x10));
				lpt.writeLPT(data[i]);
				System.out.println("TLR: OUT:"+data[i]);
			}
			lastNr=data[i];
		}
		getNextRead();
		System.out.println("TLR: IN:"+(((((byte)(changeNr)) >> 3) & 0x1f) ^ 0x10));
		lpt.writeLPT(31);
		System.out.println("TLR: OUT 31");
    	getNextRead();
    	System.out.println("TLR: IN:"+(((((byte)(changeNr)) >> 3) & 0x1f) ^ 0x10));
    	lpt.writeLPT(0);
    	System.out.println("TLR: OUT 0");
		//-------------
		System.out.println("TLR:  Verzonden frame");
		getNextRead();
		if(changeNr!=Frame.ONES){
			getNextRead();
			System.out.println(changeNr);
		}
		System.out.println("INC: "+ changeNr+ " = Frames.ONES = "+Frame.ONES);
		assertEquals("readAck: ontvangt geen flag voor ack",Frame.ONES, changeNr);
		sendResponse();
		getNextRead();
		sendResponse();
		assertTrue("Geen ack header ontvangen",changeNr>0);
		getNextRead();
		sendResponse();
		System.out.println("Eerste gedeelte ack: "+Frame.toBinaryString((byte)changeNr));
		assertTrue("Eerste gedeelte ack fout",changeNr==119);
		getNextRead();
		sendResponse();
		getNextRead();
		sendResponse();
		System.out.println("Eerste gedeelte ack: "+Frame.toBinaryString((byte)changeNr));
		assertTrue("tweede gedeelte ack fout",changeNr==119);
		System.out.println("TLR: beide ack's ontvangen");
		while(changeNr!=Frame.ONES){
			getNextRead();
			sendResponse();
		}
		
		pushFrame((byte)12,PAYLOAD_8BYTE);
		getNextRead();
		if(changeNr!=Frame.ONES){
			getNextRead();
			System.out.println(changeNr);
		}
		assertEquals("readAck: ontvangt geen flag voor ack",Frame.ONES, changeNr);
		sendResponse();
		getNextRead();
		sendResponse();
		assertTrue("Geen ack header ontvangen",changeNr>0);
		getNextRead();
		sendResponse();
		assertTrue("Eerste gedeelte ack fout",changeNr==-1);
		getNextRead();
		sendResponse();
		assertTrue("tweede gedeelte ack fout",changeNr==-9);
		while(changeNr!=Frame.ONES){
			getNextRead();
			sendResponse();
		}
		System.out.println("Done the   teast yeah");
	}
	
	public void testBrokenEndFlagMidData(){
		for(int i=0;i<3;i++){
			pushFrame((byte)1,PAYLOAD_8BYTE);	
		}
		//-----------
		byte head = 1;
		byte data [] = PAYLOAD_8BYTE;
		getNextRead();
		System.out.println("New frame ga ik verzenden nu!");
		System.out.println("TLR: IN:"+(((((byte)(changeNr)) >> 3) & 0x1f) ^ 0x10));
		assertNotSame("Bij begin verzenden frame geen 31 op kabel", changeNr, Frame.ONES);
		lpt.writeLPT(31);
		System.out.println("TLR: OUT 31");
		getNextRead();
		System.out.println("TLR: IN:"+(((((byte)(changeNr)) >> 3) & 0x1f) ^ 0x10));
		assertNotSame("Na eerste flag geen flag terug", changeNr, Frame.ONES);
		lpt.writeLPT(head);
		int lastNr = head;
		for(int i=0;i<8;i++){
			getNextRead();
			System.out.println("TLR: IN:"+(((((byte)(changeNr)) >> 3) & 0x1f) ^ 0x10));
			if(lastNr!=data[i]){
				lpt.writeLPT(data[i]);
				System.out.println("TLR: OUT:"+data[i]);
			}else{
				lpt.writeLPT(0);
				System.out.println("TLR: OUT: 0");
				getNextRead();
				System.out.println("TLR: IN:"+(((((byte)(changeNr)) >> 3) & 0x1f) ^ 0x10));
				lpt.writeLPT(data[i]);
				System.out.println("TLR: OUT:"+data[i]);
			}
			lastNr=data[i];
		}
		getNextRead();
		System.out.println("TLR: IN:"+(((((byte)(changeNr)) >> 3) & 0x1f) ^ 0x10));
		lpt.writeLPT(28);
		System.out.println("TLR: OUT BROKEN 31 > 30");
    	getNextRead();
    	System.out.println("TLR: IN:"+(((((byte)(changeNr)) >> 3) & 0x1f) ^ 0x10));
    	lpt.writeLPT(0);
    	System.out.println("TLR: OUT 0");
		//-----------
		for(int i=0;i<3;i++){
			pushFrame((byte)1,PAYLOAD_8BYTE);	
		}
		pushFrame((byte)12,PAYLOAD_8BYTE);
		
		System.out.println("TLR:  Verzonden frame");
		getNextRead();
		if(changeNr!=Frame.ONES){
			getNextRead();
			System.out.println(changeNr);
		}
		System.out.println("INC: "+ changeNr+ " = Frames.ONES = "+Frame.ONES);
		assertEquals("readAck: ontvangt geen flag voor ack",Frame.ONES, changeNr);
		sendResponse();
		getNextRead();
		sendResponse();
		assertTrue("Geen ack header ontvangen",changeNr>0);
		getNextRead();
		sendResponse();
		System.out.println("Eerste gedeelte ack: "+Frame.toBinaryString((byte)changeNr));
		assertTrue("Eerste gedeelte ack fout",changeNr==-105);
		getNextRead();
		sendResponse();
		getNextRead();
		sendResponse();
		System.out.println("Eerste gedeelte ack: "+Frame.toBinaryString((byte)changeNr));
		assertTrue("tweede gedeelte ack fout",changeNr==-105);
		System.out.println("TLR: beide ack's ontvangen");
		while(changeNr!=Frame.ONES){
			getNextRead();
			sendResponse();
		}
		//Retransmit + ack of  frame 3
		pushFrame((byte)12,PAYLOAD_8BYTE);
		getNextRead();
		System.out.println("TLR: changeNr: "+changeNr);
		if(changeNr!=Frame.ONES){
			getNextRead();
			System.out.println(changeNr);
		}
		System.out.println("INC: "+ changeNr+ " = Frames.ONES = "+Frame.ONES);
		assertEquals("readAck: ontvangt geen flag voor ack",Frame.ONES, changeNr);
		sendResponse();
		getNextRead();
		sendResponse();
		assertTrue("Geen ack header ontvangen",changeNr>0);
		getNextRead();
		sendResponse();
		System.out.println("Eerste gedeelte ack: "+Frame.toBinaryString((byte)changeNr));
		assertTrue("Eerste gedeelte ack fout",changeNr==-113);
		getNextRead();
		sendResponse();
		getNextRead();
		sendResponse();
		System.out.println("Eerste gedeelte ack: "+Frame.toBinaryString((byte)changeNr));
		assertTrue("tweede gedeelte ack fout",changeNr==-113);
		System.out.println("TLR: beide ack's ontvangen");
		System.out.println("INC: "+ changeNr);
		while(changeNr!=Frame.ONES){
			getNextRead();
			System.out.println("INC: ILOOP "+ changeNr);
			sendResponse();
		}
		
	}
	
	public void testBrokenEndAndBeginFlagMidData(){
		for(int i=0;i<3;i++){
			pushFrame((byte)1,PAYLOAD_8BYTE);	
		}
		//----------- first frame
		byte head = 1;
		byte data [] = PAYLOAD_8BYTE;
		getNextRead();
		System.out.println("New frame ga ik verzenden nu!");
		System.out.println("TLR: IN:"+(((((byte)(changeNr)) >> 3) & 0x1f) ^ 0x10));
		assertNotSame("Bij begin verzenden frame geen 31 op kabel", changeNr, Frame.ONES);
		lpt.writeLPT(31);
		System.out.println("TLR: OUT 31");
		getNextRead();
		System.out.println("TLR: IN:"+(((((byte)(changeNr)) >> 3) & 0x1f) ^ 0x10));
		assertNotSame("Na eerste flag geen flag terug", changeNr, Frame.ONES);
		lpt.writeLPT(head);
		int lastNr = head;
		for(int i=0;i<8;i++){
			getNextRead();
			System.out.println("TLR: IN:"+(((((byte)(changeNr)) >> 3) & 0x1f) ^ 0x10));
			if(lastNr!=data[i]){
				lpt.writeLPT(data[i]);
				System.out.println("TLR: OUT:"+data[i]);
			}else{
				lpt.writeLPT(0);
				System.out.println("TLR: OUT: 0");
				getNextRead();
				System.out.println("TLR: IN:"+(((((byte)(changeNr)) >> 3) & 0x1f) ^ 0x10));
				lpt.writeLPT(data[i]);
				System.out.println("TLR: OUT:"+data[i]);
			}
			lastNr=data[i];
		}
		getNextRead();
		System.out.println("TLR: IN:"+(((((byte)(changeNr)) >> 3) & 0x1f) ^ 0x10));
		lpt.writeLPT(28);
		System.out.println("TLR: END FLAG BROKEN 31 >28");
    	getNextRead();
    	System.out.println("TLR: IN:"+(((((byte)(changeNr)) >> 3) & 0x1f) ^ 0x10));
    	lpt.writeLPT(0);
    	System.out.println("TLR: OUT 0");
    	
    	// -------- second frame
    	

		getNextRead();
		System.out.println("New frame ga ik verzenden nu!");
		System.out.println("TLR: IN:"+(((((byte)(changeNr)) >> 3) & 0x1f) ^ 0x10));
		assertNotSame("Bij begin verzenden frame geen 31 op kabel", changeNr, Frame.ONES);
		lpt.writeLPT(2);
		System.out.println("TLR: BEGIN FLAG BROKEN 31>1");
		getNextRead();
		System.out.println("TLR: IN:"+(((((byte)(changeNr)) >> 3) & 0x1f) ^ 0x10));
		assertNotSame("Na eerste flag geen flag terug", changeNr, Frame.ONES);
		lpt.writeLPT(head);
		lastNr = head;
		for(int i=0;i<8;i++){
			getNextRead();
			System.out.println("TLR: IN:"+(((((byte)(changeNr)) >> 3) & 0x1f) ^ 0x10));
			if(lastNr!=data[i]){
				lpt.writeLPT(data[i]);
				System.out.println("TLR: OUT:"+data[i]);
			}else{
				lpt.writeLPT(0);
				System.out.println("TLR: OUT: 0");
				getNextRead();
				System.out.println("TLR: IN:"+(((((byte)(changeNr)) >> 3) & 0x1f) ^ 0x10));
				lpt.writeLPT(data[i]);
				System.out.println("TLR: OUT:"+data[i]);
			}
			lastNr=data[i];
		}
		getNextRead();
		System.out.println("TLR: IN:"+(((((byte)(changeNr)) >> 3) & 0x1f) ^ 0x10));
		lpt.writeLPT(31);
		System.out.println("TLR: OUT 31");
    	getNextRead();
    	System.out.println("TLR: IN:"+(((((byte)(changeNr)) >> 3) & 0x1f) ^ 0x10));
    	lpt.writeLPT(0);
    	System.out.println("TLR: OUT 0");
    	
		//-----------
		for(int i=0;i<3;i++){
			pushFrame((byte)1,PAYLOAD_8BYTE);	
		}

		System.out.println("TLR:  Verzonden frame");
		getNextRead();
		if(changeNr!=Frame.ONES){
			getNextRead();
			System.out.println(changeNr);
		}
		System.out.println("INC: "+ changeNr+ " = Frames.ONES = "+Frame.ONES);
		assertEquals("readAck: ontvangt geen flag voor ack",Frame.ONES, changeNr);
		sendResponse();
		getNextRead();
		sendResponse();
		assertTrue("Geen ack header ontvangen",changeNr>0);
		getNextRead();
		sendResponse();
		System.out.println("Eerste gedeelte ack: "+Frame.toBinaryString((byte)changeNr));
		assertTrue("Eerste gedeelte ack fout",changeNr==119);
		getNextRead();
		sendResponse();
		getNextRead();
		sendResponse();
		System.out.println("Eerste gedeelte ack: "+Frame.toBinaryString((byte)changeNr));
		assertTrue("tweede gedeelte ack fout",changeNr==119);
		System.out.println("TLR: beide ack's ontvangen");
		while(changeNr!=Frame.ONES){
			getNextRead();
			sendResponse();
		}
		//Retransmit + ack of  frame 3
		for(int i=0;i<7;i++){
			pushFrame((byte)1,PAYLOAD_8BYTE);	
		}
		pushFrame((byte)12,PAYLOAD_8BYTE);
		getNextRead();
		System.out.println("TLR: changeNr: "+changeNr);
		if(changeNr!=Frame.ONES){
			getNextRead();
			System.out.println(changeNr);
		}
		System.out.println("INC: "+ changeNr+ " = Frames.ONES = "+Frame.ONES);
		assertEquals("readAck: ontvangt geen flag voor ack",Frame.ONES, changeNr);
		sendResponse();
		getNextRead();
		sendResponse();
		assertTrue("Geen ack header ontvangen",changeNr>0);
		getNextRead();
		sendResponse();
		System.out.println("Eerste gedeelte ack: "+Frame.toBinaryString((byte)changeNr));
		assertTrue("Eerste gedeelte ack fout",changeNr==-113);
		getNextRead();
		sendResponse();
		getNextRead();
		sendResponse();
		System.out.println("Eerste gedeelte ack: "+Frame.toBinaryString((byte)changeNr));
		assertTrue("tweede gedeelte ack fout",changeNr==-113);
		System.out.println("TLR: beide ack's ontvangen");
		System.out.println("INC: "+ changeNr);
		while(changeNr!=Frame.ONES){
			getNextRead();
			System.out.println("INC: ILOOP "+ changeNr);
			sendResponse();
		}
		
	}
	
	public void testBrokenBeginFlagMidData(){
		for(int i=0;i<3;i++){
			pushFrame((byte)1,PAYLOAD_8BYTE);	
		}
		//-----------
		byte head = 1;
		byte data [] = PAYLOAD_8BYTE;
		getNextRead();
		System.out.println("New frame ga ik verzenden nu!");
		System.out.println("TLR: IN:"+(((((byte)(changeNr)) >> 3) & 0x1f) ^ 0x10));
		assertNotSame("Bij begin verzenden frame geen 31 op kabel", changeNr, Frame.ONES);
		lpt.writeLPT(1);
		System.out.println("TLR: OUT BROKEN BEGIN FLAG 31>28");
		getNextRead();
		System.out.println("TLR: IN:"+(((((byte)(changeNr)) >> 3) & 0x1f) ^ 0x10));
		assertNotSame("Na eerste flag geen flag terug", changeNr, Frame.ONES);
		lpt.writeLPT(head);
		int lastNr = head;
		for(int i=0;i<8;i++){
			getNextRead();
			System.out.println("TLR: IN:"+(((((byte)(changeNr)) >> 3) & 0x1f) ^ 0x10));
			if(lastNr!=data[i]){
				lpt.writeLPT(data[i]);
				System.out.println("TLR: OUT:"+data[i]);
			}else{
				lpt.writeLPT(0);
				System.out.println("TLR: OUT: 0");
				getNextRead();
				System.out.println("TLR: IN:"+(((((byte)(changeNr)) >> 3) & 0x1f) ^ 0x10));
				lpt.writeLPT(data[i]);
				System.out.println("TLR: OUT:"+data[i]);
			}
			lastNr=data[i];
		}
		getNextRead();
		System.out.println("TLR: IN:"+(((((byte)(changeNr)) >> 3) & 0x1f) ^ 0x10));
		lpt.writeLPT(31);
		System.out.println("TLR: OUT 31");
    	getNextRead();
    	System.out.println("TLR: IN:"+(((((byte)(changeNr)) >> 3) & 0x1f) ^ 0x10));
    	lpt.writeLPT(0);
    	System.out.println("TLR: OUT 0");
		//-----------
		for(int i=0;i<3;i++){
			pushFrame((byte)1,PAYLOAD_8BYTE);	
		}
		pushFrame((byte)12,PAYLOAD_8BYTE);
		
		System.out.println("TLR:  Verzonden frame");
		getNextRead();
		if(changeNr!=Frame.ONES){
			getNextRead();
			System.out.println(changeNr);
		}
		System.out.println("INC: "+ changeNr+ " = Frames.ONES = "+Frame.ONES);
		assertEquals("readAck: ontvangt geen flag voor ack",Frame.ONES, changeNr);
		sendResponse();
		getNextRead();
		sendResponse();
		assertTrue("Geen ack header ontvangen",changeNr>0);
		getNextRead();
		sendResponse();
		System.out.println("Eerste gedeelte ack: "+Frame.toBinaryString((byte)changeNr));
		assertTrue("Eerste gedeelte ack fout",changeNr==-105);
		getNextRead();
		sendResponse();
		getNextRead();
		sendResponse();
		System.out.println("Eerste gedeelte ack: "+Frame.toBinaryString((byte)changeNr));
		assertTrue("tweede gedeelte ack fout",changeNr==-105);
		System.out.println("TLR: beide ack's ontvangen");
		while(changeNr!=Frame.ONES){
			getNextRead();
			sendResponse();
		}
		//Retransmit + ack of  frame 3
		pushFrame((byte)12,PAYLOAD_8BYTE);
		getNextRead();
		System.out.println("TLR: changeNr: "+changeNr);
		if(changeNr!=Frame.ONES){
			getNextRead();
			System.out.println(changeNr);
		}
		System.out.println("INC: "+ changeNr+ " = Frames.ONES = "+Frame.ONES);
		assertEquals("readAck: ontvangt geen flag voor ack",Frame.ONES, changeNr);
		sendResponse();
		getNextRead();
		sendResponse();
		assertTrue("Geen ack header ontvangen",changeNr>0);
		getNextRead();
		sendResponse();
		System.out.println("Eerste gedeelte ack: "+Frame.toBinaryString((byte)changeNr));
		assertTrue("Eerste gedeelte ack fout",changeNr==-113);
		getNextRead();
		sendResponse();
		getNextRead();
		sendResponse();
		System.out.println("Eerste gedeelte ack: "+Frame.toBinaryString((byte)changeNr));
		assertTrue("tweede gedeelte ack fout",changeNr==-113);
		System.out.println("TLR: beide ack's ontvangen");
		System.out.println("INC: "+ changeNr);
		while(changeNr!=Frame.ONES){
			getNextRead();
			System.out.println("INC: ILOOP "+ changeNr);
			sendResponse();
		}
		
	}
	
//	public void testDetectionTooShortFrame(){
//		pushFrame((byte)12,PAYLOAD_9BYTE);
//		getNextRead();
//		if(changeNr!=Frame.ONES){
//			getNextRead();
//			System.out.println(changeNr);
//		}
//		assertEquals("readAck: ontvangt geen flag voor ack",Frame.ONES, changeNr);
//		sendResponse();
//		getNextRead();
//		sendResponse();
//		assertTrue("Geen ack header ontvangen",changeNr>0);
//		getNextRead();
//
//		sendResponse();
//		System.out.println(Frame.toBinaryString((byte)changeNr));
//		assertEquals("Eerste gedeelte ack fout",changeNr,119);
//		getNextRead();
//		sendResponse();
//		getNextRead();
//		sendResponse();
//		assertEquals("Tweede gedeelte ack fout",changeNr,119);
//		while(changeNr!=Frame.ONES){
//			getNextRead();
//			sendResponse();
//		}
//	}
	
	public void testBrokenBeginFlagBeginFrameSegment(){
		
		//----------- first frame
		byte head = 12;
		byte data [] = PAYLOAD_8BYTE;
		getNextRead();
		System.out.println("New frame ga ik verzenden nu!");
		System.out.println("TLR: IN:"+(((((byte)(changeNr)) >> 3) & 0x1f) ^ 0x10));
		assertNotSame("Bij begin verzenden frame geen 31 op kabel", changeNr, Frame.ONES);
		lpt.writeLPT(30);
		System.out.println("TLR: KAPOT FLAG 30");
		lpt.writeLPT(0);
		System.out.println("TLR: 0 becase flag fail");
		lpt.writeLPT(31);
		System.out.println("TLR: OUT 31");
		getNextRead();
		System.out.println("TLR: IN:"+(((((byte)(changeNr)) >> 3) & 0x1f) ^ 0x10));
		assertNotSame("Na eerste flag geen flag terug", changeNr, Frame.ONES);
		lpt.writeLPT(head);
		int lastNr = head;
		for(int i=0;i<8;i++){
			getNextRead();
			System.out.println("TLR: IN:"+(((((byte)(changeNr)) >> 3) & 0x1f) ^ 0x10));
			if(lastNr!=data[i]){
				lpt.writeLPT(data[i]);
				System.out.println("TLR: OUT:"+data[i]);
			}else{
				lpt.writeLPT(0);
				System.out.println("TLR: OUT: 0");
				getNextRead();
				System.out.println("TLR: IN:"+(((((byte)(changeNr)) >> 3) & 0x1f) ^ 0x10));
				lpt.writeLPT(data[i]);
				System.out.println("TLR: OUT:"+data[i]);
			}
			lastNr=data[i];
		}
		getNextRead();
		System.out.println("TLR: IN:"+(((((byte)(changeNr)) >> 3) & 0x1f) ^ 0x10));
		lpt.writeLPT(31);
		System.out.println("TLR: OUT 31");
    	getNextRead();
    	System.out.println("TLR: IN:"+(((((byte)(changeNr)) >> 3) & 0x1f) ^ 0x10));
    	lpt.writeLPT(0);
    	System.out.println("TLR: OUT 0");
    	
		getNextRead();
		System.out.println(changeNr);
		if(changeNr!=Frame.ONES){
			getNextRead();
			System.out.println(changeNr);
		}
		assertEquals("readAck: ontvangt geen flag voor ack",Frame.ONES, changeNr);
		sendResponse();
		System.out.println("response send");
		getNextRead();
		sendResponse();
		assertTrue("Geen ack header ontvangen",changeNr>0);
		getNextRead();
		sendResponse();
		assertEquals("Eerste gedeelte ack fout",changeNr,-1);
		getNextRead();
		sendResponse();
		assertEquals("tweede gedeelte ack fout",changeNr,-9);
		while(changeNr!=Frame.ONES){
			getNextRead();
			sendResponse();
		}
	}
	
	// NON TEST METHODES
	
	private static void readAck(int a, int b) {
		getNextRead();
		System.out.println("TLR: IN:"+(((((byte)(changeNr)) >> 3) & 0x1f) ^ 0x10));
		if(changeNr!=Frame.ONES){
			getNextRead();
			System.out.println(changeNr);
		}

		assertEquals("readAck: ontvangt geen flag voor ack",Frame.ONES, changeNr);
		sendResponse();
		getNextRead();
		sendResponse();
		System.out.println("TLR: IN:"+(((((byte)(changeNr)) >> 3) & 0x1f) ^ 0x10));
		assertTrue("Geen ack header ontvangen",changeNr>0);
		getNextRead();
		sendResponse();
		System.out.println("TLR: IN:"+changeNr);
		System.out.println("TLR: IN:"+(((((byte)(changeNr)) >> 3) & 0x1f) ^ 0x10));
		assertTrue("Eerste gedeelte ack fout",changeNr==a);
		getNextRead();
		sendResponse();
		if(a==-113){
			getNextRead();
			sendResponse();
		}
		System.out.println("TLR: IN:"+changeNr);
		System.out.println("TLR: IN:"+Frame.toBinaryString((byte)(changeNr)));
		System.out.println("TLR: IN:"+(((((byte)(changeNr)) >> 3) & 0x1f) ^ 0x10));
		assertTrue("tweede gedeelte ack fout",changeNr==b);
		while(changeNr!=Frame.ONES){
			getNextRead();
			sendResponse();
		}
	}

	private static void pushFrame(byte head, byte[] data){
		getNextRead();
		System.out.println("New frame ga ik verzenden nu!");
		System.out.println("TLR: IN:"+(((((byte)(changeNr)) >> 3) & 0x1f) ^ 0x10));
		assertNotSame("Bij begin verzenden frame geen 31 op kabel", changeNr, Frame.ONES);
		lpt.writeLPT(31);
		System.out.println("TLR: OUT 31");
		getNextRead();
		System.out.println("TLR: IN:"+(((((byte)(changeNr)) >> 3) & 0x1f) ^ 0x10));
		assertNotSame("Na eerste flag geen flag terug", changeNr, Frame.ONES);
		lpt.writeLPT(head);
		int lastNr = head;
		for(int i=0;i<8;i++){
			getNextRead();
			System.out.println("TLR: IN:"+(((((byte)(changeNr)) >> 3) & 0x1f) ^ 0x10));
			if(lastNr!=data[i]){
				lpt.writeLPT(data[i]);
				System.out.println("TLR: OUT:"+data[i]);
			}else{
				lpt.writeLPT(0);
				System.out.println("TLR: OUT: 0");
				getNextRead();
				System.out.println("TLR: IN:"+(((((byte)(changeNr)) >> 3) & 0x1f) ^ 0x10));
				lpt.writeLPT(data[i]);
				System.out.println("TLR: OUT:"+data[i]);
			}
			lastNr=data[i];
		}
		getNextRead();
		System.out.println("TLR: IN:"+(((((byte)(changeNr)) >> 3) & 0x1f) ^ 0x10));
		lpt.writeLPT(31);
		System.out.println("TLR: OUT 31");
    	getNextRead();
    	System.out.println("TLR: IN:"+(((((byte)(changeNr)) >> 3) & 0x1f) ^ 0x10));
    	lpt.writeLPT(0);
    	System.out.println("TLR: OUT 0");
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
