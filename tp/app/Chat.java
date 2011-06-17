package tp.app;

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
        socket = trans.createSocket(destAddr, scrPort, destPort);
        this.sender = sender;
    }

    public void sendMessage(String message) {
        String senderInfo = sender + ": ";
        message = senderInfo += message;
        byte[] origBytes = message.getBytes();
        int messLength = origBytes.length;

        if (messLength >= (65536)) {
            System.out.println("message too long: size: " + messLength);
            return;
        }

        byte length1 = (byte) (messLength >>> 8);
        byte length2 = (byte) messLength;

        byte[] bytemessage = new byte[2 + messLength];

        bytemessage[0] = length1;
        bytemessage[1] = length2;

        for (int k = 2, p = 0; k < bytemessage.length; k++, p++) {
            bytemessage[k] = origBytes[p];
        }

        messLength = bytemessage.length;

        byte[] tempMssg;
        int end = 0;
        for (int i = 0; i < messLength; i += 96) {
            if (messLength > (i + 96)) {
                end = 96;
            } else {
                end = messLength - i;
            }

            tempMssg = new byte[end];
            for (int j = 0; j < end; j++) {
                tempMssg[j] = bytemessage[j + i];
            }
            socket.writeOut(tempMssg);
        }
        count++;
        ChatApp.addMessage("SND: " + message);
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
