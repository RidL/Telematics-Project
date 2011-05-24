package tp.link;

import tp.trans.Segment;

public class HLSender extends Thread {

    final int WINDOW_SIZE = 8;
    final int BUFFER_SIZE = 21;

    private HLReceiver hlr;
    private LLSender lls;
    private Frame[] frame_buffer;

    private int sendPointer;
// Count voor het aaltal frames in de buffer
    private int framesInBuffer;
// byte met ack data, zowel voor sending als receiving mode.
    private byte ack;

// Wordt er op dit moment een segment gebufferd?
    private boolean segmentInBuffer;
// Wacht klasse op dit moment op ack?
    private boolean expectAck;
// Is er al een ack ontvangen?
    private boolean ackReceived;

// Is deze pc op het moment aan het receiven?
    private boolean receiverActive;
// Is er een ack die verzonden moet worden?
    private boolean ackToSend;

    /**
     * Creates a new High Level Sender
     * @param hlr The High Level Receiver with which it cooperates
     * @ensure readyToPushSegment() == true
     */
    public HLSender(HLReceiver hlr) {
        this.hlr = hlr;
        lls = new LLSender(this);
        frame_buffer = new Frame[BUFFER_SIZE];

        sendPointer = 0;
        framesInBuffer = 0;
        ack = 0;

        segmentInBuffer = false;
        expectAck = false;
        ackReceived = false;

        receiverActive = false;
        ackToSend = false;
    }

    /**
     * This class consists of 2 main mode's, namely 'receiving' and
     * 'not_receiving', where the latest is equivalent to 'sending' OR 'cable_free'.
     *
     * The class will start in 'not_receiving' mode. At this moment
     * two things can happen: the HLReceiver receives a frame from the other side
     * after which it will switch to 'receiverActive' mode or this class receives a
     * segment from the TL layer which it will start to send.
     *
     * -> Sending
     * Once it detects the TL layer has called 'pushSegment' it will call
     * pushWindow() which will start sending the first 'WINDOW_SIZE' frames.
     * After this it will switch to 'expectAck' mode and stop sending until it
     * receives an ack (mode 'ackreceived). When this happends it will call
     * 'retransmitWindow()' which will determine what has been delivered correctly.
     *
     * If it needs to retransmit data, it will send this data wait for an ack so this
     * process can start again. If all data was delivered correctly, it will update the
     * sendPointer and return, after which 'pushWindow() will be called to send the
     * next window.
     *
     * If all frames of a segment have been send correctly (sendPointer + confirmed in
     * ack = framesInBuffer) the segment will be deleted so a new segment can be send
     * and the class will return to 'cable_free' mode.
     *
     * -> Receiving
     * If however the HLReceiver receives a frame from the other side it will switch this
     * class to 'receiverActive' mode. In this mode this class will only check if it needs to
     * send a ack for the HLReceiver. If this is the case it will send this ack and
     * otherwise do nothing, even if there is a segment in the buffer.
     *
     * This is the reason the HLreceiver can only receive a frame in cable free mode, because
     * the other side is not allowed to send until the entire frames was sent correct.
     */
    @Override
    public void run() {
        while (true) {
// !receiverActive == sending || cable_free
            if (!receiverActive) {
                if (ackReceived) {
                    retransmitWindow();
                } else if (segmentInBuffer && !expectAck) {
                	System.out.println("HLS: pushWindow");
                    pushWindow();
                }
            } else {
// if receiverActive
                if (ackToSend) {
                    sendAck();
                }
            }
        }
    }

    /**
     * Deze methode geeft aan of een Transport layer een segment kan pushen
     *
     * De TL implementatie zal deze functie moeten aanroepen en om te bepalen
     * of het een frame kan aanleveren, dit kan namelijk slechts als er op
     * het moment dat er geen segment door deze klasse wordt gebufferd
     *
     * @return true als buffer leeg is, false als deze vol is.
     */
    public boolean readyToPushSegment() {
        return !segmentInBuffer;
    }

    /**
     * Pushes one transport-layer segment into this High Level Sender, should be
     * called from the transport-layer
     * @param s The segment to be pushed in the buffer
     */
    public void pushSegment(Segment s) {
        /*
         * Deel segment op in frames en vul 'frame_buffer'.
         * Geeft het aantal frames aan in 'framesInBuffer'.
         *
         * Denk overna: Als er een segment in buffer is,
         * wil je deze dan toch overschrijven?
         */
        int segPtr = 0; // indicating what data to push next in the incoming segment
        int ptr = 0;    // where to put the next data into the buffer
        byte[] bytes = s.getBytes();    // assuming this method exists

        boolean isFin = false;
        while(segPtr < bytes.length) {
            byte[] newBytes = new byte[5];
            int i;
            for(i = 0; i < newBytes.length && segPtr < bytes.length; i++) {
                newBytes[i] = bytes[segPtr];
                System.out.printf("HLS: bytePtr: %d & i: %d\n", segPtr, i);
                segPtr++;
            }
            if((segPtr)%5 != 0) {  // if stuffing must occur
                newBytes[i] = 10;
                if((segPtr)%5 <= 3)    // 2 or more bytes stuffing
                    newBytes[i+1] = 4;
                if((segPtr)%5 <= 2)    // 3 or 4 bytes stuffing
                    newBytes[i+2] = 10;
                if((segPtr)%5 == 1)    // 4 bytes stuffing
                    newBytes[i+3] = 4;
            }
            if(segPtr == bytes.length) {  // end of tl-packet
                isFin = true;
            }
            Frame frame = new Frame(newBytes, false, isFin);
            frame_buffer[ptr] = frame;
            framesInBuffer++;
            ptr++;
        }
        segmentInBuffer = true;
    }

    /*
     * Temp method for testing only!
     */
       public void pushSegment(byte[] bytes) {
        /*
         * Deel segment op in frames en vul 'frame_buffer'.
         * Geeft het aantal frames aan in 'framesInBuffer'.
         *
         * Denk overna: Als er een segment in buffer is,
         * wil je deze dan toch overschrijven?
         */
        
        int segPtr = 0; // voor in het binnengekomen segment
        int ptr = 0;    // voor waar in de buffer data wordt gepompt
        //byte[] bytes = s.getByteArray();

        boolean isFin = false;
        while(segPtr < bytes.length) {
            byte[] newBytes = new byte[5];
            int i;
            for(i = 0; i < newBytes.length && segPtr < bytes.length; i++) {
                newBytes[i] = bytes[segPtr];
                //System.out.printf("bytePtr: %d & i: %d\n", segPtr, i);
                segPtr++;
            }
            if((segPtr)%5 != 0) {  // if stuffing must occur
                newBytes[i] = 10;
                if((segPtr)%5 <= 3)    // 2 or more bytes stuffing
                    newBytes[i+1] = 4;
                if((segPtr)%5 <= 2)    // 3 or 4 bytes stuffing
                    newBytes[i+2] = 10;
                if((segPtr)%5 == 1)    // 4 bytes stuffing
                    newBytes[i+3] = 4;
            }
            if(segPtr == bytes.length) {  // end of tl-packet
                isFin = true;
            }
            Frame frame = new Frame(newBytes, false, isFin);
            frame_buffer[ptr] = frame;
            framesInBuffer++;
            ptr++;
            
        }
        segmentInBuffer = true;
    }

    /**
     * Pushes WINDOW_SIZE frames one at a time to the Low Level Sender
     */
    public void pushWindow() {
    	int i = sendPointer;	// for do-while loop
        if(sendPointer == 0) {  // if the first frame is encountered
        	i = sendPointer+1;
            hlr.setSenderActive(true);
            boolean succ = lls.pushFirstFrame(frame_buffer[sendPointer]);
            if(!succ) {
                hlr.setSenderActive(false);
                try {
                    Thread.sleep((long) (Math.random()*5));
                }
                catch (InterruptedException ex) {
                    System.err.println("Thread sleep failed");
                }
                return;
            }
        }
        // pushing the rest of the WINDOW_SIZE frames
        
        do {
        	 lls.pushFrame(frame_buffer[i], true);
            i++;
        }
        while(i%WINDOW_SIZE != 0);

        System.out.println("HLS: \n--Window pushed--\n");
        expectAck = true;
        hlr.setExpectingAck();

        /*
         * Begint op 'sendPointer' en stuur stuk voor stuk 'WINDOW_SIZE' frames naar 'lls'.
         *
         * Als 'framesInBuffer' - 'sendPointer' kleiner is dan 'WINDOW_SIZE worden
         * slechts het aantal dat de berekening oplevert gestuurd.
         *
         * Als het eerste frame wordt verstuurd (dus bij 'sendPointer = 0 en frame 1)
         * moet gebruikt worden gemaakt van 'lls.pushFirstFrame(frame)'. Deze methode
         * returned een boolean. Bij een false blijkt de andere kant ook probeert
         * data te verzenden en is frame niet goed verzonden. Er moet dan sleep van random
         * miliseconde worden aangeroepen en hierna moet de functie moet worden returned.
         *
         * Als de andere kant in de tussentijd is begonnen met senden dan is 'receiverActive' op
         * true gezet en wordt er niet weer pushWindow() aangeroepen. Zodra 1 van de kanten
         * succesvol het eerste frame verstuurd is de kabel dus geclaimed.
         *
         * Ook moet bij het eerste frame 'hlr.setSenderActive(true)' worden aangeroepen zodat de
         * HLReceiver weet dat als er een frame binnenkomt hij dit ignored (wordt al
         * afgehandeld door 'pushFirstFrame' methode). Als 'pushFirstFrame' false returned
         * moet er ook 'hlr.setSending(false)' worden aangeroepen voordat de random sleep
         * gebeurdt zodat de andere kant wel kabel kan claimen in de tussentijd.
         *
         * Als er wel succesvol data is overgestuurd wordt LReceiver geinformeerd dat hij een
         * ack moet verwachten doormiddel van 'hlr.setExpectingAck()' aan te roepen. Tenslotte
         * geeft hij aan dat ack wordt verwacht en returned. Het past 'sendPointer'
         * NIET aan! Dat doet retransmitWindow
         *
         */
    }

    /**
     * Sets this HLSender to receiving mode or non-receiving mode
     * @param b true for receiving mode, false for non-receiving mode
     */
    public void setReceiverActive(boolean b) {
        receiverActive = b;
    }

    /**
     * Indicates that an ACK has been received, the ack-bits should be put in b
     * @param b the byte (8 bits) indicating which of the 8 frames are correctly received
     */
    public void ackReceived(byte b) {
        ackReceived = true;
        ack = b;  
    }

    /**
     * Checks whether retransmission is needed for the window just sent and
     * retransmits the incorrectly received frames
     */
    public void retransmitWindow() {
    	System.out.println("in rtrwindow");
        boolean retrans = false;
        
        ackReceived = false;
        expectAck = false;  
        
        int i;
        for(i = 0; i < WINDOW_SIZE; i++) {
            if((byte)(ack << i) < 0) {
                retrans = true;
                //Frame temp = frame_buffer[sendPointer+i];
                if(frame_buffer[(sendPointer+i)].isFin()) {
                    i++;
                    break;
                }
                lls.pushFrame(frame_buffer[sendPointer+i], true);
            }
        }
        if(!retrans) {
        	System.out.println("\nHLR: No retransmit, sendPointer updated \n");
            if(frame_buffer[sendPointer+i].isFin()) {
                sendPointer = 0;
                framesInBuffer = 0;
                segmentInBuffer = false;
            }
            else {
                sendPointer += i;
            }
        } else {
            System.out.println("\nHLR:--Retransmitting--\n");
            expectAck = true;
            hlr.setSenderActive(false);
            hlr.setExpectingAck();
        }


        /*
         * Deze functie bepaald of en welke frames er retransmit moeten worden
         * nadat er een ack is received (data is te vinden in ack) en zal hierna
         * deze frames naar de LLSender sturen. Als hij stuff retransmit zal
         * hij 'l' op true zetten, HLReceiver informeren dat hij een
         * ack moet verwachten door 'hlr.setExpectingAck()' aan te roepen en
         * daarna returnen.
         *
         * Als ALLE frames goed zijn overgekomen (volgens byte) zal 'expectAck' echter false
         * blijven en zal 'sendPointer' worden aangepast aangezien het nu het
         * volledige window goed is ontvangen en de volgende window kan worden verstuurd.
         *
         * Als 'framesInBuffer' - 'sendPointer' kleiner is dan 'WINDOW_SIZE worden
         * slechts het aantal dat de berekening oplevert gecontroleerd op correct
         * aangekomen. Als in dit geval alle frames goed zijn overkomen is het
         * volledige segment goedaangekomen en wordt ook 'segmentInBuffer' op false gezet
         * en moet ook 'sendPointer' en 'framesInBuffer' op 0 worden gezet.
         *
         * Ook moet 'hlr.setSending(false)' worden aangeroepen zodat de receiver weer
         * een frame van de andere kant kan ontvangen.
         *
         */
    }
    
    public void pushFrame(Frame f){
    	lls.pushFirstFrame(f);
    }
    
    /**
     * Informs this class that a certain ack needs to be send.
     *
     * The run loop of this class will detect this method was called
     * and start sending the ack.
     *
     * @param b the ack with retransmit data.
     */
    public void ackToSend(byte b) {
        ackToSend = true;
        ack = b;
    }
    
    /**
     *
     */
    public void sendAck() {
        /*
         * Maakt een ack frame met behulp van byte 'ack', verstuurd deze ack,
         * zet ackToSend op false en informeer HLReceiver.
         */
    	System.out.println("HLS:  Sending ack");
        byte[] ackData = {ack,10,4,10,4};
        Frame ackFrame = new Frame(ackData, true, false);
        ackToSend = false;
        lls.pushFrame(ackFrame, true);
    }


}

