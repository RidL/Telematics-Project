package tp.ui;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class RouteComponent extends JPanel {
	private static final long serialVersionUID = 1L;
	private String name;
	private JTextField nameField;
	private JTextField tpIP;
	private JTextField routeIP;
	private JButton routeConfirm;
	
	public RouteComponent(String name){
		this.name = name;
		nameField = new JTextField(name, 20);
		tpIP = new JTextField(5);
		routeIP = new JTextField("130.89.8.", 20);
		routeConfirm = new JButton("GO");
		this.add(nameField);
		this.add(tpIP);
		this.add(routeIP);
		this.add(routeConfirm);
	}
	
	public RouteComponent(){
		nameField = new JTextField("Name", 20);
		nameField.setEditable(false);
		tpIP = new JTextField("TP Addr  ");
		tpIP.setEditable(false);
		routeIP = new JTextField("IP Addr", 20);
		routeIP.setEditable(false);
		this.add(nameField);
		this.add(tpIP);
		this.add(routeIP);
		this.add(new JLabel("              "));
	}
	
	public String getName() {
		return name;
	}

	public JTextField getRouteIP() {
		return routeIP;
	}

	public void setName(String name) {
		nameField.setText(name);
		this.name = name;
	}

	public void setRouteIP(JTextField routeIP) {
		this.routeIP = routeIP;
	}
}
