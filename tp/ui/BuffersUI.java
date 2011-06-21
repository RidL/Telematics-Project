package tp.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;

import tp.trans.Segment;
import tp.trans.Trans;

public class BuffersUI extends JPanel implements Observer{
	private static final long serialVersionUID = 1L;
	
	//private RouteStats routeStats;
	private JPanel routePanel;
	private JScrollPane routeBuff;
	private JTable routeTable;
	
	private JPanel transPanel;
	private JScrollPane transBuff;
	private JTable transTable;

	public BuffersUI(){
		TitledBorder rt = BorderFactory.createTitledBorder("Route Buffs");
		routePanel = new JPanel();
		routePanel.setBorder(rt);
		routePanel.setLayout(new BorderLayout());
		routeTable = new JTable(new MyRouteTableModel());
		routeBuff = new JScrollPane(routeTable);
		routePanel.add(routeBuff, BorderLayout.CENTER);
		routeBuff.setPreferredSize(new Dimension(500,300));
		
		TitledBorder tt = BorderFactory.createTitledBorder("Trans Buffs");
		transPanel = new JPanel();
		transPanel.setBorder(tt);
		transPanel.setLayout(new BorderLayout());
		transTable = new JTable(new MyRouteTableModel());
		transBuff = new JScrollPane(transTable);
		transBuff.setPreferredSize(new Dimension(500, 300));
		transPanel.add(transBuff, BorderLayout.CENTER);
		
		setLayout(new BorderLayout());
		add(routePanel, BorderLayout.NORTH);
		add(transPanel, BorderLayout.CENTER);
	}
	
	public void setObs(){
		Trans.getTrans().getRoute().addObserver(this);
	}
	
	@Override
	public void update(Observable arg0, Object arg1) {
		routeTable.setModel(new MyRouteTableModel());
	}
	
	private class MyRouteTableModel extends AbstractTableModel{
		private static final long serialVersionUID = 1L;
		
		private final String[] COLUMN_NAMES = 
			{"Src Addr", "Src Port", "Dst Addr", "Dst Port", "Len", "SEQ", "ACK", "HSH"};
		
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
//			return opts.size();
			return 0;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			Object ret = null;
			Segment s = Trans.getTrans().getRoute().getRoutableSegments().get(rowIndex);
			if(columnIndex==0){
				ret = s.getSourceAddress();
			}else if(columnIndex==1){
				ret = s.getSourcePort();
			}else if(columnIndex==2){
				ret = s.getDestinationAddress();
			}else if(columnIndex==3){
				ret = s.getDestinationPort();
			}else if(columnIndex==4){
				ret = s.getLength();
			}else if(columnIndex==5){
				ret = s.getSEQ();
			}else if(columnIndex==6){
				ret = s.isACK();
			}else if(columnIndex==7){
				ret = s.getHash();
			}
			return ret;
		}
	}
}
