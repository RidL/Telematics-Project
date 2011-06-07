/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tp.app;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jesse
 */
public class SocketWriter extends Thread {
    
    private Socket sock;
    private OutputStream os;
    private FakeSocket fs;

    public SocketWriter(FakeSocket fs) {
        this.fs = fs;
        try {
            sock = new Socket("127.0.0.1", 9876);
            os = sock.getOutputStream();
        } catch (UnknownHostException ex) {
            Logger.getLogger(SocketWriter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SocketWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void write(byte[] bytes) {
        try {
            os.write(bytes);
        } catch (IOException ex) {
            Logger.getLogger(SocketWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        byte[] data = fs.readOut();
        while(data != null) {
            try {
                os.write(data);
                data = fs.readOut();
            } catch (IOException ex) {
                Logger.getLogger(SocketWriter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
