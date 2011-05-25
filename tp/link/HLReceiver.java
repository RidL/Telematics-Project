package tp.link;

public class HLReceiver extends Thread {

    private static final int WINDOW_SIZE = 8;
    private static final int BUFFER_SIZE = 21;

    private LLReceiver llr;
    private HLSender hls;
    private Frame[] frameBuffer;
    private boolean senderActive;
    private boolean expectingAck;
    
    private byte ack;
    private int errCount;
    private int recPtr;
    private int windowPtr;

    /**
     * Creates a new HLReceiver and initialises it, an HLSender must be set
     * after instantiating this class with setSender(highLevelSender)
     */
    public HLReceiver() {
        llr = new LLReceiver(this);
        frameBuffer = new Frame[BUFFER_SIZE];
        senderActive = false;
        expectingAck = false;
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

            Frame tempFrame = llr.read();
            // !senderActive == receiving || cable_free
            if (!senderActive) {
                interpretFrame(tempFrame);
            } else {
            	// if senderActive
            	System.out.println("HLR: ACK PROCCESSING");
                ackReceived(tempFrame);
            }
        }
    }

    public void setSenderActive(boolean b) {
        senderActive = b;
    }

    public void setExpectingAck() {
        expectingAck = true;
    }

    public void ackReceived(Frame tempFrame) {
        byte ack = tempFrame.getBytes()[1]; //first byte = header.
        System.out.println("HLR: got ack interpreting: " + Frame.toBinaryString(ack));
        hls.ackReceived(ack);
        System.out.println("HLR: SET HLS TO EXPACT ACK");
        llr.setInvalidFrame();
        expectingAck = false;
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
        byte ack = 0;
        boolean[] acks = new boolean[8];
        boolean newWindow = true;
        for(int i = 0; i<WINDOW_SIZE; i++) {
             if(frameBuffer[windowPtr+i] == null) {
                 ack+= Math.pow(2, (WINDOW_SIZE-1)-i);
                 acks[i] = false;
                 newWindow = false;
                 errCount++;
             } else {
                 acks[i] = true;
             }
             if(frameBuffer[windowPtr+i]!=null && frameBuffer[windowPtr+i].isFin()){
                 windowPtr = 0;
                 recPtr = 0;
                 newWindow = false;
                 break;
             }
        }
        if(newWindow) {
            windowPtr += WINDOW_SIZE;
        }
        System.out.println("HLR: newWindow: " + newWindow);
        System.out.println("HLR: ACK: " + Frame.toBinaryString(ack));
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
        // shit interpreten
    	if(errCount>0){
    		for(int bit=0; bit<8; bit++){
    			if((byte)(ack<<bit)<0){
    				frameBuffer[recPtr-WINDOW_SIZE+bit] = tempFrame;
    				ack = (byte)(ack^((byte)Math.pow(2, 7-bit)));
    				break;
    			}
    		}
    		frameBuffer[recPtr] = tempFrame;
    		errCount--;
            
    	}else{
    		frameBuffer[recPtr] = tempFrame;
        	recPtr++;
    	}
        if((tempFrame != null && tempFrame.isFin()) || (recPtr%WINDOW_SIZE == 0)&&(errCount==0)) {   // if sequence of frames is received
            llr.setInvalidFrame();
            sendAck();
        }
        if(recPtr == BUFFER_SIZE) {
            recPtr = 0;
        }
    }

    /**
     * Temp method
     * @param frames
     */
    @Deprecated
    public void tempFill(Frame[] frames) {
        this.frameBuffer = frames;
    }

    public static void main(String[] args) {
        HLReceiver r = new HLReceiver();
        byte[] data = new byte[]{12,4,3,9,0,3,1,2,6,3,2,6,8,0,5,4,3,3,6,7,89,32,2};
        Frame[] frames = new Frame[]{null,null,
            new Frame(data, false, false),new Frame(data, false, false),null,null,
            null,new Frame(data, false, false),new Frame(data, false, false),new Frame(data, false, false),
            new Frame(data, false, false),new Frame(data, false, false),new Frame(data, false, false),new Frame(data, false, false),new Frame(data, false, false),new Frame(data, false, false),null,new Frame(data, false, false),new Frame(data, false, false),new Frame(data, false, false),new Frame(data, false, false),new Frame(data, false, false)};
        r.tempFill(frames);
        r.sendAck();
    }
}

