/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tp.app;

import tp.trans.TPSocket;

/**
 *
 * @author STUDENT\s1027492
 */
public class ChatReceiver implements Runnable {

    private TPSocket socket;

    public ChatReceiver(Chat chat) {
        socket = chat.getSocket();
    }

    @Override
    public void run() {
        byte[] data;
        while (true) {
            if ((data = socket.readIn()) != null) {
                processMessage(data);
            }
        }
    }

    private void processMessage(byte[] data) {
        int messLength = 0;
        messLength |= data[0];
        messLength <<= 8;
        messLength |= data[1];

        char[] message = new char[messLength];

        for (int i = 2, j = 0; i < data.length; i++, j++) {
            message[j] = (char) (data[i]);
        }

        if (messLength > 94) {
            byte[] newData;
            int expectedMssgs = (messLength - 94)/96 + 1;
            for (int i = 0; i < expectedMssgs; i++) {
                boolean done = false;
                while (!done) {
                    if ((newData = socket.readIn()) != null) {
                        for (int j = 94 + (i * 96), k = 0; k < newData.length; j++, k++) {
                            message[j] = (char) newData[k];
                            done = true;
                        }
                    }
                }
            }
        }

        System.out.println("Ontvangen: " + String.valueOf(message));
    }
}
