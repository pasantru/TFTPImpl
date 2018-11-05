package implJavaTFTP;
import packets.ACK;
import packets.DATA;
import packets.ERROR;
import packets.Factory;
import res.FileCreator;
import java.io.IOException;
import java.net.*;
import java.util.Arrays;

public class TFTP_C {
    private static final String SEPARATION = "\t\t\t\t\t\t\t\t\t\t ";
    private static final int MAXSIZE = 512;
    private static final int MAXRETRY = 3;
    private static final int TIMEOUTTIME = 1000;

    private static InetAddress address;
    private static int port;
    private static byte[] contents;
    private static String filename;
    private static String mode;
    private static String protocol;

    public TFTP_C(InetAddress address, int port, String filename, String mode, String protocol) {
        this.address = address;
        this.port = port;
        this.filename = filename;
        this.mode = mode;
        this.protocol = protocol;

        //TODO Question replace err
//        if(mode.equals("ascii")) contents = FileCreator.contentsOfFileText(filename);
//        else if (mode.equals("octet")) contents = FileCreator.contentsOfFile(filename);
    }

    public void tftp(){
        switch (protocol){
            case "put": method_put();break;
            case "get": method_get();break;
        }
    }

    private static void method_get(){
        int tries;
        String packet_type;
        boolean receivedResponse,
                moar_data = true;
        byte [] buffer = new byte[10];
        Factory factory = new Factory();
        DATA d;
        try{
            int packet_block_num = 1;
            byte[] bytes_to_send = factory.returnPacket("RRQ",filename, mode).returnPacketContent();
            packet_type = "RRQ";
            DatagramSocket socket = new DatagramSocket();
            DatagramPacket request,
                    receive = null;

            do{

                tries = 0;
                receivedResponse = false;
                do{
                    //TODO check
                    request = new DatagramPacket(bytes_to_send, bytes_to_send.length, address, port);
                    socket.send(request);
                    if(packet_type.equals("RRQ")) System.out.println(("----> RRQ " + filename + " " + mode));
                    else if(packet_type.equals("ACK")) System.out.println("----> ACK " + packet_block_num);
                    socket.setSoTimeout(TIMEOUTTIME);
                    try {

                        receive = new DatagramPacket(new byte[MAXSIZE], MAXSIZE);
                        socket.receive(receive);
                        checkServer(socket, receive);
                        receivedResponse = true;
                        byte[] buff = receive.getData();
                        compareOpcode(buff, packet_block_num);

                    } catch (SocketTimeoutException e) {tries++;System.out.println("Timeout, " + (MAXRETRY - tries) + "left.");
                    } catch (IOException ex){ex.printStackTrace();}
                    packet_type = "ACK";
                    //TODO file storing and showing on the screen idk
                }while((!receivedResponse) && (tries < MAXRETRY));

                d = new DATA(receive.getData());
                buffer = joinByteArrays(buffer, d.getData());
                socket.setSoTimeout(0);

            }while(moar_data);
            FileCreator.createFileFromContentsBin(filename, buffer);
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }


    private static void method_put(){
        int tries;
        String packet_type;
        boolean receivedResponse,
                    moar_data = true;
        Factory factory = new Factory();
        int start_pos = 0,
                end_pos = 0;
        try{
            int packet_block_num = 1;
            byte[] bytes_to_send = factory.returnPacket("WRQ",filename, mode).returnPacketContent();
            packet_type = "WRQ";
            DatagramSocket socket = new DatagramSocket();
            DatagramPacket request;

            do {
                tries = 0;
                receivedResponse = false;
                request = new DatagramPacket(bytes_to_send, bytes_to_send.length, address, port);

                do {
                    socket.send(request);
                    if(packet_type.equals("WRQ")) System.out.println(("----> WRQ " + filename + " " + mode));
                    else if(packet_type.equals("DATA")) System.out.println(("----> DATA " + packet_block_num + " " + (end_pos-start_pos) + "bytes"));
                    socket.setSoTimeout(TIMEOUTTIME);
                    try {

                        DatagramPacket receive = new DatagramPacket(new byte[MAXSIZE], MAXSIZE);
                        socket.receive(receive);
                        checkServer(socket, receive);
                        receivedResponse = true;
                        byte[] buff = receive.getData();
                        compareOpcode(buff, packet_block_num);

                    } catch (SocketTimeoutException e) {tries++;System.out.println("Timeout, " + (MAXRETRY - tries) + "left.");
                    } catch (IOException ex){ex.printStackTrace();}


                } while ((!receivedResponse) && (tries < MAXRETRY));
                //TODO the file must be sent piece by piece
                if(receivedResponse){
                    packet_type = "DATA";
                    start_pos = packet_block_num*512;

                    if(start_pos > contents.length)                     moar_data = false;
                    if((packet_block_num*512+512) < contents.length)    end_pos = packet_block_num*512+512;
                    else                                                end_pos = contents.length-1;

                    bytes_to_send = new DATA((short)packet_block_num, Arrays.copyOfRange(contents, start_pos, end_pos)).returnPacketContent();
                }

                socket.setSoTimeout(0);

            } while(moar_data);

            //TODO print the packets lost and the packets sent
        }catch(IOException ex){ex.printStackTrace();}
    }

    /**
     *
     * @param socket
     * @param packet_block_num
     */

    private static boolean receivePacket(DatagramSocket socket, int packet_block_num, int tries){
        boolean receivedResponse = false;

        return receivedResponse;
    }

    /**
     *
     * @param socket socket for the transaction
     * @param receive DatagramPacket to send
     *
     */

    private static void checkServer(DatagramSocket socket, DatagramPacket receive){
        Factory factory = new Factory();
        /*  TODO ERROR packet
            here if the packet recieved is from an unknown source it will send an error packet, then delete the fucking Exception.
            but check if it is sent */
        try{
            if (receive.getPort() != port && receive.getAddress() != address){
                byte[] bytes_to_send = factory.returnPacket((short) 05, "The packet recieved is from an unknown source.").returnPacketContent();
                DatagramPacket request = new DatagramPacket(bytes_to_send, bytes_to_send.length, address, port);
                socket.send(request);
            }
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }


    /** This function prints a user friendly view of the communication between the server and the client
     *
     * @param buff the contents of the packet
     * @param packet_block_num the block num currently sent by the client
     *
     */
    private static void compareOpcode(byte[] buff, int packet_block_num){
        if(buff[0]==0 && buff[1]==4){
            ACK ack = new ACK(buff);
            if(ack.getBlocknum()==(short)packet_block_num)        System.out.println((SEPARATION + "<---- ACK " + packet_block_num));
            //TODO else wrong block num ??
        } else if(buff[0]==0 && buff[1]==5){
            ERROR error = new ERROR(buff);
            System.out.println((SEPARATION + "<---- ERROR " + error.getErrorCode() + " " + error.getErrMesg()));
        }
    }

    private static byte[] joinByteArrays(byte[] array1, byte[] array2){
        byte[] joinedArray = Arrays.copyOf(array1, array1.length + array2.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }
}
