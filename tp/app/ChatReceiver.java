package tp.app;

import tp.trans.TPSocket;

/**
 *
 * @author STUDENT\s1027492
 */
public class ChatReceiver implements Runnable {

    private TPSocket socket;
    private int count;

    public ChatReceiver(Chat chat) {
        socket = chat.getSocket();
    }

    @Override
    public void run() {
        while (true) {
            processMessage(socket.readIn());    //new message received
        }
    }

    private void processMessage(byte[] data) {
        int messLength = 0;
        messLength |= data[0];
        messLength <<= 24;
        messLength >>>= 16;
        messLength |= (int) (data[1] & 0x000000ff);

        char[] message = new char[messLength];
        for (int i = 2, j = 0; i < data.length; i++, j++) {
            message[j] = (char) (data[i]);
        }

        if (messLength > 94) {
            byte[] newData;
            int expectedMssgs = (messLength - 94) / 96 + 1;
            for (int i = 0; i < expectedMssgs; i++) {
                newData = socket.readIn();
                for (int j = 94 + (i * 96), k = 0; k < newData.length; j++, k++) {
                    message[j] = (char) newData[k];
                }
            }
        }
        count++;
        ChatApp.addMessage("RCVD: " + String.valueOf(message));
    }

    /**
     * @return the count
     */
    public int getCount() {
        return count;
    }
}
