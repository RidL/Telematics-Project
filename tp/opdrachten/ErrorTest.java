package tp.opdrachten;

import lpt.ErrorLpt;

/**
 * Class that generates bit patterns for the ErrorDetector
 * @author robin
 *
 */
public class ErrorTest {
	/**
	 * Indefinitely generates a series of bit-patters used for
	 * ErrorDetector, a new bit-pattern is put on the line every 50ms
	 */
	public static void main(String[] args){
		ErrorLpt err = new ErrorLpt();
		int one = 1;
		while(true){
			if( one == 1 ){
				err.writeLPT(15);
				one--;
			}else{
				err.writeLPT(16);
				one++;
			}
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
