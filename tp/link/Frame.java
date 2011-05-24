package tp.link;

import tp.util.ByteBuilder;

public class Frame {
	private byte[] bytes;
	private int index;
	public static final int PAYLOAD_SIZE = 40;
	public static final int ONES = 127;
	public static final int ZEROS = -121;
	/**
	 * Creates a new instance of a Frame object.
	 * @param data the data of this frame
	 * @param ack the ACK header information of this Frame
	 * @param fin the FIN header information of this Frame
	 */
	public Frame(byte[] payload, boolean ack, boolean fin){
		byte[] tmp = escape(payload);
		bytes = new byte[8];
		if(ack){
			bytes[0] = -128;
		}
		if(fin){
			bytes[0] += 64;
		}
		bytes[0] += 8;
		for(int i=1; i<=tmp.length; i++){
			bytes[i] = tmp[i-1];
		}
		index = -1;
	}
	
	public Frame(byte[] data, byte head){
		//this(data, ((byte)(head&-128)==-128), ((byte)(head&64))==64);
        bytes = new byte[8];
        bytes[0] = head;
        byte[] tmp = unescape(data);
        for(int i=1; i<=data.length; i++){
            bytes[i] = tmp[i-1];
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
		int addIndex = 0;
		ByteBuilder build = new ByteBuilder();
		
		for(int byt = 0; byt<b.length; byt++){
			for(int bit=0; bit<8; bit++){
				ByteBuilder.ByteReturn retval;
				int curr = 0;
				
				if((byte)(b[byt]<<bit)<0){
					curr = 1;
				}
				retval = build.add(curr);
				
				if(retval == ByteBuilder.ByteReturn.FULL || retval == ByteBuilder.ByteReturn.CARRY){
					eBuff[addIndex] = build.pop();
					addIndex++;
				}
				if(retval == ByteBuilder.ByteReturn.FLAG || retval == ByteBuilder.ByteReturn.CARRY){
					if(curr==1){
						retval = build.add(0);
					}else{
						retval = build.add(1);
					}
					if(retval == ByteBuilder.ByteReturn.FULL || retval == ByteBuilder.ByteReturn.CARRY){
						eBuff[addIndex] = build.pop();
						addIndex++;
					}
				}
			}
		}
		if(!build.isPopped()){
			eBuff[addIndex] = build.pop();
		}
		return eBuff;
	}
	
	public byte[] getBytes(){
		return bytes;
	}
	
	public boolean isFin(){
		return (byte)(bytes[0]&64)==64;
	}
	
	public boolean isACK(){
		return (byte)(bytes[0]&-128)==-128;
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
		byte[] uBuff = new byte[8];
		int addIndex = 0;
		int carry = 0;
		ByteBuilder build = new ByteBuilder();
		
		for(int byt=0; byt<b.length; byt++){
			for(int bit=carry; bit<8; bit++){
				ByteBuilder.ByteReturn retval;
				int curr = 0;
				
				if((byte)(b[byt]<<bit)<0){
					curr = 1;
				}
				
				carry = 0;
				retval = build.add(curr);
				if(retval==ByteBuilder.ByteReturn.FLAG || retval==ByteBuilder.ByteReturn.CARRY){
					build.flagSeen(curr);
					if(bit==7)
						carry = 1;
					bit++;
				}
				if(retval==ByteBuilder.ByteReturn.FULL || retval==ByteBuilder.ByteReturn.CARRY){
					uBuff[addIndex] = build.pop();
					//System.out.println("UNESCAPE ADDED: " + uBuff[addIndex]);
					addIndex++;
				}
			}
		}
		return uBuff;
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
	 * First gives the head then gives the next five bits of this frame's pay-load
	 * @return the next five bits of this frame
	 */
	public int next(){
		if(index>=56){
			return -1;
		}
		ByteBuilder build = new ByteBuilder();
		build.add(0);
		build.add(0);
		build.add(0);
		int byt = index/8;
		int bit = index %8;
		int ret = 0;
		
		if(index == -1){
			ret = bytes[0]>>3;
			index = 8;
		}else{
			for(int i=0; i<5; i++,bit++){
				//System.out.print("byte:" + byt + " bit:" + bit);
				if(bit==8){
					byt++;
					i--;
					bit = -1; //bit++ still happens, HACKAGE YO
					continue;
				}
				if((byte)(bytes[byt]<<bit)<0){
					//System.out.println(" -- adding 1");
					build.add(1);
				}else{
					//System.out.println(" -- adding 0");
					build.add(0);
				}
			}
			index += 5;
			ret = build.pop();
		}
		
		System.out.println(toBinaryString((byte)(ret&0x1F)));
		return ret&0x1F;
	}
	
	public static void main(String[] args){
		byte[] buff = new byte[5];
		buff[0] = -32;
		buff[1] = -1;
		buff[2] = 0;
		buff[3] = 0;
		buff[4] = 0;
//		System.out.println(toBinaryString(buff));
//		byte[] esc = escape(buff);
//		System.out.println(toBinaryString(esc));
//		System.out.println(toBinaryString(unescape(esc)));
		byte[] tmp = new byte[7];
		byte head;
		Frame f = new Frame(buff, true, true);
		System.out.println(toBinaryString(f.getBytes()));
		
		int it = 0;
		int n = f.next();
		System.out.println((toBinaryString((byte)n)));
		head = (byte)(n<<3);
		n = f.next();
		while(n!=-1){
			bitConcat(tmp, (byte)(n<<3), it);
			it+=5;
			if(it>=51){
				break;
			}
			System.out.println((toBinaryString((byte)n)));
			n = f.next();
		}
		Frame rcv = new Frame(tmp, head);
		System.out.println((toBinaryString(tmp)));
		System.out.println((toBinaryString(rcv.getBytes())));
	}
}
