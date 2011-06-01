package tp.link;

import tp.trans.Segment;


public interface Link {
	public void pushSegment(Segment s);
	public boolean readyToPushSegment();
}
