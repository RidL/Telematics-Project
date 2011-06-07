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
import tp.trans.TPSocket;
import tp.trans.Trans;
import tp.util.Log;

/**
 *
 * @author STUDENT\s1012886
 */
public class FileSender {

    private static final int MAX_SEGMENT_DATA = 96;
    private Trans trans;
    private TPSocket tpSocket;
    //private FakeSocket tpSocket;
    private File file;
    private FileInputStream fis;

    public FileSender(int address, int srcPort, int dstPort) {
        trans = Trans.getTrans();
        tpSocket = trans.createSocket(address, srcPort, dstPort);
    //tpSocket = new FakeSocket(true);
    }

    public void send(String fileName) {
        file = new File(fileName);
        byte[] header = createHeader();
        try {
            byte[] writeData = new byte[MAX_SEGMENT_DATA];
            int i, j;   // write header to tpSocket
            for (i = 0, j = 0; j < header.length; i++, j++) {
                writeData[i] = header[j];
                if (i == MAX_SEGMENT_DATA - 1) {
                    int temp = 0;
                    //hasWritten = tpSocket.writeIn(writeData);
                    boolean suc;
                    do {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(FileSender.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        suc = tpSocket.writeOut(writeData);
                  //      System.out.println("number of bytes written: " + writeData.length);
                        temp++;
                        if ((temp % 100) == 0) {
                            System.out.println("trying to write to socket1");
                        }
                    } while (!suc);
                    writeData = new byte[MAX_SEGMENT_DATA]; // not neccesary but assures no duplicate header data
                    // System.out.println("DATA: " + Frame.toBinaryString(writeData));
                    i = 0;
                }
            }

            fis = new FileInputStream(file);
            byte[] bytes = new byte[MAX_SEGMENT_DATA - i];
            int dataRead = fis.read(bytes);
            System.out.println("dataRead: " + dataRead);

            // fill last segment with header-data up with the first real data
            for (j = 0; j < bytes.length; i++, j++) {
                writeData[i] = bytes[j];
                if (i == MAX_SEGMENT_DATA - 1) {
                   
                    boolean suc;
                    do {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(FileSender.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        suc = tpSocket.writeOut(writeData);
                    //    System.out.println("number of bytes written: " + writeData.length);

                        
                            System.out.println("trying to write to socket2");
                        
                    } while (!suc);
                    //hasWritten = tpSocket.writeIn(writeData);
                    // System.out.println("DATA: " + Frame.toBinaryString(writeData));
                    break;
                }
            }
            //fis.close();

            bytes = new byte[MAX_SEGMENT_DATA];
            while (dataRead != -1) {
                int temp = 0;
                dataRead = fis.read(bytes);
                System.out.println("bytes read: " + dataRead);
                boolean suc;
                do {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(FileSender.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    suc = tpSocket.writeOut(bytes);
                //    System.out.println("number of bytes written: " + bytes.length);
                    temp++;
                    if ((temp % 100) == 0) {
                        System.out.println("trying to write to socket3");
                    }
                } while (!suc);
            //hasWritten = tpSocket.writeIn(bytes);
            //System.out.println("DATA: " + Frame.toBinaryString(writeData));
            }
        } catch (FileNotFoundException ex) {
            System.out.println("ERROR: File not found ( " + fileName + ")");
        } catch (IOException ex) {
            System.out.println("ERROR in reading data");
        }
        System.out.println("niet veel data");
    }

    private void sendBytes(byte[] bytes) {
    }

    private byte[] createHeader() {
        System.out.println("HEADER_TEST***");
        byte fileNameLength = (byte) file.getName().length();
        byte[] fileName = file.getName().getBytes();
        byte[] fileLength = longToBytes(file.length());
        System.out.println("FILE_LENGTH: " + file.length());
        System.out.println("Length: " + fileLength);
        System.out.println("FILE_NAME: " + Frame.toBinaryString(fileName));

        byte[] header = new byte[1 + fileNameLength + fileLength.length];
        header[0] = fileNameLength;
        for (int i = 1, j = 0; i < fileNameLength + 1 && j < fileNameLength; i++, j++) {
            header[i] = fileName[j];

        }
        for (int i = fileNameLength + 1, j = 0; i < header.length && j < fileLength.length; i++, j++) {
            header[i] = fileLength[j];
        }
        System.out.println("FILE_LENGTH_BINARY: " + Frame.toBinaryString(fileLength));
        System.out.println("HEADER: " + Frame.toBinaryString(header));
        System.out.println("END OF HEADER_TEST***");
        return header;
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
        result[7] = (byte) (in >>> 0);

        System.out.println(Frame.toBinaryString(result) + " result");
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
        System.out.println(Long.toBinaryString(result) + " result");


        return result;
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
