package tp.trans;

public class Segment {
	private byte[] bytes;
	
	public Segment(byte[] data, byte[] head){
		int i=0;
		bytes = new byte[data.length+head.length];
		for(int h=0; h<head.length; h++,i++){
			bytes[i] = head[h];
		}
		for(int d=0; d<data.length; d++, i++){
			bytes[i] = data[d];
		}
	}
	
	public Segment(byte[] bytes){
		this.bytes = bytes;
	}
	
	public int getPort(){
		return 1;
	}
	
	public int getAddress(){
		return 1;
	}
	
	public byte[] getBytes(){
		return bytes;
	}
}
