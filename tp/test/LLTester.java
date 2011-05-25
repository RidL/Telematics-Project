package tp.test;

import lpt.Lpt;

public class LLTester {

	private static Lpt lpt = new Lpt();
	private static int changeNr = -1;
	
	private static void getNextRead() {
        int nr;
        while (true) {
            nr = lpt.readLPT();
           // System.out.println("LLS: GNR: " +(((((byte) nr) >> 3) & 0x1f) ^ 0x10));
            if (nr != changeNr) {
                microSleep();
                changeNr = lpt.readLPT();
                break;
            }
        }
        //System.out.println("LLS: IN: "+(((((byte) changeNr) >> 3) & 0x1f) ^ 0x10));
    }
	
	private static void microSleep() {
        @SuppressWarnings("unused")
		int i = (int) Math.random() * 9;
        i++;
    }

	private static void testFrameTransmit() {
		byte head = 3;
		boolean alt = true;
		
		lpt.writeLPT(10);
		System.out.println("LLTEST: OUT 10");
		lpt.writeLPT(31);
		System.out.println("LLTEST: OUT 31");
		getNextRead();
		lpt.writeLPT(head);
		
		System.out.println("LLTEST: OUT: "+head);
		getNextRead();
		for(int i = 0; i<8 ;i++){
			if(alt){
				lpt.writeLPT(10);
				System.out.println("LLTEST: OUT: 10");
			}else{
				lpt.writeLPT(4);
				System.out.println("LLTEST: OUT: 4");
			}
			alt = !alt;
			getNextRead();
		}
		lpt.writeLPT(31);
		System.out.println("Done test;");
	}
	
	public static void main(String[] args){
		testFrameTransmit();
	}
}
