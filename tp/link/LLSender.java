package tp.link;

import tp.util.Log;
import lpt.Lpt;

/**
 * This class represents the low-level link layer sender component.
 * There is a tight relationship with the class Frame.
 * LLSender has a data-buffer which will be filled with Frames.
 * The buffer consists of 10 integers: 2 integers (both 31) for flagging, the other 8 integers are the contents of the retreived Frame.

 * There is another array with size 10, which is a boolean-array and keeps track of whether integer i has been sent.
 *
 * The purpose of this class is to send the retreived Frame over the LPT-cable in chuncks of 1 integer each.
 * The first entry in the data-buffer is the flag-pattern (11111), so is the last entry.
 * Furthermore LLSender keeps track of the last sent integer over the cable.
 * If the integer to be sent exactly matches the previously sent integer, the class will send 00000 over the cable, before sending the new integer.

 *
 * Synchronization is handled as follows:
 * The receiver will put a new value on the cable if he is ready to receive a new integer.
 * LLSender continues to read from the cable.
 * Once it reads a change on the cable, it implicitely waits for a couple of CPU-cycles, and sends the new integer.
 *
 * @author Martijn Heuvink
 */
public class LLSender {

    private Lpt cable;
   // private Frame[] frames;
    private int changeNr;
    private int lastNr = -1;
    private boolean alt = false;
    private static final int LL_TIME_OUT = 200;
    
    private boolean sysoutLog = false;
    /**
     * Creates a new low-level linklayer sender component and sets up the buffers.
     */
    public LLSender() {
        cable = new Lpt();
    }

    /**
     * Retreives a frame from a higher layer
     * @return
     */
    public Frame[] getFrames() {
        return null;
    }

    private void getNextRead() {
        int nr;
        while (true) {
            nr = cable.readLPT();
            if (nr != changeNr) {
                microSleep();
                changeNr = cable.readLPT();
                break;
            }
        }
        Log.writeLog(" LLS", "IN: "+(((((byte) changeNr) >> 3) & 0x1f) ^ 0x10), sysoutLog);
    }

    /**
     * This method is to be used to send frames over the cable after the
     * first frame was send successfully and there trough the cable got claimed
     * for the entire TP package.
     */
    public boolean pushFrame(Frame f, boolean flag) {
    	boolean ackIncoming = false;
    	
    	int n = f.next();
    	if(flag){
    		getNextRead();
    		cable.writeLPT(31);
    		Log.writeLog(" LLS", "OUT: 31", sysoutLog);
    		
    		if (changeNr == Frame.ONES) {
    			ackIncoming = true;
    		}
    		getNextRead();
    		if(changeNr == Frame.ONES){
    			ackIncoming = true;
    		}
    		if(ackIncoming){
    			cable.writeLPT(0);
    			return ackIncoming;
    		}
    	} else {
    		getNextRead();
    	}
    	
    	while(n!= -1 && n!=0){
    		if(lastNr!=n){
    			cable.writeLPT(n);
    		}else{
    			cable.writeLPT(0);
    			Log.writeLog(" LLS", "OUT: 0", sysoutLog);
    			getNextRead();
    			cable.writeLPT(n);
    		}
    		Log.writeLog(" LLS", "OUT: " + n, sysoutLog);
    		lastNr = n;
    		n = f.next();
    		getNextRead();
    	}
    	cable.writeLPT(31);
    	Log.writeLog(" LLS", "OUT: 31", sysoutLog);
    	getNextRead();
    	Log.writeLog(" LLS", "OUT: 0", sysoutLog);
		cable.writeLPT(0);
		Log.writeLog(" LLS", "frame sent", sysoutLog);
		return ackIncoming;
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
     * @return false if other side is also trying to send
     * true if the first frame was succesfully send.
     */
    public boolean pushFirstFrame(Frame f) {
        boolean succes = false;
        long timeoutCount = System.currentTimeMillis();
        getNextRead();
    	cable.writeLPT(31);
    	Log.writeLog(" LLS", "OUT: 31", sysoutLog);
     	
    	if (changeNr == Frame.ONES) { //shift(changeNr) == 31
    		Log.writeLog(" LLS", "COLLISION", sysoutLog);
    		 //--------- readNext() + timeout
    		int nr;
            while (true) {
                nr = cable.readLPT();
                if (nr != changeNr) {
                    microSleep();
                    changeNr = cable.readLPT();
                    break;
                }
                if(System.currentTimeMillis()>(timeoutCount+LL_TIME_OUT)){
              	  changeNr = Frame.ONES;
              	  break;
                }
            }
            Log.writeLog(" LLS", "IN: "+(((((byte) changeNr) >> 3) & 0x1f) ^ 0x10), sysoutLog);
            //------------
         	cable.writeLPT(0);
         	Log.writeLog(" LLS", "OUT: 0", sysoutLog);
         }else{
        	 //--------- readNext() + timeout
        	  int nr;
              while (true) {
                  nr = cable.readLPT();
                  if (nr != changeNr) {
                      microSleep();
                      changeNr = cable.readLPT();
                      break;
                  }
                  if(System.currentTimeMillis()>(timeoutCount+LL_TIME_OUT)){
                	  changeNr = Frame.ONES;
                	  break;
                  }
              }
              Log.writeLog(" LLS", "IN: "+(((((byte) changeNr) >> 3) & 0x1f) ^ 0x10), sysoutLog);
              //------------
        	 if (changeNr == Frame.ONES) { //shift(changeNr) == 31
        		 cable.writeLPT(0);
        		 Log.writeLog(" LLS", "OUT: 0", sysoutLog);
        	 } else {
        		 lastNr = f.next();
        		 cable.writeLPT(lastNr);
        		 Log.writeLog(" LLS", "OUT: " + (((((byte) lastNr) >> 3) & 0x1f) ^ 0x10), sysoutLog);
        		 pushFrame(f,false);
        		 succes = true;
          	 }
         }

    /** Implementation idea
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
     * for 'normal sending see the 'pushFrame' implementation **/
        return succes;
    }

	
    private void sendResponse() {
        if (alt) {
        	cable.writeLPT(4);
        } else {
        	cable.writeLPT(10);
        }
        alt=!alt;
    }
    private static void microSleep() {
    	@SuppressWarnings("unused")
		int i = (int) Math.random() * 9;
    	i++;
    }

} 