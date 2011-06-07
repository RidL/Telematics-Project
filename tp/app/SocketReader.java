package tp.app;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jesse
 */
public class SocketReader extends Thread {
    
    private ServerSocket ssock;
    private Socket sock;
    private InputStream is;
    private FakeSocket fs;

    public SocketReader(FakeSocket fs) {
        this.fs = fs;
        try {
            ssock = new ServerSocket(9876);
            sock = null;
        } catch (IOException ex) {
            Logger.getLogger(SocketReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        while(sock == null) {
            try {
                sock = ssock.accept();
                System.out.println("hoe vaak kom ik hier dan?");
                is = sock.getInputStream();
            } catch (IOException ex) {
                Logger.getLogger(SocketReader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        byte[] bytes = new byte[96];
        int dataRead = 0;
        try {
            dataRead = is.read(bytes);
            System.out.println("lees ik data");
        }
        catch (IOException ex) {
            Logger.getLogger(SocketReader.class.getName()).log(Level.SEVERE, null, ex);
        }

        while(dataRead != -1) {
            try {
                System.out.println("am i writing data into buffer?");
                fs.writeIn(bytes);
                dataRead = is.read(bytes);
            } catch (IOException ex) {
                Logger.getLogger(SocketReader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public byte[] read() {
        byte[] result = new byte[96];
        try {
            is.read(result);
        } catch (IOException ex) {
            Logger.getLogger(SocketReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
}
