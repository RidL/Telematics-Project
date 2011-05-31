package tp.app;

import javax.swing.JFrame;

/**
 *
 * @author STUDENT\s1027492
 */
public class ChatApp extends JFrame{

    public static void main(String[] args) {
        Chat chat = new Chat(2, 13, 4, "Blanket");
        ChatReceiver cr = new ChatReceiver(chat);
        new Thread(cr).start();
       
        chat.sendMessage("nienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienkenienke");
        chat.sendMessage("Robin voert geen flikker uit op dit moment!");
        chat.sendMessage("Michael Zjakson");
    }
}
