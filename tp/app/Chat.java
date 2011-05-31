package tp.app;

import tp.link.Frame;
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

    public Chat(int destAddr, int destPort, int scrPort, String sender) {
        trans = Trans.getTrans();
        trans.start();
        socket = trans.createSocket(destAddr, scrPort, destPort);
        this.sender = sender;
    }

    public void sendMessage(String message) {
        byte[] origBytes = message.getBytes();
        int messLength = origBytes.length;

        byte length1 = (byte) (messLength >>> 8);
        byte length2 = (byte) messLength;

        String senderInfo = sender + ": ";

        byte[] senderInfoBytes = senderInfo.getBytes();
        int senderLength = senderInfo.length();
        byte[] bytemessage = new byte[2 + senderLength + messLength];


        bytemessage[0] = length1;
        bytemessage[1] = length2;

        for (int d = 2, e = 0; e < senderLength; d++, e++) {
            bytemessage[d] = senderInfoBytes[e];
        }

        for (int k = 2 + senderLength, p = 0; k < bytemessage.length; k++, p++) {
            bytemessage[k] = origBytes[p];
        }

        System.out.println(bytemessage.length);

        byte[] tempMssg;
        int end = 0;
        for (int i = 0; i < bytemessage.length; i += 96) {
            if (bytemessage.length > (i + 96)) {
                end = 96;
            } else {
                end = 96 - ((i + 96) % bytemessage.length);
            }

            tempMssg = new byte[end];
            for (int j = 0; j < end; j++) {
                tempMssg[j] = bytemessage[j + i];
            }
            //  System.out.println(Frame.toBinaryString(tempMssg) + "-in buffer gepleurd");
            boolean suc;
            do {
                suc = socket.writeOut(tempMssg);
            } while (!suc);
        }
    }
}
