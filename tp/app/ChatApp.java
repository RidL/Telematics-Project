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
import javax.swing.*;

/**
 *
 * @author STUDENT\s1027492
 */
public class ChatApp extends JFrame implements ActionListener {

    private JTextField tfUserID, tfDestAddr, tf;
    private JTextArea mssgArea;
    private JButton bConnect;
    private JScrollPane scrollPane;

    public ChatApp(String title) {
        super(title);


        JPanel p1 = new JPanel();
        p1.setLayout(new BoxLayout(p1, BoxLayout.Y_AXIS));
        JPanel pp = new JPanel(new GridLayout(2, 2));
        pp.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        tfUserID = new JTextField("Enter your chatname", 12);
        tfDestAddr = new JTextField("Enter destination address", 12);


        pp.add(new JLabel("ChatName: "));
        pp.add(tfUserID);
        pp.add(new JLabel("Destination address: "));
        pp.add(tfDestAddr);
        pp.add(new JLabel("Destination port: "));
        

        bConnect = new JButton("Start Listening");
        bConnect.addActionListener(this);

        JPanel pButton = new JPanel();
        pButton.setLayout(new BoxLayout(pButton, BoxLayout.X_AXIS));

        pButton.add(bConnect);
        pButton.add(Box.createRigidArea(new Dimension(10, 10)));

        p1.add(pp);
        p1.add(pButton);

        // Panel p2 - Messages
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

        this.pack();

        this.setSize(500, 500);
        //this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        tfUserID.setEditable(false);
    }
}