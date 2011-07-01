package tp.link;

import tp.trans.Segment;
import tp.trans.Trans;
import tp.util.Log;

public class HLReceiver extends Thread {
    private static final int WINDOW_SIZE = 8;
    private static final int BUFFER_SIZE = 24;
    private static final int HL_SLEEP_TIME = 1000;

    private LLReceiver llr;
    private HLSender hls;
    private Frame[] frameBuffer;
    private boolean senderActive;
    private boolean expectingAck;
    
    private long timeoutCount;
    
    private byte ack;
    private int errCount;
    private int recPtr;
    private int windowPtr;
    
    private boolean frameBroken;
    private boolean sysoutLog = false;
    
    /**
     * Creates a new HLReceiver and initialises it, an HLSender must be set
     * after instantiating this class with setSender(highLevelSender)
     */
    public HLReceiver() {
    	Log.writeLog(" HLR", "booted", sysoutLog);
        llr = new LLReceiver(this);
        frameBuffer = new Frame[BUFFER_SIZE];
        senderActive = false;
        expectingAck = false;
        frameBroken = false;
        errCount = 0;
        recPtr = 0;
        windowPtr = 0;
        hls = null;
    }

    /**
     * Sets the High Level Sender of this receiver, must be done directly after
     * creating an HLReceiver object
     * @param hls the sender to be set
     */
    public void setSender(HLSender hls){
    	this.hls = hls;
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
    @Override
    public void run() {
        while (true) {
        	resetTimer();
            Frame tempFrame = llr.read();
            if(tempFrame==null&&timeOut() || (tempFrame != null && tempFrame.isFin() && frameBroken)){
            	llr.setInvalidFrame();
            	Log.writeLog(" HLR", "TIMEOUT @ NULL FRAME", sysoutLog);
                for(int i=windowPtr; i<frameBuffer.length; i++){
                	frameBuffer[i] = null;
                }
            	sendAck();
            	recPtr = recPtr-(recPtr%WINDOW_SIZE);
            	errCount = 0;
            	frameBroken = false;
            }else{
            	
                // !senderActive == receiving || cable_free
                if (!senderActive) {
                	Log.writeLog(" HLR", "Interpreting frame", sysoutLog);
                    interpretFrame(tempFrame);
                } else {
                	// if senderActive
                	Log.writeLog(" HLR", "Interpreting ack", sysoutLog);
                    ackReceived(tempFrame);
                }
            }
        }
    }

    public void setSenderActive(boolean b) {
        senderActive = b;
    }

    public void setExpectingAck() {
        expectingAck = true;
    }
    
    public boolean timeOut(){
    	return System.currentTimeMillis()>(timeoutCount+HL_SLEEP_TIME);
    }
    
    public void resetTimer(){
    	timeoutCount = System.currentTimeMillis();
    }
    
    public void ackReceived(Frame tempFrame) {
    	byte[] ack = new byte[5];
    	for(int i = 0; i<5;i++){
    		ack[i] =  tempFrame.getBytes()[i];
    	}
    	int[] ackScore = new int[5];
    	int	bestAck = 0;
    	for(int i = 1;i<6;i++){
    		for(int y = 0;y<5;y++){
    			if(ack[y]==ack[i]){
    				ackScore[i] += 1;
    			}
    		}
    		
    		if(ackScore[i]>ackScore[bestAck]){
    			bestAck = i;
    		}
    	}

        Log.writeLog(" HLR", "got ack, interpreting" + Frame.toBinaryString(ack), sysoutLog);
        llr.setInvalidFrame();
        expectingAck = false;
        hls.ackReceived(ack[bestAck]);
        
        // ackReceived non-existent, ik gebruik expectingAck
        // frameReceived setten lijkt me niet nodig

        /*
         * Haalt de 'ack byte' uit het huidige tempFrame en roept
         * hls.ackReceived(byte) aan met deze ack byte.
         * Vervolgens zet het ackReceived en frameReceived op false;
         */
    }

    public void sendAck() {
        /*
         * Deze functie wordt aan het einde van serie frames aangeroepen.
         * Hij bepaald welke frames goed zijn ontvangen en beginnend vanaf MSB
         * stopt hij dit in een byte
         *
         * Vervolgens roept hij 'HLSender' aan met 'ackToSend(byte) waaarbij
         * hij de gemaakte byte meegeeft
         */
    	this.ack = 0;
        byte ack = 0;
        boolean newWindow = true;
        boolean hasFin = false;
        for(int i = 0; i<WINDOW_SIZE; i++) {
            if((windowPtr+i>20))
            	break;
        	if((frameBuffer[windowPtr+i] == null)) {
                 ack+= Math.pow(2, (WINDOW_SIZE-1)-i);
                 newWindow = false;
                 if(!hasFin)
                	 errCount++;
                 Log.writeLog(" HLR", "error in window at: " + i + "", sysoutLog);
             }else{
            	 if(frameBuffer[windowPtr+i].isFin()){
                	 hasFin = true;
                 }
             }
        }
        if(newWindow) {
        	Log.writeLog(" HLR", "new window", sysoutLog);
            windowPtr += WINDOW_SIZE;
        }
        Log.writeLog(" HLR", "sending ack: " + Frame.toBinaryString(ack), sysoutLog);
        this.ack = ack;
        hls.ackToSend(ack);
    }
    /**
     * Ik doe niets als true
     * @return
     */
    public boolean inSenderActiveMode() {
        return senderActive;
    }


    /**
     * Checks whether this Receiver is expecting an acknoledgement
     * @return true if an ACK is expected
     */
    public boolean expectingAck(){
    	return expectingAck;
    }
    
    
    /**
     * Wij allebei niets doen, jij je bek houden
     */
    public void setReceivingMode(boolean b) {
        hls.setReceiverActive(b);
    }

    /**
     * Buffers a frame and checks wheter an ACK-Frame must be sent
     * @param tempFrame The Frame to be interpreted and buffered
     */
    private void interpretFrame(Frame tempFrame) {
    	if(errCount>0){
    		Log.writeLog(" HLR", "had errors in last frame, rcving retransmit", sysoutLog);
    		for(int bit=0; bit<8; bit++){
    			if((byte)(ack<<bit)<0){
    				frameBuffer[windowPtr+bit] = tempFrame;
    				Log.writeLog(" HLR", "put retransmit frame at: "+(windowPtr+bit), sysoutLog);
    				ack = (byte)(ack^((byte)Math.pow(2, 7-bit)));
    				break;
    			}
    		}
    		errCount--;
            
    	}else{
    		if(recPtr>=24){
    			Log.writeLog(" HLR", "Buffer overvlow encounterd, setting rcvPtr 8 back", sysoutLog);
    			for(int i = 16; i<24;i++){
    				frameBuffer[i] = null;
    			}
    			recPtr = 16;
    		}
    		Log.writeLog(" HLR", "Frame added to buffer", sysoutLog);
    		frameBuffer[recPtr] = tempFrame;
        	recPtr++;
        	Log.writeLog(" HLR", "recPtr update: "+recPtr, sysoutLog);
    	}
    	// Moet er een ack worden gestuurd?
        if((tempFrame != null && tempFrame.isFin()) || (recPtr%WINDOW_SIZE == 0)&&(errCount==0)) {
        	Log.writeLog(" HLR", "Sending ACK", sysoutLog);
        	llr.setInvalidFrame();
            sendAck();
        }
        // Is dit het laatste frame van segment en geen retransmits meer?
        //TEMP CODE, TEMPFRAME ZOU NIET FIN MOGEN ZIJN NA TIMEOUT
        Log.writeLog(" HLR", "recPtr is: "+recPtr, sysoutLog);
        if(frameBuffer[recPtr-1] != null && frameBuffer[recPtr-1].isFin() && errCount==0) {
        	 Log.writeLog(" HLR", "Final frame encountered, empty'ing bufferz", sysoutLog);
           
            
            
            /**
             * Hier moet nog code komen op stuff naar boven te pushe
             * 
             * Ook moet hier iets komen dat niet volledige buffer (24 size) kan worden gepushed, max 21
             * 
             */
            byte[] buff = new byte[21*8];
            for(int i=0; i<21; i++){
            	for(int k=0; k<8; k++){
            		if(frameBuffer[i]!=null){
            			buff[(i*8)+k] = frameBuffer[i].getBytes()[k];
            		}
            	}
            }
            Trans.getTrans().getRoute().rcvSegment(new Segment(buff));
            
            resetSegment();
            System.out.println("Segment received");
        }
    }

    public void resetSegment(){
    	  frameBuffer = new Frame[BUFFER_SIZE];
          senderActive = false;
          expectingAck = false;
          frameBroken = false;
          errCount = 0;
          recPtr = 0;
          windowPtr = 0;
    }
    
	public void windowBroken() {
		frameBroken = true;	
	}
}

