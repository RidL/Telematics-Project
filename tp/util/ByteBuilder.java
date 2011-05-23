package tp.util;

public class ByteBuilder {
	public enum ByteReturn{
		FULL, FLAG, CARRY, CONT
	}
	
	private byte data;
	private boolean popped;
	private int index;
	private int zeros;
	private int ones;
	
	public ByteBuilder(){
		data  = 0;
		index = 0;
		zeros = 0;
		ones = 0;
		popped = true;
	}
	
	public void reset(){
		data = 0;
		index = 0;
		zeros = 0;
		ones = 0;
	}
	
	public ByteReturn add(int bit){
		ByteReturn ret = ByteReturn.CONT;
		popped = false;
		if(bit==1){
			ones++;
			zeros=0;
			data += Math.pow(2, 7-index);
			index++;
			if(ones==4){
				ret = ByteReturn.FLAG;
			}
		}else{
			ones = 0;
			zeros++;
			index++;
			if(zeros==4){
				ret = ByteReturn.FLAG;
			}
		}
		
		if(index==8){
			if(ret==ByteReturn.FLAG){
				ret = ByteReturn.CARRY;
			}else{
				ret = ByteReturn.FULL;
			}
		}
		return ret;
	}
	
	public byte pop(){
		byte ret = data;
		data = 0;
		index = 0;
		popped = true;
		return ret;
	}
	
	public boolean isPopped(){
		return popped;
	}
	
	public void flagSeen(int curr){
		if(curr == 1){
			zeros = 1;
			ones = 0;
		}else{
			zeros = 0;
			ones = 1;
		}
	}
	
	/*public static void main(String[] args){
		ByteBuilder b = new ByteBuilder();
		b.add(1);
		b.add(1);
		b.add(0);
		b.add(0);
		b.add(0);
		b.add(0);
		b.add(0);
		b.add(0);
		System.out.println(Frame.toBinaryString(b.pop()));
	}
	*/
}
