package tp.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import tp.trans.TPSocket;
import tp.trans.Trans;
import tp.util.Log;

/**
 * Responsible for reading data from the socket
 * @author STUDENT\s1012886
 */
public class FileReceiver extends Thread {

    private static final int MAX_SEGMENT_DATA = 96;
    private Trans trans;
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
        trans = Trans.getTrans();
        tpSocket = trans.createSocket(address, srcPort, dstPort);
    }

    public FileReceiver(int address, int srcPort, int dstPort, FileSender sender) {
        trans = Trans.getTrans();
        tpSocket = sender.getSocket();
    }

    /**
     * Reads data from the socket and writes it to a file
     */
    public void receive() throws FileNotFoundException, IOException, InterruptedException {
        byte[] bytesIn = null;
        while (bytesIn == null) {    // wait until tpSocket returns data
            bytesIn = tpSocket.readIn();
        }
        System.out.println("bytesIN: " + bytesIn);
        int fileNameLength = (int) bytesIn[0];
        byte[] fileName = new byte[fileNameLength];

        // read fileName from tpSocket
        // fileName might be longer than 1 tl-segment
        int i, j;
        for (i = 1  , j = 0; j < fileNameLength; i++, j++) {
            fileName[j] = bytesIn[i];
            if (i == MAX_SEGMENT_DATA - 1) {
                do {
                    Thread.sleep(5);
                    bytesIn = tpSocket.readIn();
                    Log.writeLog(" FileReceiver", new String(bytesIn) + "\n--------SECOND DATA----------------", false);
                    i = 0;
                } while (bytesIn == null);
            }
        }

        // read fileLength from tpSocket
        byte[] fileLength = new byte[8];
        for (j = 0; j < 8; i++, j++) {
            fileLength[j] = bytesIn[i];
            if (i == MAX_SEGMENT_DATA - 1) {
                do {
                    Thread.sleep(5);
                    synchronized (tpSocket.getINLOCK()) {
                        bytesIn = tpSocket.readIn();
                      //  FileSender.NOTIFY = true;
                    }
                    Log.writeLog(" FileReceiver", new String(bytesIn) + "\n--------THIRD DATA----------------", false);
                    i = 0;
                } while (bytesIn == null);
            }
        }
        System.out.println("Filelength received: " + bytesToLong(fileLength));

        // read (first) data that is left from the segment containing header info
        byte[] firstData = new byte[MAX_SEGMENT_DATA - i];
        for (j = 0; i < MAX_SEGMENT_DATA; i++, j++) {
            firstData[j] = bytesIn[i];
        }
        file = new File(new String(fileName));
        //file.createNewFile();
        fos = new FileOutputStream(file);
        fos.write(firstData);

        Log.writeLog(" FileReceiver", new String(firstData) + "\n--------FIRST DATA----------------", false);

        // keep reading data from tpSocket
        int dataPtr = firstData.length;
        byte[] read = null;
        long length = bytesToLong(fileLength);
        while (dataPtr < length) {
            read = null;
            while (read == null) {
                Thread.sleep(5);
                synchronized (tpSocket.getINLOCK()) {
                    read = tpSocket.readIn();
                    //FileSender.NOTIFY = true;
                }
            }
            fos.write(read);
            Log.writeLog(" FileReceiver", new String(read) + "\n--------REMAINING DATA----------------", false);
            dataPtr += read.length;
        }
        fos.close();
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

    @Override
    public void run() {
        try {
            //  while(true) {
            receive();
            // }
        } catch (FileNotFoundException ex) {
            System.out.println("ERROR: File not found");
        } catch (IOException ex) {
            System.out.println("Read error");
        } catch (InterruptedException ex) {
            System.out.println("Thread error");
        }
    // }
    }

    public static void main(String[] args) {
        Log.getInstance("FileReceiver");
        FileReceiver f = null;

        if (args.length == 4) {
            try {
                f = new FileReceiver(Integer.parseInt(args[0]), Integer.parseInt(args[1]),
                        Integer.parseInt(args[23]));
            } catch (NumberFormatException nfe) {
                System.out.println("ERROR: Wrong arguments");
                System.out.println("Number format exception");
            }
            try {
                f.receive();
            }
            catch (FileNotFoundException ex) {
                System.out.println("ERROR: File not found");
            }
            catch (IOException ex) {
                System.out.println("Read error");
            }
            catch (InterruptedException ex) {
                System.out.println("Thread error");
            }
        } else {
            System.out.println("ERROR: Wrong arguments");
        }
    }
}
