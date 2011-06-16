package tp.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

public class ConnectionsUI extends JPanel {
	private static final long serialVersionUID = 1L;
	private JPanel networkPanel;
	private JTextField networkAddress;
	private JButton networkConfirm;
	
	private JPanel routePanel;
	private JPanel newPane;
	private RouteComponent titlePanel;
	private JButton newRoute;
	private ArrayList<RouteComponent> routes;
	
	public ConnectionsUI(){
		TitledBorder networkBorder;
		networkBorder = BorderFactory.createTitledBorder("Local");
		networkPanel = new JPanel();
		networkPanel.setBorder(networkBorder);
		networkAddress = new JTextField(5);
		networkConfirm = new JButton("Start");
		networkPanel.add(networkAddress);
		networkPanel.add(networkConfirm);
		
		
		routes = new ArrayList<RouteComponent>();
		routes.add(new RouteComponent(""));
		newPane = new JPanel();
		newRoute = new JButton("New");
		newPane.add(newRoute);
		titlePanel = new RouteComponent();
		buildRoutes();
		
		setLayout(new BorderLayout());
		this.add(networkPanel, BorderLayout.NORTH);
		this.add(routePanel, BorderLayout.CENTER);
	}
	
	public void buildRoutes(){
		routePanel = new JPanel();
		routePanel.setLayout(new GridLayout(routes.size()+2,1));
		routePanel.add(titlePanel);
		TitledBorder routeBorder;
		routeBorder = BorderFactory.createTitledBorder("Tunnels");
		routePanel.setBorder(routeBorder);
		for(RouteComponent rc:routes){
			routePanel.add(rc);
		}
		routePanel.add(newPane);
	}
	
	public static void main(String[] args){
		JFrame f = new JFrame();
		f.setContentPane(new ConnectionsUI());
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.pack();
		f.setVisible(true);
	}
}
