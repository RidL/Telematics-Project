package tp.ui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Iterator;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;

import tp.trans.RouteStats;
import tp.trans.Trans;

public class StatsUI extends JPanel implements Observer{
	private static final long serialVersionUID = 1L;
	
	private JPanel inStatsPanel;
	private JLabel inNum;
	private JScrollPane inScroll;
	private JTable inTable;
	
	private JPanel outStatsPanel;
	private JLabel outNum;
	private JScrollPane outScroll;
	private JTable outTable;
	
	private JPanel routStatsPanel;
	private JLabel routNum;
	private JScrollPane routScroll;
	private JTable routTable;
	
	public StatsUI(){
		TitledBorder is = BorderFactory.createTitledBorder("In Stats");
		inStatsPanel = new JPanel();
		inStatsPanel.setBorder(is);
		inStatsPanel.setLayout(new GridLayout(1,2));
		inNum = new JLabel("Outgoing: ");
		inStatsPanel.add(inNum);
		inTable = new JTable();
		inTable.setAutoCreateRowSorter(true);
		inTable.setModel(new InTableModel());
		inScroll = new JScrollPane(inTable);
		inScroll.setPreferredSize(new Dimension(200,200));
		inStatsPanel.add(inScroll);
		
		
		TitledBorder os = BorderFactory.createTitledBorder("Out Stats");
		outStatsPanel = new JPanel();
		outStatsPanel.setBorder(os);
		outStatsPanel.setLayout(new GridLayout(1,2));
		outNum = new JLabel("Ingoing: ");
		outStatsPanel.add(outNum);
		outTable = new JTable(new InTableModel());
		outTable.setAutoCreateRowSorter(true);
		outTable.setModel(new OutTableModel());
		outScroll = new JScrollPane(outTable);
		outScroll.setPreferredSize(new Dimension(200,200));
		outStatsPanel.add(outScroll);
		
		
		TitledBorder rst = BorderFactory.createTitledBorder("Route Stats");
		routStatsPanel = new JPanel();
		routStatsPanel.setBorder(rst);
		routStatsPanel.setLayout(new GridLayout(1,2));
		routNum = new JLabel("Routed: ");
		routStatsPanel.add(routNum);
		routTable = new JTable(new InTableModel());
		routTable.setAutoCreateRowSorter(true);
		routTable.setModel(new RoutTableModel());
		routScroll = new JScrollPane(routTable);
		routScroll.setPreferredSize(new Dimension(200, 200));
		routStatsPanel.add(routScroll);
		
		setLayout(new GridLayout(3,1));
		add(inStatsPanel);
		add(outStatsPanel);
		add(routStatsPanel);
	}

	private class InTableModel extends AbstractTableModel{
		private static final long serialVersionUID = 1L;
		
		private final String[] COLUMN_NAMES = 
			{"In Port", "#"};
		
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
			return Trans.isInited()?
					Trans.getTrans().getRoute().getRouteStats().getInPort().size():
					0;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			Object ret = null;
			if(Trans.isInited()){
				Set<Map.Entry<Integer, Integer>> data = Trans.getTrans().getRoute().
						getRouteStats().getInPort().entrySet();
				Iterator<Map.Entry<Integer,Integer>> it=data.iterator();
				for(int i=0; it.hasNext()&&(i<rowIndex); i++){
					if(columnIndex==0){
						ret = it.next().getKey();
					}else{
						ret = it.next().getValue();
					}
				}
			}else{
				ret = "";
			}
			return ret;
		}
	}
	
	private class OutTableModel extends AbstractTableModel{
		private static final long serialVersionUID = 1L;
		
		private final String[] COLUMN_NAMES = 
			{"Out Addr", "#"};
		
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
			return Trans.isInited()?
					Trans.getTrans().getRoute().getRouteStats().getOutAddr().size():
					0;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			Object ret = null;
			if(Trans.isInited()){
				Set<Map.Entry<Integer, Integer>> data = Trans.getTrans().getRoute().
						getRouteStats().getOutAddr().entrySet();
				Iterator<Map.Entry<Integer,Integer>> it=data.iterator();
				for(int i=0; it.hasNext()&&(i<rowIndex); i++){
					if(columnIndex==0){
						ret = it.next().getKey();
					}else{
						ret = it.next().getValue();
					}
				}
			}else{
				ret = "";
			}
			return ret;
		}
	}
	
	private class RoutTableModel extends AbstractTableModel{
		private static final long serialVersionUID = 1L;
		
		private final String[] COLUMN_NAMES = 
			{"Out Addr", "#"};
		
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
			return Trans.isInited()?
					Trans.getTrans().getRoute().getRouteStats().getRoutedAddr().size():
					0;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			Object ret = null;
			if(Trans.isInited()){
				Set<Map.Entry<Integer, Integer>> data = Trans.getTrans().getRoute().
						getRouteStats().getRoutedAddr().entrySet();
				Iterator<Map.Entry<Integer,Integer>> it=data.iterator();
				for(int i=0; it.hasNext()&&(i<rowIndex); i++){
					if(columnIndex==0){
						ret = it.next().getKey();
					}else{
						ret = it.next().getValue();
					}
				}
			}else{
				ret = "";
			}
			return ret;
		}
	}
	
	@Override
	public void update(Observable arg0, Object arg1) {
		RouteStats stats = Trans.getTrans().getRoute().getRouteStats();
		inNum.setText("Outoing: " + stats.getInNum());
		outNum.setText("Ingoing: " + stats.getOutNum());
		routNum.setText("Routed: " + stats.getRoutedNum());
	}
}
