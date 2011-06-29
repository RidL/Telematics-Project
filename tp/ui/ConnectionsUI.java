package tp.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;

import tp.app.ChatApp;
import tp.app.FileTransferGUI;
import tp.link.Link;
import tp.link.Tunnel;
import tp.trans.Trans;

public class ConnectionsUI extends JPanel implements Observer{
	private static final long serialVersionUID = 1L;
	
	private ConnControl ctrl;
	private TransUI tui;
	
	//network UI stuff
	private JPanel networkPanel;
	private JTextField networkAddress;
	private JButton networkConfirm;
	
	//route UI stuff
	private JPanel routePanel;
	private JScrollPane tablePanel;
	ArrayList<RouteOptions> opts;
	private JTable routeData;
	
	private JPanel newPane;
	private JButton newRoute;
	private JButton startChat;
	private JButton startFileTrans;
	private JButton newRef;
	
	public ConnectionsUI(TransUI tui){
		ctrl = new ConnControl();
		opts = new ArrayList<RouteOptions>();
		this.tui = tui;
		
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
		routeData = new JTable(new MyTableModel());
		tablePanel = new JScrollPane(routeData);
		routePanel = new JPanel();
		routePanel.setLayout(new BorderLayout());
		routePanel.setBorder(routeTitle);
		routePanel.add(tablePanel, BorderLayout.CENTER);
		
		newPane = new JPanel();
		newRoute = new JButton("New");
		newRoute.setEnabled(false);
		newRoute.addActionListener(ctrl);
		newPane.add(newRoute);
		startChat = new JButton("Chat");
		startChat.setEnabled(false);
		startChat.addActionListener(ctrl);
		newPane.add(startChat);
		startFileTrans = new JButton("File");
		startFileTrans.setEnabled(false);
		startFileTrans.addActionListener(ctrl);
		newPane.add(startFileTrans);
		newRef = new JButton("Tun");
		newRef.setEnabled(false);
		newRef.addActionListener(ctrl);
		newPane.add(newRef);
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
				Trans.getTrans(Integer.parseInt(networkAddress.getText()));
				tui.addObservers();
				networkConfirm.setEnabled(false);
				networkAddress.setEditable(false);
				newRoute.setEnabled(true);
				startChat.setEnabled(true);
				startFileTrans.setEnabled(true);
				newRef.setEnabled(true);
			}else if(src==newRoute){
				new RouteOptionsFrame();
			}else if(src==startChat){
				int row = routeData.getSelectedRow();
				if(row>=0){
					int addr = Integer.parseInt(opts.get(row).getTP());
					new ChatApp("Chat", addr);
				}
			}else if(src==startFileTrans){
				int row = routeData.getSelectedRow();
				if(row>=0){
					int addr = Integer.parseInt(opts.get(row).getTP());
					new FileTransferGUI("File Transfer", addr);
				}
			}else if(src==newRef){
				new RouteOptionsFrame(true);
			}
		}
	}
	
	@Override
	public void update(Observable arg0, Object arg1) {
		System.out.println("asd");
		Set<Map.Entry<Integer,Link>> routes = Trans.getTrans().getRoute().getRoutes();
		opts.clear();
		for(Map.Entry<Integer, Link> e: routes){
			RouteOptions ro = new RouteOptions();
			ro.setTP(Integer.toString(e.getKey()));
			Link val = e.getValue();
			if(val instanceof Tunnel){
				Tunnel t = (Tunnel)val;
				ro.setIP(t.getAddress());
				ro.setPort(t.getPort());
				ro.setListen( t.isListening());
				ro.setConnected(t.isConnected());
			}else{
				ro.setIP("LPT");
				ro.setPort("LPT");
			}
			opts.add(ro);
		}
		routeData.setModel(new MyTableModel());
	}
	
	private class MyTableModel extends AbstractTableModel{
		private static final long serialVersionUID = 1L;
		
		private final String[] COLUMN_NAMES = 
			{"Name", "TP Addr", "IP Addr", "Port", "Listen", "Connected"};
		
		@Override
		public String getColumnName(int col){
			return COLUMN_NAMES[col];
		}
		
		@Override
		public int getColumnCount() {
			return COLUMN_NAMES.length;
		}

		@Override
		public int getRowCount() {
			return opts.size();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			Object ret = null;
			if(columnIndex==0){
				ret = opts.get(rowIndex).getName();
			}else if(columnIndex==1){
				ret = opts.get(rowIndex).getTP();
			}else if(columnIndex==2){
				ret = opts.get(rowIndex).getIP();
			}else if(columnIndex==3){
				ret = opts.get(rowIndex).getPort();
			}else if(columnIndex==4){
				ret = opts.get(rowIndex).isListen();
			}else if(columnIndex==5){
				ret = opts.get(rowIndex).isConnected();
			}
			return ret;
		}
	}
}
