package tp.test;

import junit.framework.*;
import lpt.Lpt;
import junit.swingui.TestRunner;

public class TestAll {
	
	public static void main(String[] args) {
		// the TestRunner can also be selected from the package junit.awtui or junit.textui
		Lpt lpt = new Lpt();
		lpt.writeLPT(10);
		TestRunner.run(TestAll.class);
	}
	
	public static Test suite() {
		TestSuite suite = new TestSuite();
		suite.addTestSuite(TestLinkReceiver.class);
		return suite;
	}

}
