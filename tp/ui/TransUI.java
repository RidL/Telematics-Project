package tp.ui;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

public class TransUI extends JFrame{
	private static final long serialVersionUID = 1L;
	private JTabbedPane tabs;
	
	public TransUI(){
		tabs = new JTabbedPane();
		tabs.addTab("Connections", new ConnectionsUI());
		tabs.addTab("Local Buffers", new BuffersUI());
		tabs.addTab("Stats", new StatsUI());
		setContentPane(tabs);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("TP Monitoring");
		pack();
		setVisible(true);
	}

	public static void main(String[] args){
		new TransUI();
	}
}
