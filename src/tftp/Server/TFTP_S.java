package tftp.Server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.*;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileAlreadyExistsException;
import java.util.Arrays;

import tftp.Packets.*;
import tftp.FileUsage.FileCreator;

public class TFTP_S {
    private static final int        PORT = 6790;
    private static final int        MAXSIZE = 516;
    private static final int        TIMEOUTTIME = 1000;
    private static final int        MAXRETRY = 3;
    private static InetAddress      address;
    private static int              port;
    private static SocketAddress    socket_address;

    public static void main(String argv[]){
        System.out.println("Started the server.");
        try{
            DatagramSocket socket = new DatagramSocket(PORT);

            while(true){
                try {
                    DatagramPacket receive;
                    byte[] buff;
                    receive = new DatagramPacket(new byte[MAXSIZE], MAXSIZE);
                    System.out.println("Waiting a request.");
                    socket.receive(receive);
                    buff = receive.getData();
                    address = receive.getAddress();
                    port = receive.getPort();
                    socket_address = receive.getSocketAddress();
                    System.out.println(buff[0] + buff[1]);
                    RRQ_WRQ packet = new RRQ_WRQ(receive.getData());
                    new File(packet.getFilename());
                    System.out.println("address: " + address + ", port: " + port + " packet: " + packet.toString());

                    if (compareOpcode(buff).equals("RRQ")) {

                        System.out.println("Recieved a ReadRequest.");
                        readRequest(socket, receive);

                    } else if (compareOpcode(buff).equals("WRQ")) {

                        System.out.println("Received a WriteRequest.");
                        writeRequest(socket, receive);

                    }

                }catch (FileNotFoundException ex){
                    sendError(socket, 01, "File not found on the server.");

                }catch (AccessDeniedException ex) {
                    sendError(socket, 02, "Access Violation.");

                }catch(PacketFormatException ex){
                    sendError(socket, 04, "Packet incorrectly formed.");

                }catch (FileAlreadyExistsException ex){
                    sendError(socket, 06,"The file already exists in the server.");

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }catch(SocketException ex){
            ex.printStackTrace();
        }
    }


    /*
     *  This method is done the only other thing to do is the random lost packets witch will be left to decide if doing
     *  another method or just implementing it using a boolean which toggles the feature.
     */

    private static void readRequest(DatagramSocket socket, DatagramPacket receive){
        try {
            System.out.println("Ongoing ReadRequest.");
            int tries;
            byte[] bytes_to_send;
            boolean receivedResponse;
            String filename;
            RRQ_WRQ packet = new RRQ_WRQ(receive.getData());
            filename = packet.getFilename();

            int packet_num = 0;
            int start_pos;
            DATA d;
            DatagramPacket send;

            do {
                tries = 0;
                receivedResponse = false;
                start_pos = packet_num * 512;
                d = Factory.returnPacket((short) (packet_num + 1), FileCreator.contentsOfFile(filename, start_pos));
                bytes_to_send = d.returnPacketContent();
                do {
                    send = new DatagramPacket(bytes_to_send, bytes_to_send.length, address, port);
                    socket.setSoTimeout(TIMEOUTTIME);
                    socket.send(send);

                    try {
                        receive = new DatagramPacket(new byte[MAXSIZE], MAXSIZE);
                        socket.receive(receive);
                        receivedResponse = true;
                    } catch (SocketTimeoutException ex) {
                        tries++;
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                } while ((!receivedResponse) && (tries < MAXRETRY));

                socket.setSoTimeout(0);
                if (!receivedResponse) break;
                if (checkClient(receive)) {
                    System.out.println(("Client OK"));
                    packet_num++;
                }

            } while (d.getData().length == 512);
        }catch(PacketFormatException ex){
            sendError(socket, 04, "Packet incorrectly formed.");
        }catch(IOException ex){
            ex.printStackTrace();
        }

    }


    //TODO check pliz the write Request
    private static void writeRequest(DatagramSocket socket, DatagramPacket receive){
        try {
            DatagramPacket send;
            byte[] bytes_to_send;
            byte[] buffer = new byte[0];
            boolean receivedResponse,
                        exit = false;
            String filename;
            RRQ_WRQ packet = new RRQ_WRQ(receive.getData());
            filename = packet.getFilename();

            int tries,
                packet_num = 0;
            DATA d;

            do {
                tries = 0;
                receivedResponse = false;
                bytes_to_send = Factory.returnPacket((short) packet_num).returnPacketContent();

                do {
                    socket.setSoTimeout(TIMEOUTTIME);

                    try {
                        send = new DatagramPacket(bytes_to_send, bytes_to_send.length, receive.getAddress(), receive.getPort());
                        socket.send(send);

                        receive = new DatagramPacket(new byte[MAXSIZE], MAXSIZE);
                        socket.receive(receive);
                        receivedResponse = true;

                        /*  Checks the condition that the packet is from the same address and the same port that the initial
                         *  connection otherwise it will completely ignore the packet and continue with the execution */
                        if (checkClient(receive) && checkOrderPackets(packet_num, (int) new DATA(receive.getData()).getBlockNum())) {
                            d = new DATA(receive.getData());
                            buffer = joinByteArrays(buffer, d.getData());
                            packet_num++;
                        }else{
                            exit = true;
                            break;
                        }

                        if (d.getData().length < 512) {
                            exit = true;
                            break;
                        }

                    } catch (SocketTimeoutException e) {
                        tries++;
                        System.out.println("Timeout, " + (MAXRETRY - tries) + "left.");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                } while ((!receivedResponse) && (tries < MAXRETRY));

                socket.setSoTimeout(0);
            } while (!exit && tries < MAXRETRY);
            if (receivedResponse) FileCreator.createFileFromContentsBin(filename, buffer);
        }catch(PacketFormatException ex){
            sendError(socket, 04, "Packet incorrectly formed.");
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }

    private static boolean checkOrderPackets(int current_packet_num, int packet_num){
        return current_packet_num==packet_num;
    }




    private static boolean checkClient(DatagramPacket receive){
        return receive.getSocketAddress().equals(socket_address);
    }

    private static void sendError(DatagramSocket socket, int errCode, String message){
        try{
            byte[] bytes_to_send = Factory.returnPacket((short) errCode, message).returnPacketContent();
            DatagramPacket request = new DatagramPacket(bytes_to_send, bytes_to_send.length, address, port);
            socket.send(request);
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }

    private static String compareOpcode(byte[] buff){
        if(buff[0]==0 && buff[1]==1)        return "RRQ";
        else if(buff[0]==0 && buff[1]==2)   return "WRQ";
        else if(buff[0]==0 && buff[1]==3)   return "DATA";
        else if(buff[0]==0 && buff[1]==4)   return "ACK";
        else if(buff[0]==0 && buff[1]==5)   return "ERROR";
                                            return "";
    }

    private static byte[] joinByteArrays(byte[] array1, byte[] array2){
        byte[] joinedArray = Arrays.copyOf(array1, array1.length + array2.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }



}
