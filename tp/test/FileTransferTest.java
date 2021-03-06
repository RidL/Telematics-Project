/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tp.test;

import tp.trans.SocketTakenException;
import tp.trans.TPSocket;
import tp.trans.Trans;
import tp.trans.UnkownTPHostException;

/**
 *
 * @author STUDENT\s1012886
 */
public class FileTransferTest {

    private Trans trans;
    private TPSocket tpsocket;

    public FileTransferTest() {
        trans = Trans.getTrans();
        trans.start();
        try {
			tpsocket = trans.createSocket(12, 5, 7);
		} catch (SocketTakenException e) {
			e.printStackTrace();
		} catch (UnkownTPHostException e) {
			e.printStackTrace();
		}
        while (true) {
            byte[] data = new byte[96];
            for (int i = 0; i < 95; i++) {
                data[i] = (byte) (30 * (Math.random()) + 1);
            }
            tpsocket.writeOut(data);
           // System.out.println(tpsocket.isOutDirty() + "writer");
        }
    }

    public static void main(String[] args) {
         new FileTransferTest();
    }
}
