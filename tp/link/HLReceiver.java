package tp.link;

public class HLReceiver extends Thread {
	
	private static final int WINDOW_SIZE = 8;
	private static final int BUFFER_SIZE = 21;

	private LLReceiver llr;
	private HLSender hls;
	private Frame[] frame_buffer;         
	private boolean senderActive;
	private boolean expectingAck;
	
	public HLReceiver(HLSender hls) {
		llr = new LLReceiver(this);
		this.hls =  hls;
		frame_buffer = new Frame[BUFFER_SIZE];
		senderActive = false;
		expectingAck = false;
		
	}
	
	/**
	 * When the program is started, the cable is in 'cable_free' mode.
	 * This class listens to the cable and waits until it receives a
	 * claim request from the other side of the cable.
	 * 
	 * When this class receives 11111 on the cable and 'senderActive' is false, it switches to 'receiving' mode.
	 * Here it will let the HLSender wait while it starts reading the data. It will
	 * interpret the frames and stuff and order the HLSender to return ack's.
	 * 
	 * If senderActive = true it will check if it is waiting for an ack, and else will do nothing.
	 * 
	 * If however its own HLSender starts sending data, it will switch to 'senderActive' mode.
	 * Here it will only be notified to receive ack's from the other side. After this
	 * it will go back to wait.
	 */
	public void run(){
		while(true){
			
			Frame tempFrame = llr.read();
			
			// !senderActive == receiving || cable_free
			if(!senderActive){
				 	interpretFrame(tempFrame);
			}else{
			// if senderActive
				if(expectingAck){
					ackReceived(tempFrame);
				}
			}
		}
	}
	
	public void setSenderActive(boolean b){
		senderActive = b;
	}

	public void setExpectingAck(){
		expectingAck = true;
	}

	
	public void ackReceived(Frame tempFrame){
		/*
		 * Haalt de 'ack byte' uit het huidige tempFrame en roept 
		 * hlr.ackReceived(byte) aan met deze ack byte.
		 * Vervolgens zet het ackReceived en frameReceived op false;
		 */
	}
	
	public void sendAck(){
		/*
		 * Deze functie wordt aan het einde van serie frames aangeroepen.
		 * Hij bepaald welke frames goed zijn ontvangen en beginnend vanaf MSB
		 * stopt hij dit in een byte
		 * 
		 * Vervolgens roept hij 'HLSender' aan met 'ackToSend(byte) waaarbij 
		 * hij de gemaakte byte meegeeft
		 */
	}
	
	/**
	 * Ik doe niets als true
	 * @return
	 */
	public boolean inSenderActiveMode(){
		return senderActive;
	}
	
	/**
	 * Wij allebei niets doen, jij je bek houden
	 */
	public void setReceivingMode(boolean b){
		hls.setReceiverActive(b);
	}
	
	private void interpretFrame(Frame tempFrame) {
		// TODO Auto-generated method stub
		
	}

}
