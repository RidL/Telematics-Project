package tp.link;

public class LinkInitializer {

	static private HLReceiver hlr;
	static private HLSender hls;
	
	/**
	 * This class is the temporary startup class of the link.
	 * 
	 * It is meant to be replaced by a router program, but since this is not yet finished 
	 * we created this class for building and testing purposes of the Link layer.
	 * 
	 * Once started, it creates a HLReceiver and HLSender and starts both.
	 * @param args
	 */
	public static void main(String[] args) {
		hlr = new HLReceiver(hls);
		hls = new HLSender(hlr);
		hlr.start();
		hls.start();

	}

}
