package tp.app;

import java.util.logging.Level;
import java.util.logging.Logger;
import tp.trans.TPSocket;
import tp.trans.Trans;

/**
 *
 * @author STUDENT\s1027492
 */
public class Chat {

    private Trans trans;
    private TPSocket socket;
    private String sender;
    private int count;

    public Chat(int destAddr, int destPort, int scrPort, String sender) {
        trans = Trans.getTrans();
      //  trans.start();
        socket = trans.createSocket(destAddr, scrPort, destPort);
        this.sender = sender;
    }

    public void sendMessage(String message) {
        String senderInfo = sender + ": ";
        message = senderInfo += message;
        byte[] origBytes = message.getBytes();
        int messLength = origBytes.length;

//        System.out.println(messLength);

        if (messLength >= (65536)) {
            System.out.println("message too long: size: " + messLength);
            return;
        }

        byte length1 = (byte) (messLength >>> 8);
        byte length2 = (byte) messLength;

      //  System.out.println(Integer.toBinaryString(length1));
      //  System.out.println(Integer.toBinaryString(length2));


        byte[] bytemessage = new byte[2 + messLength];

        bytemessage[0] = length1;
        bytemessage[1] = length2;

        for (int k = 2, p = 0; k < bytemessage.length; k++, p++) {
            bytemessage[k] = origBytes[p];
        }




        byte[] tempMssg;
        int end = 0;
        for (int i = 0; i < bytemessage.length; i += 96) {
            if (bytemessage.length > (i + 96)) {
                end = 96;
            } else {
                end = bytemessage.length - i;
            }

            tempMssg = new byte[end];
            for (int j = 0; j < end; j++) {
                tempMssg[j] = bytemessage[j + i];
            }
            boolean suc;
            do {
                try {
                    Thread.sleep(5);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Chat.class.getName()).log(Level.SEVERE, null, ex);
                }
                suc = socket.writeOut(tempMssg);
            } while (!suc);
        }
        count++;
        System.out.println("Verzonden: " + message);

    }

    public TPSocket getSocket() {
        return socket;
    }

    /**
     * @return the count
     */
    public int getCount() {
        return count;
    }
}
