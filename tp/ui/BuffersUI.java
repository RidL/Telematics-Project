package tp.ui;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

public class BuffersUI extends JPanel{
	private static final long serialVersionUID = 1L;
	
	private JPanel routePanel;
	private BufferComponent routeTitles;
	private ArrayList<BufferComponent> routes;
	
	private JPanel transPanel;
	private BufferComponent transTitles;
	private ArrayList<BufferComponent> socks;

	public BuffersUI(){
		routeTitles = new BufferComponent();
		transTitles = new BufferComponent();
		routes = new ArrayList<BufferComponent>();
		socks = new ArrayList<BufferComponent>();
		buildRouteBuffer();
		buildTransBuffer();
		
		setLayout(new BorderLayout());
		add(routePanel, BorderLayout.CENTER);
		add(transPanel, BorderLayout.SOUTH);
	}
	
	public void buildRouteBuffer(){
		TitledBorder routeBorder;
		routeBorder = BorderFactory.createTitledBorder("Route Buffers");
		routePanel = new JPanel();
		routePanel.setBorder(routeBorder);
		routePanel.add(routeTitles);
	}
	
	public void buildTransBuffer(){
		TitledBorder transBorder;
		transBorder = BorderFactory.createTitledBorder("Trans Buffers");
		transPanel = new JPanel();
		transPanel.setBorder(transBorder);
		transPanel.add(transTitles);
	}
}
