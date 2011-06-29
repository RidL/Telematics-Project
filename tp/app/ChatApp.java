package tp.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Box;

/**
 *
 * @author STUDENT\s1027492
 */
public class ChatApp extends JFrame implements ActionListener {

    private JTextField tfUserID, tfDestAddr, tfDestPort, tfSrcPort, tfChatMssg;
    private JTextArea mssgArea;
    private JButton bConnect;
    private JScrollPane scrollPane;
    private boolean init;
    private Chat chat;
    private ChatReceiver chatReceiver;
    private static JButton NOTIFY;

    public ChatApp(String title, int addr) {
        super(title);

        JPanel p1 = new JPanel();
        p1.setLayout(new BoxLayout(p1, BoxLayout.Y_AXIS));
        JPanel pp = new JPanel(new GridLayout(2, 2));
        pp.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pp.setLayout(new BoxLayout(pp, BoxLayout.Y_AXIS));

        tfUserID = new JTextField("Blanket", 12);
        tfDestAddr = new JTextField(Integer.toString(addr), 12);
        tfDestPort = new JTextField("0", 12);
        tfSrcPort = new JTextField("0", 21);

        tfChatMssg = new JTextField("Michael Zjakson!!!", 12);

        pp.add(new JLabel("ChatName: "));
        pp.add(tfUserID);
        pp.add(new JLabel("Destination address: "));
        pp.add(tfDestAddr);
        pp.add(new JLabel("Destination port: "));
        pp.add(tfDestPort);
        pp.add(new JLabel("Source port: "));
        pp.add(tfSrcPort);

        bConnect = new JButton("Chat with the fellow");
       
        JPanel pButton = new JPanel();
        pButton.setLayout(new BoxLayout(pButton, BoxLayout.X_AXIS));

        pButton.add(bConnect);
        pButton.add(Box.createRigidArea(new Dimension(10, 10)));

        p1.add(pp);
        p1.add(pButton);
        p1.add(new JLabel("Enter message:"));
        p1.add(tfChatMssg);
        tfChatMssg.setEditable(false);

        JPanel p2 = new JPanel();
        p2.setLayout(new BorderLayout());

        JLabel lbMessages = new JLabel("Messages:");
        lbMessages.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 2));
        mssgArea = new JTextArea();
        mssgArea.setBackground(Color.BLACK);
        mssgArea.setForeground(Color.GREEN);
        mssgArea.setEditable(false);

        scrollPane = new JScrollPane(mssgArea);

        scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {

            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                mssgArea.setCaretPosition(mssgArea.getDocument().getLength());
            }
        });

        p2.add(lbMessages, BorderLayout.NORTH);
        p2.add(scrollPane, BorderLayout.CENTER);

        Container cc = getContentPane();
        cc.setLayout(new BorderLayout());

        cc.add(p1, BorderLayout.NORTH);
        cc.add(p2, BorderLayout.CENTER);

        NOTIFY = new JButton();
       
        this.pack();
        this.setSize(500, 500);
        //this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setVisible(true);
        initActionListeners();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(NOTIFY)) {
            mssgArea.append("\n" + e.getActionCommand());
        } else {
            if (!init) {
                tfUserID.setEditable(false);
                tfDestAddr.setEditable(false);
                tfDestPort.setEditable(false);
                tfSrcPort.setEditable(false);
                tfChatMssg.setEditable(true);
                chat = new Chat(Integer.parseInt(tfDestAddr.getText()), Integer.parseInt(tfDestPort.getText()), Integer.parseInt(tfSrcPort.getText()), tfUserID.getText());
                chatReceiver = new ChatReceiver(chat);
                new Thread(chatReceiver).start();
                bConnect.setText("Send");
                mssgArea.setText("Chat initialized");
                init = true;
            } else {
                chat.sendMessage(tfChatMssg.getText());
            }
        }
    }

    public static synchronized void addMessage(String message) {
        NOTIFY.getActionListeners()[0].actionPerformed(new ActionEvent(NOTIFY, 0, message));    //Makes sure the Event Dispatch Thread updates the GUI
    }

    private void initActionListeners() {
         bConnect.addActionListener(this);
         NOTIFY.addActionListener(this);
    }

}