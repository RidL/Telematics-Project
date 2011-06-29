package tp.ui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import tp.link.HLReceiver;
import tp.link.HLSender;
import tp.link.Tunnel;
import tp.link.TunnelTimeoutException;
import tp.trans.Trans;

public class RouteOptionsFrame extends JFrame{
	private static final long serialVersionUID = 1L;
	
	private Control conn = new Control();
	
	private JLabel[] labels;
	private JTextField[] texts;
	private JCheckBox listeningCheck;
	private JButton confirm;
	
	public RouteOptionsFrame(){
		buildUI();
	}
	
	public void buildUI(){
		setTitle("Route Options");
		
		JPanel contentPane = new JPanel();
		GridLayout layout = new GridLayout(2,6);
		layout.setHgap(5);
		contentPane.setLayout(layout);
		
		labels = new JLabel[5];
		labels[0] = new JLabel("Name");
		labels[1] = new JLabel("TP Addr");
		labels[2] = new JLabel("IP Addr");
		labels[3] = new JLabel("Port");
		labels[4] = new JLabel("Listen");
		for(JLabel l: labels)
			contentPane.add(l);
		contentPane.add(new JLabel()); //one empty for the button
		
		texts = new JTextField[4];
		texts[0] = new JTextField(10);
		texts[1] = new JTextField(5);
		texts[2] = new JTextField(10);
		texts[3] = new JTextField(5);
		for(JTextField t: texts)
			contentPane.add(t);
		
		listeningCheck = new JCheckBox();
		confirm = new JButton("GO");
		confirm.addActionListener(conn);
		contentPane.add(listeningCheck);
		contentPane.add(confirm);
		
		setContentPane(contentPane);
		pack();
		setVisible(true);
	}
	
	private class Control implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			if(texts[3].getText().equalsIgnoreCase("LPT")){
				HLSender snd;
				HLReceiver rcv;
				rcv = new HLReceiver();
				snd = new HLSender(rcv);
				rcv.setSender(snd);
				Trans.getTrans().getRoute().addRoute(Integer.parseInt(texts[1].getText()), snd);
			}else{
				try {
					Tunnel t = new Tunnel(texts[2].getText(), Integer.parseInt(texts[3].getText()), listeningCheck.isSelected());
					Trans.getTrans().getRoute().addRoute(Integer.parseInt(texts[1].getText()), t);
					t.start();
				} catch (NumberFormatException nfe) {
					nfe.printStackTrace();
				} catch (TunnelTimeoutException tte) {
					System.err.println("Timeout while connecting to " + texts[2].getText());
				}
			}
			dispose();
		}
		
	}
}
