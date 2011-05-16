package tp.opdrachten;

import lpt.Lpt;

/**
 * Class that prints the first value from the LPT and then prints it whenever it changes.
 * It might be the case that the a value is printed more than once per Transmiter send,
 * if the value changes during a read, there may be more than one read per writeLPT(). The
 * value received is interpreted as 0>=value < 32
 *
 */
public class Receiver {
	/**
	 * Indefinitely read a new value from the LPT 
	 * 
	 */
	public static void main(String[] args){
		Lpt lpt = new Lpt();
		
		int read = 1;
		int tmp = -1;
		
		while(true){
			read = lpt.readLPT();
			read = ((read>>3)&0x1f)^0x10;
			if(tmp!=read){
				System.out.println("val: " + read);
			}
			tmp = read;
		}
	}
}
