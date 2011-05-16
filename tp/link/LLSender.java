package tp.link;

import lpt.Lpt;

public class LLSender {
	
/**
 * This class supports HLSender's roll of sending a TP segment over the cable
 * 
 * The HLSender will call one of it functions to send a frame at the time over 
 * the cable. This class will send the entire frame over the cable and includes
 * flags and 5x0's bitstuffing.
 */


	 private Lpt lpt;
	 private HLSender hls;
    
	public LLSender(HLSender hls) {
		lpt = new Lpt();
		this.hls = hls;
	}
	
	/**
	 * This method is to be used to send frames over the cable after the
	 * first frame was send successfully and there trough the cable got claimed 
	 * for the entire TP package.
	 */
	public void pushFrame(Frame frame){
	
		/*
		 * Implementation idea
		 * 
		 * As normal, start with flags, stuff the 5x0's between 2 times the same 
		 * bit row.  The normal bitstuffs have been done when the frame was  created.
		 */
	}
	
	/**
	 * This function is meant to be used when the Sender try's to send the
	 * first frame of a TP segment over the cable.
	 * 
	 * This function detects if the other side of the cable is also try'ing 
	 * to send data over the cable if this method gets used on both side's 
	 * on the cable. If the other side is not trying to send data it sends
	 * the entire first frame then returns true.
	 * 
	 * This method only returns AFTER it receives a response to it's last 5 bits.
	 * 
	 * @return 	false if other side is also trying to  send
	 * 			true if the first frame was succesfully send.
	 */
	public boolean pushFirstFrame(Frame frame){
		boolean succes = false;
		
		/*
		 * Implementation  idea
		 * 
		 * First check if the cable reads '11111'. This means that between
		 * letting the HLReceiver sleep and getting here the other side 
		 * started to send. In this case send '11111' back so the other side
		 * knows you are also trying to send. The other side will response 
		 * with '00000' and after you also put '00000' on the cable and return
		 * with false. These last 2 operations are needed so that the next try 
		 * there wont still stand '11111' on the cable.
		 * 
		 * If the cable does not read '11111', start sending '11111' over the
		 * cable and wait at a response. If this response is also '11111' the 
		 * other side is also trying to send. Send '00000' as a response
		 * and return with false.
		 * 
		 * Finaly, if the response is not '11111' then send the rest of the 
		 * frame without checking what the response is (normal sending) and 
		 * return with true AFTER you get a response for last 5 bits.
		 * 
		 * for 'normal sending see the ' pushFrame' implementation
		 */
		
		return succes;
	}

}
