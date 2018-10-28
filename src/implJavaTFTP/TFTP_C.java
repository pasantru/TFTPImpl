package implJavaTFTP;
import packets.ACK;
import packets.DATA;
import packets.ERROR;
import packets.Factory;
import res.FileCreator;
import res.PacketException;

import java.io.File;
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
        if(mode.equals("netascii")) contents = FileCreator.contentsOfFileText(filename);
        else if (mode.equals("octet")) contents = FileCreator.contentsOfFile(filename);
    }

    public void tftp(){
        int tries = 0;
        boolean receivedResponse = false;
        byte[] buff = new byte[MAXSIZE];
        Factory factory = new Factory();


        switch (protocol){
            case "put": File file = new File(filename);
                        try{
                            DatagramSocket socket = new DatagramSocket();

                        }catch(IOException ex){
                            ex.printStackTrace();
                        }


                        break;

            case "get":
                        break;
        }

        //TODO put



        //TODO get


    }
    private void method_get(){

    }


    private void method_put(){
        int tries = 0;
        String packet_type;
        boolean receivedResponse = false,
                    moar_data = true;
        Factory factory = new Factory();
        int start_pos = 0,
                end_pos = 0;
        try{
            int packet_block_num = 1;
            byte[] bytes_to_send = factory.returnPacket("WRQ",filename, mode).returnPacketContent();
            packet_type = "WRQ";
            byte[] buff;
            DatagramSocket socket = new DatagramSocket();
            DatagramPacket request,
                            receive;

            do {
                tries = 0;

                do {
                    request = new DatagramPacket(bytes_to_send, bytes_to_send.length, address, port);
                    socket.send(request);
                    if(packet_type.equals("WRQ")) System.out.println(("----> WRQ " + filename + " " + mode));
                    else if(packet_type.equals("DATA")) System.out.println(("----> DATA " + packet_block_num + " " + (end_pos-start_pos) + "bytes"));
                    socket.setSoTimeout(TIMEOUTTIME);

                    try {
                        receive = new DatagramPacket(new byte[MAXSIZE], MAXSIZE);
                        socket.receive(receive);


                        /*TODO ERROR packet
                                here if the packet recieved is from an unknown source it will send an error packet, then delete the fucking Exception.
                                but check if it is sent */
                        if (receive.getPort() != port && receive.getAddress() != address){
                            bytes_to_send = new ERROR((short) 05, "The packet recieved is from an unknown source.").returnPacketContent();
                            request = new DatagramPacket(bytes_to_send, bytes_to_send.length, address, port);
                            socket.send(request);
                        }

                        receivedResponse = true;
                        buff = receive.getData();
                        compareOpcode(buff, packet_block_num);

                    } catch (SocketTimeoutException e) {
                        tries++;
                        System.out.println("Timeout, " + (MAXRETRY - tries) + "left.");
                    }

                    //TODO file storing and showing on the screen idk
                } while ((!receivedResponse) && (tries < MAXRETRY));

                //TODO the file must be sent piece by piece
                start_pos = packet_block_num*512;
                if(start_pos>contents.length)                       moar_data = false;
                if((packet_block_num*512+512) < contents.length)    end_pos = packet_block_num*512+512;
                else                                                end_pos = contents.length-1;
                bytes_to_send = new DATA((short)packet_block_num, Arrays.copyOfRange(contents, start_pos, end_pos)).returnPacketContent();

                socket.setSoTimeout(0);
                receivedResponse = false;

            } while(moar_data);

            //TODO print the packets lost and the packets sent
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }

    /** This function prints a user friendly view of the communication between the server and the client
     *
     * @param buff the contents of the packet
     * @param packet_block_num the block numm currently sernt by the client
     *
     */
    private void compareOpcode(byte [] buff, int packet_block_num){
        if(buff[0]==0 && buff[1]==4){
            ACK ack = new ACK(buff);
            if(ack.getBlocknum()==(short)packet_block_num)        System.out.println((SEPARATION + "<---- ACK " + "3"));
            //TODO else wrong block num ??
        } else if(buff[0]==0 && buff[1]==5){
            ERROR error = new ERROR(buff);
            System.out.println((SEPARATION + "<---- ERROR " + error.getErrorCode() + " " + error.getErrMesg()));
        }
    }

}
