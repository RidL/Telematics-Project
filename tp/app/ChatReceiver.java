/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tp.app;

import java.util.logging.Level;
import java.util.logging.Logger;
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
        byte[] data;
        while (true) {
            if ((data = socket.readIn()) != null) {
                processMessage(data);
            }
        }
    }

    private void processMessage(byte[] data) {
        int messLength = 0;
       // System.out.println(data[0] + "-" + data[1]);
        messLength |= data[0];
      //  System.out.println(Integer.toBinaryString(messLength));
        messLength <<= 24;
        messLength >>>= 24;
        messLength <<= 8;
       // System.out.println(Integer.toBinaryString(messLength));
      //  data[1] <<= 24;
       // data[1] >>>= 24;
        int daat = (int) data[1];
        daat = daat & 0x000000ff;
       // System.out.println(Integer.toBinaryString(daat) + "=data");
        messLength |= daat;
       // System.out.println(Integer.toBinaryString(messLength));
        messLength <<= 16;
        messLength >>>= 16;
       // System.out.println(Integer.toBinaryString(messLength));
        char[] message = new char[messLength];

        for (int i = 2, j = 0; i < data.length; i++, j++) {
            message[j] = (char) (data[i]);
        }

        if (messLength > 94) {
            byte[] newData;
            int expectedMssgs = (messLength - 94) / 96 + 1;
            for (int i = 0; i < expectedMssgs; i++) {
                boolean done = false;
                while (!done) {
                    
                    if ((newData = socket.readIn()) != null) {
                        for (int j = 94 + (i * 96), k = 0; k < newData.length; j++, k++) {
                            message[j] = (char) newData[k];
                            done = true;
                        }
                    }

                    if (!done) {
                        try {
                        Thread.sleep(5);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ChatReceiver.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    }
                }
            }
        }
        count++;
        System.out.println("Ontvangen: " + String.valueOf(message));
    }

    /**
     * @return the count
     */
    public int getCount() {
        return count;
    }
}
