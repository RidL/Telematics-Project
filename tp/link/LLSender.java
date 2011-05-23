package tp.link;

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
    private int[] frameData;
    private boolean[] sentData;
    private int changeNr;
    private int frameCount = 0;
    private int lastNr = -1;
    private HLSender hls;
    private long time;

    /**
     * Creates a new low-level linklayer sender component and sets up the buffers.
     */
    public LLSender(HLSender hls) {
        this.hls = hls;
       // frames = new Frame[8];
        frameData = new int[11];
        sentData = new boolean[11];
        cable = new Lpt();
        time = System.currentTimeMillis();
       /* fillRand();
        if (pushFirstFrame(frames[0])) {
            for (int i = 1; i < 2000; i++) {
                fillRand();
                pushFrame(frames[0]);
            }
        }*/
    }

    /**
     * Fills the data-buffer with random data
     */
    private void fillRand() {
        frameData[0] = 31;
        sentData[0] = false;
        for (int i = 1; i < frameData.length - 1; i++) {
            frameData[i] = (int) (30 * (Math.random()) + 1);
            sentData[i] = false;
        }
        frameData[10] = 31;
        sentData[10] = false;
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
        System.out.println("LLS: IN: "+(((((byte) changeNr) >> 3) & 0x1f) ^ 0x10));
    }

    /**
     * This method is to be used to send frames over the cable after the
     * first frame was send successfully and there trough the cable got claimed
     * for the entire TP package.
     */
    public void pushFrame(Frame f, boolean flag) {
//        for (int i = 0; i < f.getBytes().length; i++) {
//            if (!sentData[i]) {
//                getNextRead();
//                if (lastNr != f.getBytes()[i]) {
//                	if (f.getBytes()[i] == 0) {	//temp
//                		sendOther(i);			//temp
//                	}							//temp
//                	else {						//temp
//                		sendData(i, f);			//temp
//                    }							//temp
//                    //sendData(i,f);			//by removing temp, uncomment!
//                    System.out.println("LLS: "+f.getBytes()[i] + " = verzonden data");
//                } else {
//                    cable.writeLPT(0);
//                    System.out.println("LLS: lol "+0 + " = verzonden data");
//                    getNextRead();
//                    if (f.getBytes()[i] == 0) {	//temp
//                		sendOther(i);			//temp
//                	}							//temp
//                	else {						//temp
//                		sendData(i, f);			//temp
//                    }							//temp
//                    //sendData(i,f);			//by removing temp, uncomment!
//                    System.out.println("LLS: "+f.getBytes()[i] + " = verzonden data");
//                }
//                if (i == f.getBytes().length - 1) {
//                    frameCount++;
//                    if (frameCount % 10 == 0) {
//                        System.out.println("LLS: "+"frame ended: verzonden frames: " + frameCount);
//                        System.out.println("LLS: "+(double)(System.currentTimeMillis() - time)/frameCount);
//                    }
//                    getNextRead();
//                    cable.writeLPT(31);
//                    System.out.println("LLS: " +31 + " = verzonden data");
//                    for (int j = 0; j < sentData.length; j++) {
//                    	sentData[j] = false; 
//                    }
//                  //  fillRand();
//                }
//            }
//        }
    	System.out.println(Frame.toBinaryString(f.getBytes()));
    	int n = f.next();
    	if(flag){
    		getNextRead();//read first, because we didn't read after last send
    		cable.writeLPT(31);
    	}
    		
    	do{
    		getNextRead();
    		if(lastNr!=n){
    			cable.writeLPT(n);
    		}else{
    			cable.writeLPT(0);
    			getNextRead();
    			cable.writeLPT(n);
    		}
    		System.out.println("LLS: OUT: " + n + "");
    		lastNr = n;
    		n = f.next();
    	}while(n!=-1);
    	cable.writeLPT(31); //just send, don't read next see ^^
    	System.out.println("LLS: ===frame sent===");
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
//        int initNr = cable.readLPT();
//        changeNr = initNr;
//        System.out.println("LLS: "+(((((byte) initNr) >> 3) & 0x1f) ^ 0x10) + " gelezen");
//        if ((((((byte) initNr) >> 3) & 0x1f) ^ 0x10) == 31) {
//            cable.writeLPT(31);
//            System.out.println("LLS: "+"31 geschreven");
//            getNextRead();  //this should be 0
//            cable.writeLPT(0);
//            System.out.println("LLS: "+"0 geschreven");
//            succes = false;
//        } else {
//            cable.writeLPT(31);
//            System.out.println("LLS: "+"31 geschreven");
//            getNextRead();
//            if ((((((byte) changeNr) >> 3) & 0x1f) ^ 0x10) == 31) {
//                cable.writeLPT(0);
//                System.out.println("LLS: "+"0 geschreven");
//                succes = false;
//            } else {
//                //sentData[0] = true;
//                sendData(0, f);
//                System.out.println("LLS: "+(((((byte) f.getBytes()[0]) >> 3) & 0x1f) ^ 0x10) + " geschreven");
//                pushFrame(f);
//                succes = true;
//            }
//        }
        
       
 
        changeNr = cable.readLPT();
        System.out.println("LLS: InitialValue: "+changeNr);
    	System.out.println(Frame.toBinaryString(f.getBytes()));
    	
    	cable.writeLPT(31);
    	System.out.println("LLS: OUT: 31");
     	
    	if ((((((byte) changeNr) >> 3) & 0x1f) ^ 0x10) == 31) {
    		System.out.println("LLS: COLLISION DETECTED");
         	getNextRead();  //this should be 0
         	cable.writeLPT(0);
         	System.out.println("LLS: OUT: 0");
         }else{
        	 getNextRead();
        	 if ((((((byte) changeNr) >> 3) & 0x1f) ^ 0x10) == 31) {
        		 cable.writeLPT(0);
        		 System.out.println("LLS: "+"0 geschreven");
        	 } else {
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

    private void microSleep() {
        int i = (int) Math.random() * 9;
        i++;
    }

    private void sendData(int i, Frame f) {
        cable.writeLPT(f.getBytes()[i]);
        lastNr = f.getBytes()[i];
        sentData[i] = true;
    // System.out.println((((((byte) frameData[i]) >> 3) & 0x1f) ^ 0x10) + " geschreven");
    }
    
    private void sendOther(int i) {
    	cable.writeLPT(30);
        lastNr = 30;
        sentData[i] = true;
    }
} 