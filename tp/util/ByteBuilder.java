package tp.util;

import tp.link.*;

public class ByteBuilder {
	public enum ByteReturn{
		FULL, OVERFLOW, CONT
	}
	
	private byte data;
	
	private int index;
	private int zeros;
	private int ones;
	private int carry;
	
	public ByteBuilder(){
		data  = 0;
		index = 0;
		zeros = 0;
		ones = 0;
		carry = -1;
	}
	
	public void reset(){
		data = 0;
		index = 0;
		zeros = 0;
		ones = 0;
	}
	
	public ByteReturn add(int bit){
		ByteReturn ret = ByteReturn.CONT;
		if(carry>=0){
			int tmp = carry;
			carry = -1;
			add(tmp);
		}
		if(bit == 1){
			zeros = 0;
			ones++;
			data += Math.pow(2, 7-index);
			System.out.println(Frame.toBinaryString(data));
			index++;
			if(ones==4){//need escape
				System.out.println("=====FLAG 1 DETECTED=====");
				ones = 0;
				zeros = 1;
				index++;
				if(index>8){
					index = 1;
					ret = ByteReturn.OVERFLOW;
				}
			}
			if(index == 8){
				index = 0;
				ret = ByteReturn.FULL;
			}
		}else{
			zeros++;
			ones = 0;
			System.out.println(Frame.toBinaryString(data));
			index++;
			if(zeros==4){
				System.out.println("=====FLAG 0 DETECTED=====");
				ones = 1;
				zeros = 0;
				data += Math.pow(2, 7-index);
				index++;
				if(index>8){
					carry = 1;
					ret = ByteReturn.OVERFLOW;
				}
			}
			if(index == 8){
				index = 0;
				ret = ByteReturn.FULL;
			}
		}
		return ret;
	}
	
	public byte pop(){
		byte ret = data;
		data = 0;
		carry = -1;
		return ret;
	}
	
	public static void main(String[] args){
		ByteBuilder b = new ByteBuilder();
		byte[] buff = new byte[7];
		
		int offset = 0;
		for(int i=0; i<40; i++){
			System.out.println("puts");
			ByteReturn retval = b.add(0);
			if(retval!=ByteReturn.CONT){
				Frame.bitConcat(buff, b.pop(), offset);
				System.out.println(Frame.toBinaryString(buff));
				offset += 8;
				if(retval == ByteReturn.OVERFLOW){
					System.out.println("overflowage");
				}
			}
		}
		Frame.bitConcat(buff, b.pop(), offset);
		System.out.println(Frame.toBinaryString(buff));
	}
}
