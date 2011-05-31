package tp.test;

import tp.link.Frame;
import junit.framework.TestCase;

public class TestFrame extends TestCase {
	public Frame f;
	
	public void testEscapeFrame(){
		f = new Frame(new byte[5], false, false);
		assertFalse("Ack bit set",f.isACK());
		assertFalse("Fin bit set",f.isFin());
		assertEquals("Header not correct",1, f.next());
		int r = f.next();
		do{
			assertEquals("Error in payload!", 1, r);
			r = f.next();
		}while(r!=0 && r!=-1);
	}
	
	public void testUnescapeFrame(){
		f = new Frame(new byte[5], false, false);
		byte[] data = new byte[7];
		
		for(int i=0; i<7; i++){
			data[i] = f.getBytes()[i+1];
		}
		data[6]+=56;//sim the 3 1s that the lpt.readLPT() generates in this particular case
		System.out.println(Frame.toBinaryString(data));
		f = new Frame(data,(byte)0);
		System.out.println(Frame.toBinaryString(f.getBytes()));
		for(int i=1; i<f.getBytes().length; i++){
			assertEquals("Failed at unescaping on byte: "+i,0,f.getBytes()[i]);
		}
	}
}
