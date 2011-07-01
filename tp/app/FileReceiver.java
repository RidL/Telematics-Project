package tp.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import tp.trans.SocketTakenException;
import tp.trans.TPSocket;
import tp.trans.Trans;
import tp.trans.UnkownTPHostException;

/**
 * Responsible for reading data from the socket
 * @author STUDENT\s1012886
 */
public class FileReceiver extends Thread {

    private static final int MAX_SEGMENT_DATA = 96;
    private TPSocket tpSocket;
    private File file;
    private FileOutputStream fos;

    /**
     * Creates a new FileReceiver
     * @param address the transport layer address of this receiver
     * @param srcPort the source port
     * @param dstPort the destination port
     */
    public FileReceiver(int address, int srcPort, int dstPort) {
        try {
			tpSocket = Trans.getTrans().createSocket(address, srcPort, dstPort);
		} catch (SocketTakenException e) {
			e.printStackTrace();
		} catch (UnkownTPHostException e) {
			e.printStackTrace();
		}
    }

    public FileReceiver(int address, int srcPort, int dstPort, FileSender sender) {
        //tpSocket = trans.createSocket(address, srcPort, dstPort);
        //tpSocket = new FakeSocket(false);

        tpSocket = sender.getSocket();
    }

    /**
     * Reads data from the socket and writes it to a file
     */
    public void receive() throws FileNotFoundException, IOException, InterruptedException {
        byte[] bytesIn = null;
        System.out.println("Started Waiting for File");
        bytesIn = tpSocket.readIn();
        System.out.println("bytesIN: " + bytesIn);

        int fileNameLength = (int) bytesIn[0];
        byte[] fileName = new byte[fileNameLength];

        // read fileName from tpSocket
        // fileName might be longer than 1 tl-segment
        int i, j;
        for (i = 1, j = 0; j < fileNameLength; i++, j++) {
            fileName[j] = bytesIn[i];
            if (i == MAX_SEGMENT_DATA - 1) {
                bytesIn = tpSocket.readIn();
            }
        }

        // read fileLength from tpSocket
        byte[] fileLength = new byte[8];
        for (j = 0; j < 8; i++, j++) {
            fileLength[j] = bytesIn[i];
            if (i == MAX_SEGMENT_DATA - 1) {
                bytesIn = tpSocket.readIn();
            }
        }
        System.out.println("Filelength received: " + bytesToLong(fileLength));

        // read (first) data that is left from the segment containing header info
        byte[] firstData = new byte[MAX_SEGMENT_DATA - i];
        for (j = 0; i < MAX_SEGMENT_DATA; i++, j++) {
            firstData[j] = bytesIn[i];
        }
        System.out.println("File gemaakt, rest van de data in file zette------------------------------------------------------------");
        file = new File(new String(fileName));
        fos = new FileOutputStream(file);
        fos.write(firstData);
        
        // keep reading data from tpSocket
        int dataPtr = firstData.length;
        byte[] read = null;
        long length = bytesToLong(fileLength);
        while (dataPtr < length) {
        	System.out.println(dataPtr +" = datapointer");
            read = tpSocket.readIn();
            fos.write(read);
            dataPtr += read.length;
        }
        fos.flush();
        fos.close();
        System.out.println("File is binnen!------------------------------------------------------------");
    }

    public static byte[] longToBytes(long in) {
        byte[] result = new byte[8];

        result[0] = (byte) (in >>> 56);
        result[1] = (byte) (in >>> 48);
        result[2] = (byte) (in >>> 40);
        result[3] = (byte) (in >>> 32);
        result[4] = (byte) (in >>> 24);
        result[5] = (byte) (in >>> 16);
        result[6] = (byte) (in >>> 8);
        result[7] = (byte) in;
        return result;
    }

    public static long bytesToLong(byte[] bytes) {
        long result = 0;

//        result += ((long) bytes[0] << 56);
//        result += ((long) bytes[1] << 48);
//        result += ((long) bytes[2] << 40);
//        result += ((long) bytes[3] << 32);
//        result += ((long) bytes[4] << 24);
//        result += ((long) bytes[5] << 16);
//        result += ((long) bytes[6] << 8);
//        result += ((long) bytes[7] << 0);
// Why doesn't this work???

        BigInteger b = new BigInteger(bytes);
        result = b.longValue();
        return result;
    }

    public TPSocket getSocket(){
    	return this.tpSocket;
    }
    
    @Override
    public void run() {
        try {
        	//while(true)
        		receive();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileReceiver.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FileReceiver.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(FileReceiver.class.getName()).log(Level.SEVERE, null, ex);
        }
        // }
    }
}
