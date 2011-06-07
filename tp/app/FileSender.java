package tp.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import tp.link.Frame;
import tp.trans.TPSocket;
import tp.trans.Trans;

/**
 *
 * @author STUDENT\s1012886
 */
public class FileSender {

    private static final int MAX_SEGMENT_DATA = 96;

    private Trans trans;
    //private TPSocket tpSocket;
    private FakeSocket tpSocket;
    private File file;
    private FileInputStream fis;
    private FileOutputStream fos;

    public FileSender(int address, int srcPort, int dstPort) {
        trans = Trans.getTrans();
        //trans.start();
        //tpSocket = trans.createSocket(address, srcPort, dstPort);
        tpSocket = new FakeSocket(true);
    }

    public void send(String fileName) {

        file = new File(fileName);
        byte[] header = createHeader();
        try {
            /*int ptr = 0;
            while(ptr < header.length) {

            }*/

            byte[] writeData = new byte[MAX_SEGMENT_DATA];
            boolean hasWritten = true;
            int i, j;
            for(i = 0, j = 0;j < header.length; i++, j++) {
                writeData[i] = header[j];
                if(i == MAX_SEGMENT_DATA-1 && hasWritten) {
                    //hasWritten = tpSocket.writeIn(writeData);
                    hasWritten = tpSocket.writeOut(writeData);
                    writeData = new byte[MAX_SEGMENT_DATA];
                    System.out.println("DATA: " + Frame.toBinaryString(writeData));
                    i = 0;
                }
            }

            fis = new FileInputStream(file);
            byte[] bytes = new byte[MAX_SEGMENT_DATA - i];
            int dataRead = fis.read(bytes);

            for(j = 0; j < bytes.length; i++, j++) {
                writeData[i] = bytes[j];
                if(i == MAX_SEGMENT_DATA-1 && hasWritten) {
                    hasWritten = tpSocket.writeOut(writeData);
                    //hasWritten = tpSocket.writeIn(writeData);
                    System.out.println("DATA: " + Frame.toBinaryString(writeData));
                    break;
                }
            }

            //bytes = new byte[MAX_SEGMENT_DATA]; // klopt dit??
            while(dataRead != -1 && hasWritten) {
                dataRead = fis.read(bytes);
                System.out.println("bytes read: " + dataRead);
                hasWritten = tpSocket.writeOut(bytes);
                //hasWritten = tpSocket.writeIn(bytes);
                System.out.println("DATA: " + Frame.toBinaryString(bytes));
                System.out.println("hasWritten: " + hasWritten);
            }
        }
        catch (FileNotFoundException ex) {
            System.out.println("ERROR: File not found ( " + fileName + ")");
        }
        catch (IOException ex) {
            System.out.println("ERROR in reading data");
        }
                System.out.println("niet veel data");
    }

    private void sendBytes(byte[] bytes) {

    }

    private byte[] createHeader() {
        byte fileNameLength = (byte)file.getName().length();
        byte[] fileName = file.getName().getBytes();
        byte[] fileLength = longToBytes(file.length());
        System.out.println("FILE_LENGTH: " + file.length());
        System.out.println("Length: " + fileLength);
        System.out.println("FILE_NAME: " + Frame.toBinaryString(fileName));

        byte[] header = new byte[1 + fileNameLength + fileLength.length];
        header[0] = fileNameLength;
        for(int i = 1, j = 0; i < fileNameLength+1 && j < fileNameLength; i++, j++) {
            header[i] = fileName[j];

        }
        for(int i = fileNameLength+1, j = 0; i < header.length && j < fileLength.length; i++, j++) {
            header[i] = fileLength[j];
        }
        System.out.println("FILE_LENGTH_BINARY: " + Frame.toBinaryString(fileLength));
        System.out.println("HEADER: " + Frame.toBinaryString(header));
        return header;
    }


    public byte[] longToBytes(long in) {
        byte[] result = new byte[8];

        result[0] = (byte)(in >>> 56);
        result[1] = (byte)(in >>> 48);
        result[2] = (byte)(in >>> 40);
        result[3] = (byte)(in >>> 32);
        result[4] = (byte)(in >>> 24);
        result[5] = (byte)(in >>> 16);
        result[6] = (byte)(in >>>  8);
        result[7] = (byte)(in >>>  0);
        System.out.println(Frame.toBinaryString(result) + " result");
        return result;
    }

    public long bytesToLong(byte[] bytes) {
        long result = 0;

//        for (int i = 0; i < 8; i++) {
//            result |= bytes[i];
//            result <<= 8;
//        }

        result += ((long)bytes[0] << 56);
        result += ((long)bytes[1] << 48);
        result += ((long)bytes[2] << 40);
        result += ((long)bytes[3] << 32);
        result += ((long)bytes[4] << 24);
        result += ((long)bytes[5] << 16);
        result += ((long)bytes[6] << 8);
        result += ((long)bytes[7] << 0);
        //System.out.println(Frame.toBinaryString(bytes) + " bytes");
        System.out.println(Long.toBinaryString(result) + " result");

        return result;
    }

    public static void main(String[] args) {
        FileSender f = null;

        if(args.length == 4) {
            try{
                f = new FileSender(Integer.parseInt(args[0]), Integer.parseInt(args[1]),
                    Integer.parseInt(args[2]));
            }
            catch(NumberFormatException nfe) {
                System.out.println("ERROR: Wrong arguments");
                System.out.println("Number format exception");
            }
            f.send(args[3]);
        }
        else {
            System.out.println("ERROR: Wrong arguments");
        }
        long test = 9603727748442390L;
        System.out.println(test + "--> original");
        System.out.println(f.bytesToLong(f.longToBytes(test)) + "--> should be the same");
    }
}
