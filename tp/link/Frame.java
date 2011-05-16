package tp.link;

public class Frame {
	private byte[] data;
	private byte head;
	private int len;

	/**
	 * Creates a new instanc of a Frame object.
	 * @param data the data of this frame
	 * @param ack the ACK header information of this Frame
	 * @param fin the FIN header information of this Frame
	 */
	public Frame(byte[] data, boolean ack, boolean fin){
		this.data = escape(data);
		head = 0;
		if(ack){
			head = -128;
		}
		if(fin){
			head = (byte)(head&-64);
		}
	}
	
	/**
	 * Performs bit stuffing for the byte array b. Flags 00000 and 11111
	 * are escaped to 000010 and 111101 respectively. Note that the size of
	 * the return array could very well be bigger than the original one.
	 * @ensure unescape(escape(b)) == b;
	 * @ensure result.length >= b.length
	 * @param b the array to escape
	 * @return an array that is free of the flags 11111 and 00000
	 */
	public static byte[] escape(byte[] b){
		byte[] eBuff = new byte[7];
		for(int byt = 0; byt<b.length; byt++){
			for(int bit=0; bit<8; bit++){
				
			}
		}
		return null;
	}
	
	/**
	 * Performs the parsing of a byte array b so that the bitcodes that were
	 * escaped are once again back to normal.
	 * @ensure unescape(escape(b)) == b;
	 * @ensure result.length >= b.length
	 * @param b the byte array to unescape
	 * @return the unescaped array
	 */
	public static byte[] unescape(byte[] b){
		return null;
	}
	
	/**
	 * Converts a String to a byte[]
	 * @param s the String to convert
	 * @return a byte array representation of a String
	 */
	public static byte[] toByteArray(String s){
		return null;
	}
	
	/**
	 * Returns the concatenation of start and tail, offset bits into start
	 * @param start the beginning of result up to offset bits into start
	 * @param tail the end part of result
	 * @param offset the amount of bits to read from start
	 * @return a byte[] representing the concatenation of start and tail, offset bits into start
	 */
	public static void bitConcat(byte[] start, byte tail, int offset){
		int index = offset/8;
		int bitStart = offset%8;
		if(bitStart>0){
			byte mask = -1; //11111111
			mask = (byte)(mask<<(8-bitStart));//create mask to zero out old data by shifting in 0-s
			start[index] = (byte)(start[index]&mask); //zero out the old data
			mask = 0; //we need to zero out the extended signbits that may occur when shifting if tail<0
			for(int bit=0; bit<(8-bitStart); bit++){
				mask += Math.pow(2,bit);
			}
			start[index] = (byte)(start[index]|((tail>>bitStart)&mask));//store the first 8-bitStart bits in the buffer
			start[index+1] = 0; //zero out old data NOTE: COMPLETELY ZEROES OUT THE NEXT BYTE!
			start[index+1] = (byte)(tail<<(8-bitStart));//store the remaining bitStart bits in the buffer
		}else{
			start[index] = tail;
		}
		
	}
	
	public static String toBinaryString(byte[] buff){
		String ret = new String();
		for(int i=0; i<buff.length; i++){
			if(i%8==0){
				ret = ret.concat("\n");
			}
			ret = ret.concat(toBinaryString(buff[i]) + " ");
		}
		return ret;
	}
	
	public static String toBinaryString(byte b){
		StringBuilder sb = new StringBuilder("00000000");
	     for (int bit = 0; bit<8; bit++) {
	         if (((b >> bit) & 1) > 0) {
	             sb.setCharAt(7 - bit, '1');
	         }
	     }
	     return sb.toString();
	}
	
	/**
	 * Gives the next five bits of this frame
	 * @return the next five bits of this frame
	 */
	public int getNext(){
		return 1;
	}
	
	public static void main(String[] args){
		byte[] buff = new byte[8];
		buff[0] = -1;
		buff[1] = 0;
		buff[2] = -1;
		buff[3] = -1;
		buff[4] = -1;
		buff[5] = -128;
		buff[6] = -64;
		buff[7] = -1;
		System.out.println(toBinaryString(buff));
		bitConcat(buff, (byte)-64, 12);
		System.out.println(toBinaryString(buff));
	}
}
