package tp.ui;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

public class StatsUI extends JPanel {
	private static final long serialVersionUID = 1L;

	private JPanel routesPanel;
	
	private JPanel inPanel;
	
	private JPanel outPanel;
	
	public StatsUI(){
		buildRouteStats();
		buildInStats();
		buildOutStats();
	}
	
	public void buildRouteStats(){
		TitledBorder title;
		title = BorderFactory.createTitledBorder("Routes");
		routesPanel = new JPanel();
		routesPanel.setBorder(title);
		add(routesPanel);
	}
	
	public void buildInStats(){
		TitledBorder title;
		title = BorderFactory.createTitledBorder("In");
		inPanel = new JPanel();
		inPanel.setBorder(title);
		add(inPanel);
	}
	
	public void buildOutStats(){
		TitledBorder title;
		title = BorderFactory.createTitledBorder("Out");
		outPanel = new JPanel();
		outPanel.setBorder(title);
		add(outPanel);
	}
}
