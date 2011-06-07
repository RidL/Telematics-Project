/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

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
public class FileTransfer {

    private static final int MAX_SEGMENT_DATA = 96;
    
    Trans trans;
    TPSocket tpSocket;
    File file;
    FileInputStream fis;
    FileOutputStream fos;

    public FileTransfer(int address, int srcPort, int dstPort) {
        trans = Trans.getTrans();
        trans.start();
        tpSocket = trans.createSocket(address, srcPort, dstPort);

    }

    public void send(String fileName) {

        file = new File(fileName);
        byte[] header = createHeader();
        try {
            /*int ptr = 0;
            while(ptr < header.length) {

            }*/

            byte[] writeData = new byte[MAX_SEGMENT_DATA];
            int i, j;
            for(i = 0, j = 0;j < header.length; i++, j++) {
                writeData[i] = header[j];
                if(i == MAX_SEGMENT_DATA-1) {
                    tpSocket.writeOut(writeData);
                    writeData = new byte[MAX_SEGMENT_DATA];
                    i = 0;
                }
            }

            fis = new FileInputStream(file);
            byte[] bytes = new byte[MAX_SEGMENT_DATA - i];
            int dataRead = fis.read(bytes);

            for(j = 0; j < bytes.length; i++, j++) {
                writeData[i] = bytes[j];
                if(i == MAX_SEGMENT_DATA-1) {
                    tpSocket.writeOut(writeData);
                    break;
                }
            }
            
            while(dataRead != -1) {
                dataRead = fis.read(bytes);
                //System.out.println("bytes read: " + dataRead);
                tpSocket.writeOut(bytes);
            }
        }
        catch (FileNotFoundException ex) {
            System.out.println("ERROR: File not found ( " + fileName + ")");
        }
        catch (IOException ex) {
            System.out.println("ERROR in reading data");
        }
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

    public void receive() {
        byte[] bytesIn = tpSocket.readIn();
        int fileNameLength = (int)bytesIn[0];
        byte[] fileName = new byte[fileNameLength];
        int i, j;
        for(i = 1, j = 0; j < fileNameLength; i++, j++) {
            fileName[j] = bytesIn[i];
            if(i == MAX_SEGMENT_DATA-1) {
                bytesIn = tpSocket.readIn();
                i = 0;
            }
        }
        byte[] fileLength = new byte[8];
        for(j = 0; j < 8; i++, j++) {
            fileName[j] = bytesIn[i];
            if(i == MAX_SEGMENT_DATA-1) {
                bytesIn = tpSocket.readIn();
                i = 0;
            }
        }
        byte[] firstData = new byte[MAX_SEGMENT_DATA - i];
        for(j = 0; i < MAX_SEGMENT_DATA; i++, j++) {
            firstData[j] = bytesIn[i];
        }

        try {
            file = new File("output.data");
            file.createNewFile();
            fos = new FileOutputStream(file);
            fos.write(firstData);

            int dataPtr = firstData.length;
            while(dataPtr < bytesToLong(fileLength)) {
                fos.write(tpSocket.readIn());
                dataPtr += MAX_SEGMENT_DATA;
            }
        }
        catch(FileNotFoundException ex) {
            
        }
        catch(IOException ioe) {

        }
    }

    public byte[] longToBytes(long in) {
        byte[] writeBuffer = new byte[8];

        writeBuffer[0] = (byte)(in >>> 56);
        writeBuffer[1] = (byte)(in >>> 48);
        writeBuffer[2] = (byte)(in >>> 40);
        writeBuffer[3] = (byte)(in >>> 32);
        writeBuffer[4] = (byte)(in >>> 24);
        writeBuffer[5] = (byte)(in >>> 16);
        writeBuffer[6] = (byte)(in >>>  8);
        writeBuffer[7] = (byte)(in >>>  0);
        return writeBuffer;
    }

    public long bytesToLong(byte[] bytes) {
        long result = 0;

//        for (int i = 0; i < 8; i++) {
//            result |= bytes[i];
//            result <<= 8;
//        }


        result += (bytes[0] << 56);
        result += (bytes[1] << 48);
        result += (bytes[2] << 40);
        result += (bytes[3] << 32);
        result += (bytes[4] << 24);
        result += (bytes[5] << 16);
        result += (bytes[6] <<  8);
        result += (bytes[7] <<  0);
        System.out.println(Long.toBinaryString(result) + " result");

        return result;
    }

    public static void main(String[] args) {
        FileTransfer f = null;

        if(args.length == 5) {
            try{
                f = new FileTransfer(Integer.parseInt(args[1]), Integer.parseInt(args[2]),
                    Integer.parseInt(args[3]));
            }
            catch(NumberFormatException nfe) {
                System.out.println("ERROR: Wrong arguments");
            }
            if(args[0].equals("-s")) {
                f.send(args[4]);
            }
            else if(args[0].equals("-r")) {
                f.receive();
            }
        }
        else {
            System.out.println("ERROR: Wrong arguments");
        }

        long test = (long)87367243;
        
        System.out.println(Long.toBinaryString(test) + " werthers original");
        System.out.println(f.bytesToLong(f.longToBytes(test)));
    }
}
