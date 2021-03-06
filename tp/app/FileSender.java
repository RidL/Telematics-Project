package tp.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import tp.link.Frame;
import tp.trans.SocketTakenException;
import tp.trans.TPSocket;
import tp.trans.Trans;
import tp.trans.UnkownTPHostException;
import tp.util.Log;

/**
 * Is responsible for sending file data on the application layer
 * @author STUDENT\s1012886
 */
public class FileSender {

    private static final int MAX_SEGMENT_DATA = 96;
    private Trans trans;
    private TPSocket tpSocket;
    //private FakeSocket tpSocket;
    private File file;
    private FileInputStream fis;
    // public  static boolean NOTIFY = true;

    /**
     * Creates a new FileSender
     * @param address the address of the receiver
     * @param srcPort the port through which data is sent
     * @param dstPort the port through which data is received at the destination
     */
    public FileSender(int address, int srcPort, int dstPort) {
        trans = Trans.getTrans();
        try {
			tpSocket = trans.createSocket(address, srcPort, dstPort);
		} catch (SocketTakenException e) {
			e.printStackTrace();
		} catch (UnkownTPHostException e) {
			e.printStackTrace();
		}
    }

    /**
     * Sends a file identified by fileName through the socket
     * @param fileName the name of the file to be send
     */
    public void send(String fileName) throws FileNotFoundException, IOException, InterruptedException {
        System.out.println("Sending file...");

        file = new File(fileName);
        byte[] header = createHeader();

        byte[] writeData = new byte[MAX_SEGMENT_DATA];
        int i, j;   // write header to tpSocket
        for (i = 0, j = 0; j < header.length; i++, j++) {
            writeData[i] = header[j];
            if (i == MAX_SEGMENT_DATA - 1) {
                tpSocket.writeOut(writeData);
                writeData = new byte[MAX_SEGMENT_DATA]; // not neccesary but assures no duplicate header data
                i = 0;
            }
        }

        fis = new FileInputStream(file);
        byte[] bytes = new byte[MAX_SEGMENT_DATA - i];
        int dataRead = fis.read(bytes);

        // fill last segment containing header-data up with the first file data
        for (j = 0; j < bytes.length; i++, j++) {
            writeData[i] = bytes[j];
            if (i == MAX_SEGMENT_DATA - 1) {
                tpSocket.writeOut(writeData);
                break;
            }
        }

        bytes = new byte[MAX_SEGMENT_DATA];
        while (dataRead != -1) {
            dataRead = fis.read(bytes);
            tpSocket.writeOut(bytes);
            bytes = new byte[MAX_SEGMENT_DATA];
        }
        System.out.println("End of file");
    }

    /**
     * Creates a new file-tranfer header for the file
     * @return the header as a byte array
     */
    private byte[] createHeader() {
        //  System.out.println("***START HEADER_TEST***");
        byte fileNameLength = (byte) file.getName().length();
        byte[] fileName = file.getName().getBytes();
        byte[] fileLength = longToBytes(file.length());
        //  System.out.println("FILE_LENGTH: " + file.length());
        //  System.out.println("FILE_NAME: " + Frame.toBinaryString(fileName));

        byte[] header = new byte[1 + fileNameLength + fileLength.length];
        header[0] = fileNameLength;
        for (int i = 1, j = 0; i < fileNameLength + 1 && j < fileNameLength; i++, j++) {
            header[i] = fileName[j];

        }
        for (int i = fileNameLength + 1, j = 0; i < header.length && j < fileLength.length; i++, j++) {
            header[i] = fileLength[j];
        }
        //    System.out.println("FILE_LENGTH_BINARY: " + Frame.toBinaryString(fileLength));
        //    System.out.println("HEADER: " + Frame.toBinaryString(header));
        //    System.out.println("***END HEADER_TEST***");
        return header;
    }

    /**
     * Converts a long number to a byte array
     * @param in the number to convert
     * @return the number stored in a byte array
     */
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

    /**
     * Converts a byte array to a long
     * @param bytes the byte array to convert
     * @return the long value of bytes
     */
    public static long bytesToLong(byte[] bytes) {
//        long result = 0;

//        result += ((long) bytes[0] << 56);
//        result += ((long) bytes[1] << 48);
//        result += ((long) bytes[2] << 40);
//        result += ((long) bytes[3] << 32);
//        result += ((long) bytes[4] << 24);
//        result += ((long) bytes[5] << 16);
//        result += ((long) bytes[6] << 8);
//        result += ((long) bytes[7] << 0);
//        Why doesn't this work???

        BigInteger b = new BigInteger(bytes);
        return b.longValue();
    }

    public static void main(String[] args) {
        /*Log.getInstance("FileSender");
        FileSender f = null;

        if (args.length == 4) {
        try {
        f = new FileSender(Integer.parseInt(args[0]), Integer.parseInt(args[1]),
        Integer.parseInt(args[2]));
        } catch (NumberFormatException nfe) {
        System.out.println("ERROR: Wrong arguments");
        System.out.println("Number format exception");
        }
        f.send(args[3]);
        } else {
        System.out.println("ERROR: Wrong arguments");
        }*/

        long test = 9603727748442390L;
        System.out.println(test + "--> original");
        System.out.println(FileSender.bytesToLong(FileSender.longToBytes(test)) + "--> should be the same");
    }

    public TPSocket getSocket() {
        return tpSocket;
    }
}
