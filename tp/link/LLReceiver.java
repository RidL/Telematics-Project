package tp.link;

import lpt.Lpt;

public class LLReceiver {

    private Lpt lpt;
    private HLReceiver hlr;
    private boolean alt;
    private boolean frameReceived;
    private int tmp;
    private boolean validFrame;
    private boolean readingFrame;
    private Frame f;
    private byte[] data;
    private int offset;
    private byte header;
    private static final int INITIAL_VALUE = 10;

    public LLReceiver(HLReceiver hlr) {
        this.hlr = hlr;
        lpt = new Lpt();
        alt = true;
        f = null;
        frameReceived = false;
        validFrame = false;
        readingFrame = false;
        data = new byte[7];
        offset = 0;
        header = 0;
        lpt.writeLPT(INITIAL_VALUE);
        tmp = lpt.readLPT(); 
        System.out.println("LLR: Write initial value>: 10");
        int i = ((tmp >> 3) & 0x1f) ^ 0x10;
        System.out.println("LLR: Read initial value: "+i);
    }
 
    public Frame read() {
        f = null;
        frameReceived = false;
        
        
        while(!frameReceived){
        	if(lpt.readLPT() != tmp){
        		for (int z = 0; z < 2; z++) {
                    int y = 3;
                    y++;
                }
        	tmp = lpt.readLPT();
        	int i = ((tmp >> 3) & 0x1f) ^ 0x10;
        	System.out.println("LLR:   ---->: "+i);
        	if(i == 31 && !validFrame && readThisFrame()){
        		validFrame = true;
        	}
        	if(validFrame){
        		sendResponse();
        		bitInterpret(i);
        	}
        	}
        }
        System.out.println("LLR:<!--Frame received--!>");
        return f;
       
    }
    
    private void sendResponse() {
    	   if (alt) {
               lpt.writeLPT(4);
              System.out.println("LLR: <<: 4");
               alt = false;
           } else {
               lpt.writeLPT(10);
              System.out.println("LLR: <<: 10");
               alt = true;
           }
		
	}

	private void bitInterpret(int i) {
		if (i==31 && readingFrame){
			if(offset <= 50){
                System.out.println("LLR: new Frame offset: " + offset + " data: " + data.length);
				f = new Frame(data, header);
			}
			frameReceived=true;
			offset = 0;
			readingFrame = false;
		}else if(i!= 0 && i!=31 && offset <=50){
			if(!readingFrame){
				header = (byte)i;
				readingFrame = true;
			}else{
				Frame.bitConcat(data, (byte)i, offset);
				offset = offset+5;
			}
		}
	}

	public void setInvalidFrame(){
		validFrame = false;
	}
	
	public boolean readThisFrame(){
    	boolean ret = false;
    	if(hlr.inSenderActiveMode()){
    		if(hlr.expectingAck()){
    			ret = true;
    		}else{
    			ret = false;
    		}
    	}else{
    		hlr.setReceivingMode(true);
    		ret = true;
    	}  	
    	return ret;
    }
}

