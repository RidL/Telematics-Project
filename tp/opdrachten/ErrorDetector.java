package tp.opdrachten;

import lpt.Lpt;

/**
 * Reads values from an LPT port, this program expects 11111 and 00000
 * as bit-patterns, it counts the amount of suspected bit flips, and calculates
 * the error rate
 *
 */
public class ErrorDetector {
	
	/**
	 * Indefinitely reads values and tries to count the bit flips
	 * 
	 */
	public static void main(String[] args) throws Throwable{
		Lpt lpt = new Lpt();
		int read = 0;
		int tmp = 0;
		int errorCount=0;
		int total = 0;
		
		while(true){
			read = lpt.readLPT();
			read = ((read>>3))&0x1f;
			int ones = countOnes(read);
			
			if(Math.abs((countOnes(tmp)-ones))>2){
				if(ones>2){
				 	errorCount += 5-ones;
				}else{
					errorCount += ones;
				}
				total +=5;
				System.out.println("Errorpercentage: "+(((double)errorCount)/total)*(100));
				tmp = read;
				Thread.sleep(50);
			}
		}
	}
	
	/**
	 * Counts the amount of 1's in the five least significant bits the parameter
	 * @param a the value of which to count the ones
	 * @return the amount of ones in value a
	 */
	public static int countOnes(int a){
		int count = 0;
		int tmp = a;
		if(tmp<0){
			tmp*=-1;
		}
		for(int i=0;i<5;i++){
			count += tmp%2;
			tmp =(tmp>>1);
		}
		return count;
	}
}
