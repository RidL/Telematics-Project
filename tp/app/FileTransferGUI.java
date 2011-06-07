package tp.app;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author jesse
 */
public class FileTransferGUI extends JFrame {

    private File file;
    private FileSender fs;
    private FileReceiver fr;
    private JPanel filePanel;
    private ButtonHandler bHandler;
    private JTextField addressField, sourcePortField, destPortField, fileField;

    public FileTransferGUI(String title) {
        super(title);
        bHandler = new ButtonHandler();
        buildGUI();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400);
        setVisible(true);
        pack();
    }

    private void buildGUI() {
        Container c = getContentPane();
        JPanel rootPanel = new JPanel();
        JLabel addressLabel = new JLabel("Address");
        addressField = new JTextField();
        addressField.setColumns(10);
        JLabel sourcePortLabel = new JLabel("Source port");
        sourcePortField = new JTextField();
        sourcePortField.setColumns(10);
        JLabel destPortLabel = new JLabel("Destination port");
        destPortField = new JTextField();
        destPortField.setColumns(10);

        JLabel fileLabel = new JLabel("File");
        JButton fileButton = new JButton("Browse");
        fileField = new JTextField();
        fileField.setColumns(20);
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

    public static void main(String[] args) {
        new FileTransferGUI("File Transfer");
    }

    class ButtonHandler implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            if (e.getActionCommand().equals("add_file")) {
                JFileChooser jfs = new JFileChooser();
                int succ = jfs.showDialog(filePanel, "Choose a file to send");
                if (succ == JFileChooser.APPROVE_OPTION) {
                    file = jfs.getSelectedFile();
                    fileField.setText(file.getAbsolutePath());
                }

            }
            else if (e.getActionCommand().equals("send")) {
                int  address = Integer.parseInt(addressField.getText());
                int sourcePort = Integer.parseInt(addressField.getText());
                int destPort = Integer.parseInt(destPortField.getText());
                fs = new FileSender(address, sourcePort, destPort);
                fs.send(file.getAbsolutePath());
            }
            else if (e.getActionCommand().equals("receive")) {
                int  address = Integer.parseInt(addressField.getText());
                int sourcePort = Integer.parseInt(addressField.getText());
                int destPort = Integer.parseInt(destPortField.getText());
                fr = new FileReceiver(address, sourcePort, destPort);
                fr.start();
            }
        }
    }
}
