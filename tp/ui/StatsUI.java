package tp.ui;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

public class StatsUI extends JPanel {
	
	private JPanel routesPanel;
	
	private JPanel inPanel;
	
	private JPanel outPanel;
	
	public StatsUI(){
		
		
	}
	
	public void buildRouteStats(){
		TitledBorder title;
		title = BorderFactory.createTitledBorder("Routes");
		routesPanel = new JPanel();
		routesPanel.setBorder(title);
		
	}
	
	public void buildInStats(){
		TitledBorder title;
		title = BorderFactory.createTitledBorder("In");
		inPanel = new JPanel();
		inPanel.setBorder(title);
	}
	
	public void buildOutStats(){
		TitledBorder title;
		title = BorderFactory.createTitledBorder("Out");
		outPanel = new JPanel();
		outPanel.setBorder(title);
	}
}
