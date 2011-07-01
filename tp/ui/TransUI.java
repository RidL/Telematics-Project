package tp.ui;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

import tp.trans.Route;
import tp.trans.Trans;
import tp.util.Log;

public class TransUI extends JFrame{
	private static final long serialVersionUID = 1L;
	private JTabbedPane tabs;
	Log l = Log.getInstance("TP");
	private ConnectionsUI conui;
	private BuffersUI bufui;
	private StatsUI statui;
	
	public TransUI(){
		tabs = new JTabbedPane();
		conui = new ConnectionsUI(this);
		bufui = new BuffersUI();
		statui = new StatsUI();
		tabs.addTab("Connections", conui);
		tabs.addTab("Local Buffers", bufui);
		tabs.addTab("Stats", statui);
		setContentPane(tabs);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("TP Monitoring");
		pack();
		setVisible(true);
	}
	
	public void addObservers(){
		Route r = Trans.getTrans().getRoute();
		r.addObserver(conui);
		//r.addObserver(bufui);
		//r.addObserver(statui);
	}
	
	public static void main(String[] args){
	    try {
	        UIManager.setLookAndFeel(
	            UIManager.getSystemLookAndFeelClassName());
	    } 
	    catch (Exception e) {}
		try{
			new TransUI();
		}catch(Exception e){System.err.println("Uncaught Exception");}
	}
}
