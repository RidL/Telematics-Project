package tp.opdrachten;

import java.util.Scanner;

import lpt.Lpt;

/**
 * Class that sends data over the LPT
 */
public class Transmit {
	
	/**
	 * Indefinitely reads a value from System.in and if the value is between 0 
	 * and 64 it gets written on the LPT.
	 * 
	 */
	public static void main(String[] args){
		boolean cont = true;
		Scanner scan = new Scanner(System.in);
		String read;
		Lpt lpt = new Lpt();
		
		while(cont){
			System.out.println("Next value to send: ");
			read = scan.next();
			try{
				int in = Integer.parseInt(read);
				System.out.println(Integer.toBinaryString(in));
				if(in >=0 && in<64){
					lpt.writeLPT(in);
				}
			}catch(NumberFormatException e){
				cont = false;
			}
		}
	}
}
