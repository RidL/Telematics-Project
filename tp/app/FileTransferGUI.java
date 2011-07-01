package tp.app;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import tp.trans.TPSocket;
import tp.trans.Trans;
import tp.util.Log;

/**
 *
 * @author jesse
 */
public class FileTransferGUI extends JFrame implements WindowListener{

    private File file;
    private FileSender fs;
    private FileReceiver fr;
    private JPanel filePanel;
    private ButtonHandler bHandler;
    private JTextField addressField,  sourcePortField,  destPortField,  fileField;

    public FileTransferGUI(String title, int addr) {
        super(title);
        bHandler = new ButtonHandler();
        buildGUI(addr);
        addWindowListener(this);
        setSize(400, 400);
        setVisible(true);
        pack();
        new ButtonHandler();
    }

    private void buildGUI(int addr) {
        Container c = getContentPane();
        JPanel rootPanel = new JPanel();
        JLabel addressLabel = new JLabel("Address");
        addressField = new JTextField();
        addressField.setColumns(10);
        addressField.setText(Integer.toString(addr));
        JLabel sourcePortLabel = new JLabel("Source port");
        sourcePortField = new JTextField();
        sourcePortField.setColumns(10);
        sourcePortField.setText("1");
        JLabel destPortLabel = new JLabel("Destination port");
        destPortField = new JTextField();
        destPortField.setColumns(10);
        destPortField.setText("1");

        JLabel fileLabel = new JLabel("File");
        JButton fileButton = new JButton("Browse");
        fileField = new JTextField();
        fileField.setColumns(20);
        //fileField.setText("/home/STUDENT/s1012886/SNSD-Gee.jpg");
        fileButton.setActionCommand("add_file");
        fileButton.addActionListener(bHandler);
        JButton sendButton = new JButton("Send");
        sendButton.setActionCommand("send");
        sendButton.addActionListener(bHandler);
        JButton recButton = new JButton("Receive");
        recButton.setActionCommand("receive");
        recButton.addActionListener(bHandler);

        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));
        labelPanel.add(addressLabel);
        labelPanel.add(sourcePortLabel);
        labelPanel.add(destPortLabel);

        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.Y_AXIS));
        fieldPanel.add(addressField);
        fieldPanel.add(sourcePortField);
        fieldPanel.add(destPortField);

        filePanel = new JPanel();
        filePanel.add(fileLabel);
        filePanel.add(fileField);
        filePanel.add(fileButton);
        filePanel.add(sendButton);
        filePanel.add(recButton);

        rootPanel.add(labelPanel);
        rootPanel.add(fieldPanel);
        rootPanel.add(filePanel);

        c.add(rootPanel);
    }

    class ButtonHandler implements ActionListener {

        boolean init = false;
        int address;
        int sourcePort;
        int destPort;

        public void actionPerformed(ActionEvent e) {
            if (!init) {
                address = Integer.parseInt(addressField.getText());
                sourcePort = Integer.parseInt(sourcePortField.getText());
                destPort = Integer.parseInt(destPortField.getText());
                init = true;
            }

            if (e.getActionCommand().equals("add_file")) {
                JFileChooser jfs = new JFileChooser();
                int succ = jfs.showDialog(filePanel, "Choose a file to send");
                if (succ == JFileChooser.APPROVE_OPTION) {
                    file = jfs.getSelectedFile();
                    fileField.setText(file.getAbsolutePath());
                }

            } else if (e.getActionCommand().equals("send")) {
                address = Integer.parseInt(addressField.getText());
                sourcePort = Integer.parseInt(sourcePortField.getText());
                destPort = Integer.parseInt(destPortField.getText());
                try {
                	if(fs == null)
                		fs = new FileSender(address, sourcePort, destPort);
                    fs.send(file.getAbsolutePath());
                } catch (FileNotFoundException ex) {
                    System.out.println("ERROR: File not found ( " + file.getAbsolutePath() + ")");
                } catch (IOException ex) {
                    System.out.println("ERROR in reading data");
                } catch (InterruptedException ex) {
                    System.out.println("Thread error");
                }

            } else if (e.getActionCommand().equals("receive")) {
                address = Integer.parseInt(addressField.getText());
                sourcePort = Integer.parseInt(sourcePortField.getText());
                destPort = Integer.parseInt(destPortField.getText());
                if(fr==null){
                	fr = new FileReceiver(address, sourcePort, destPort);
                    fr.start();
                }
            }
        }
    }

	@Override
	public void windowActivated(WindowEvent arg0) {}

	@Override
	public void windowClosed(WindowEvent arg0) {}

	@Override
	public void windowClosing(WindowEvent arg0) {
		Trans.getTrans().closeSocket(fr.getSocket());
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {}

	@Override
	public void windowDeiconified(WindowEvent arg0) {}

	@Override
	public void windowIconified(WindowEvent arg0) {}

	@Override
	public void windowOpened(WindowEvent arg0) {}
}
