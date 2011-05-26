package tp.link;

public interface HLR {
	
	public boolean expectingAck();

	public boolean inSenderActiveMode();

	public void setReceivingMode(boolean b);

}
