package tp.link;

import lpt.Lpt;

public class LLReceiver {
	
	/**
	 * This class does nothing else then read the cable and create frames from what it reads.
	 * 
	 * In the constructor it already starts the 'read()' function which will read, forever!
	 */
	
	
	private Lpt lpt;
	private HLReceiver hlr;
	private static final int INITIAL_VALUE = 10;

	public LLReceiver(HLReceiver hlr) {
		this.hlr = hlr;
		lpt = new Lpt();
		lpt.writeLPT(INITIAL_VALUE);
	}

	/**
	 * This method reads the data of the cable and creates frames when it 
	 * reads relevent data on relevent moments. This method only starts
	 * with interpreting the data when it reads the flag '11111'.
	 * 
	 * When it reads '11111' it will check with the HLReceiver if it is
	 * in senderActive mode or not. If it is in senderActive mode it will 
	 * keep ignoring data until the next flag.
	 * 
	 * It it is not in senderActive mode however, it will put the
	 * HLSender into 'receiving' mode. Next it will continue to read
	 * the data and start building a frame and return it
	 * to the HLReceiver when it encounters the end of a frame. It can
	 * also push an error above when it receives a frame that is too big.
	 * 
	 * It will also remove all flags, 5x0's and remove bitstuffs before it
	 * puts all the data in a frame.
	 */
	public Frame read(){
		Frame f = null;
		
			
			/*
			 * Implementatie idee:
			 * 
			 * Als er een flag ('11111') wordt gelezen, word de methode
			 * 'isSenderActiveMode()' van HLReceiver aangeroepen, welke true
			 * returned als de HLReceiver in senderActive mode is, anders false.
			 * 
			 * Als het true is lees je niks tot volgende flag.
			 * 
			 * Als het false is, roep je de methode 'setReceivingMode(true)' aan
			 * van HLReceiver om de HLSender in receiving mode te stoppen en 
			 * bouw je een frame op en returned aan het einde van dit frame.
			 */
		return f;
	}
}
