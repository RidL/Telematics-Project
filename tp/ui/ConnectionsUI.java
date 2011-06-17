package tp.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableColumnModel;

import tp.trans.Trans;

public class ConnectionsUI extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private Trans trans;
	
	private ConnControl ctrl;
	
	private JPanel networkPanel;
	private JTextField networkAddress;
	private JButton networkConfirm;
	
	private JPanel routePanel;
	private JTable routeData;
	
	private JPanel newPane;
	private JButton newRoute;
	private JButton listenStart;
	private JTextField listenPort;
	
	public ConnectionsUI(){
		ctrl = new ConnControl();
		
		TitledBorder networkBorder;
		networkBorder = BorderFactory.createTitledBorder("Local");
		networkPanel = new JPanel();
		networkPanel.setBorder(networkBorder);
		networkAddress = new JTextField("0", 5);
		networkConfirm = new JButton("Start");
		networkConfirm.addActionListener(ctrl);
		networkPanel.add(networkAddress);
		networkPanel.add(networkConfirm);
		
		TitledBorder routeTitle;
		routeTitle = BorderFactory.createTitledBorder("Tunnels");
		routePanel = new JPanel();
		routePanel.setBorder(routeTitle);
		routePanel.setLayout(new BorderLayout());
		String[] columnNames = {"#","name", "TP Addr", "IP Addr"};
		Object[][] data = {{"0","","0","130.89.8."}};
		routeData = new JTable(data, columnNames);
		routePanel.add(routeData, BorderLayout.CENTER);
		
		newPane = new JPanel();
		newRoute = new JButton("New");
		newRoute.addActionListener(ctrl);
		newPane.add(newRoute);
		listenStart = new JButton("Listen");
		listenPort = new JTextField("10101", 5);
		newPane.add(listenPort);
		newPane.add(listenStart);
		routePanel.add(newPane, BorderLayout.SOUTH);
		
		setLayout(new BorderLayout());
		this.add(networkPanel, BorderLayout.NORTH);
		this.add(routePanel, BorderLayout.CENTER);
	}
	
	
	
	private class ConnControl implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent ae) {
			JButton src = (JButton)ae.getSource();
			if(src==networkConfirm){
				trans = Trans.getTrans(Integer.parseInt(networkAddress.getText()));
			}else if(src==newRoute){
				//TODO: routes.add(new RouteComponent(""));
				//buildRoutes();
			}
		}
	}
	
	public static void main(String[] args){
		JFrame f = new JFrame();
		f.setContentPane(new ConnectionsUI());
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.pack();
		f.setVisible(true);
	}
}
