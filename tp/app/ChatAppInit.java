package tp.app;

import tp.trans.Trans;
import tp.util.Log;

/**
 *
 * @author STUDENT\s1027492
 */
public class ChatAppInit {

    public static void main(String[] args) {
        ChatApp app = new ChatApp("Chat", Trans.getTrans().getAddress());
        Log.getInstance("RMS");
    }
}

