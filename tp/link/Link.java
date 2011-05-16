package tp.link;

import tp.trans.Segment;


public interface Link {
	public void pushSegment(Segment s);
	public void readyToPushSegment(Segment s);
}
