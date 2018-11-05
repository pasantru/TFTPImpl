package implJavaTFTP;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.*;
import java.nio.file.FileAlreadyExistsException;
import java.util.Arrays;

import packets.*;
import res.FileCreator;

public class TFTP_S {
    //TODO change the port number
    private static final int PORT = 69;
    private static final int BUFFER_SIZE = 512;
    private static final int MAXRETRY = 3;
    private static final int TIMEOUTTIME = 1000;
    private static InetAddress address;
    private static int port;

    public static void main(String argv[]){
        try {
            DatagramSocket socket = new DatagramSocket(PORT);
            DatagramPacket receive;
            byte [] buff;
            while(true){
                receive = new DatagramPacket(new byte[BUFFER_SIZE], BUFFER_SIZE);
                socket.receive(receive);
                buff = receive.getData();
                address = receive.getAddress();
                port = receive.getPort();
                if(compareOpcode(buff).equals("RRQ"))      readRequest(socket, receive);
                else if(compareOpcode(buff).equals("WRQ")) writeRequest(socket, receive);
            }
        } catch (SocketException e) {e.printStackTrace();
        } catch (FileAlreadyExistsException ex){
            //TODO send error
            ex.printStackTrace();
        } catch (FileNotFoundException ex){
            //TODO send error packet
            ex.printStackTrace();
        }catch (IOException e) {e.printStackTrace();}
    }

    private static void readRequest(DatagramSocket socket, DatagramPacket receive) throws IOException{
        Factory f = new Factory();
        byte [] buff,
                bytes_to_send;
        boolean receivedResponse = false;
        String filename,
                mode;
        RRQ_WRQ packet = new RRQ_WRQ(receive.getData());
        filename = packet.getFilename();
        mode = packet.getMode();

        int packet_num = 0;
        byte[] buffer = new byte[BUFFER_SIZE];
        DATA d = null;
        //TODO FINISH


    }

    private static void writeRequest(DatagramSocket socket, DatagramPacket receive) throws IOException{
        DatagramPacket send;
        Factory f = new Factory();
        byte [] bytes_to_send;
        boolean receivedResponse;
        String filename;
        RRQ_WRQ packet = new RRQ_WRQ(receive.getData());
        filename = packet.getFilename();

        int packet_num = 0;
        byte[] buffer = new byte[BUFFER_SIZE];
        DATA d = null;

        do{
            int tries = 0;
            bytes_to_send = f.returnPacket((short)packet_num).returnPacketContent();

            do{
                send = new DatagramPacket(bytes_to_send, bytes_to_send.length, receive.getAddress(), receive.getPort());
                socket.send(send);
                socket.setSoTimeout(TIMEOUTTIME);
                receivedResponse = false;

                try {
                    receive = new DatagramPacket(new byte[BUFFER_SIZE], BUFFER_SIZE);
                    socket.receive(receive);
                    receivedResponse = true;


                    /*  Checks the condition that the packet is from the same address and the same port that the initial
                     *  connection otherwise it will completely ignore the packet and continue with the execution */
                    if(address.equals(receive.getAddress()) && port == receive.getPort()){
                        d = new DATA(receive.getData());
                        buffer = joinByteArrays(buffer, d.getData());
                        packet_num++;
                    }

                } catch (SocketTimeoutException e) {tries++;System.out.println("Timeout, " + (BUFFER_SIZE - tries) + "left.");
                } catch (IOException ex){ex.printStackTrace();}

            }while ((!receivedResponse) && (tries < MAXRETRY));

            socket.setSoTimeout(0);


        }while(d.getData().length==BUFFER_SIZE);

        FileCreator.createFileFromContentsBin(filename, buffer);
    }

    private static String compareOpcode(byte[] buff){
        if(buff[0]==0 && buff[1]==1) return "RRQ";
        else if(buff[0]==0 && buff[1]==2) return "WRQ";
        return "";
    }

    private static byte[] joinByteArrays(byte[] array1, byte[] array2){
        byte[] joinedArray = Arrays.copyOf(array1, array1.length + array2.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }



}
