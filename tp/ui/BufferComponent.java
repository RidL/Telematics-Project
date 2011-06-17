package tp.ui;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class BufferComponent extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private JTextField src;
	private JTextField dst;
	private JTextField seq;
	private JTextField ack;
	private JTextField io;
	private JButton expand;
	
	public BufferComponent(String s){
		src = new JTextField(5);
		dst = new JTextField(5);
		seq = new JTextField();
		ack = new JTextField();
		io = new JTextField();
		expand = new JButton("expand");
		
		add(io);
		add(src);
		add(dst);
		add(seq);
		add(ack);
		add(expand);
	}
	
	public BufferComponent(){
		src = new JTextField("src addr:port");
		src.setEditable(false);
		dst = new JTextField("dst addr:port");
		dst.setEditable(false);
		seq = new JTextField("ack/seq #");
		seq.setEditable(false);
		ack = new JTextField("ackflag");
		ack.setEditable(false);
		io = new JTextField("in/out");
		io.setEditable(false);
		
		add(io);
		add(src);
		add(dst);
		add(seq);
		add(ack);
		add(new JLabel("            "));
	}
}
