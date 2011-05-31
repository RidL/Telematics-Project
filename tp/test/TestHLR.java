package tp.test;

import tp.link.*;

public class TestHLR extends Thread implements HLR{

	private LLReceiver llr;
	private boolean senderActive;
	private boolean expectingAck;
	private boolean receivedFrame;
	private Frame rcvdFrame;
	
	public TestHLR(){
        senderActive = false;
        expectingAck = false;
        receivedFrame = false;
	}

	public boolean expectingAck() {
		return expectingAck;
	}

    public void setExpectingAck() {
        expectingAck = true;
    }
    
	public boolean inSenderActiveMode() {
		return senderActive;
	}
	
    public void setSenderActive(boolean b) {
        senderActive = b;
    }

	public void setReceivingMode(boolean b) {
		//Kan nooit gebeuren
		System.out.println("THLR: ERROR: hlr claimed kabel");
	}
	
	public boolean receivedFrame(){
		return receivedFrame;
	}
	
	public Frame retriveFrame(){
		receivedFrame = false;
		return rcvdFrame;
	}
	
	public void run(){
		while(true){
			if(!receivedFrame){
				System.out.println("THLR: Reading new frame");
				rcvdFrame = llr.read();
				llr.setInvalidFrame();
				System.out.println("THLR: Frame read");
				receivedFrame= true;
				expectingAck = false;
			}
		}
	}
	
	
}
