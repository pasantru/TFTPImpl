package tftp.Client;
import tftp.Packets.*;
import tftp.FileUsage.FileCreator;
import java.io.IOException;
import java.net.*;
import java.util.Arrays;

public class TFTP_C {
    private static final String SEPARATION = "\t\t\t\t\t\t\t\t\t\t ";
    private static final int MAXSIZE = 520;
    private static final int MAXRETRY = 3;
    private static final int TIMEOUTTIME = 1000;

    private static InetAddress address;
    private static int port;
    private static String filename;
    private static String mode;
    private static String protocol;

    public TFTP_C(InetAddress address, int port, String filename, String mode, String protocol) {
        this.address = address;
        this.port = port;
        this.filename = filename;
        this.mode = mode;
        this.protocol = protocol;

    }

    public void tftp(){
        try{
            switch (protocol){
                case "GET": readRequest();break;
                case "PUT": writeRequest();break;
                default: break;
            }

        }catch (IOException ex){
            //Nothing
        }
    }


    private static void writeRequest(){
        int tries;
        int start_pos;
        int packets_sent = 0, packets_lost = 0;
        String lost = "";
        boolean receivedResponse;
        int packet_block_num = 0;
        DATA d;

        try{
            DatagramSocket socket = new DatagramSocket();
            byte[] bytes_to_send = Factory.returnPacket("WRQ",filename, mode).returnPacketContent();
            DatagramPacket request, receive;

            request = new DatagramPacket(bytes_to_send, bytes_to_send.length, address, port);
            socket.send(request);
            System.out.println(("----> WRQ "  + filename + " " + mode));

//            receive = new DatagramPacket(new byte[MAXSIZE], MAXSIZE);
//            socket.receive(receive);

            do{

                tries = 0;
                receivedResponse = false;

                start_pos = packet_block_num*512;
                d = Factory.returnPacket((short)(packet_block_num), FileCreator.contentsOfFile(filename, start_pos));
                bytes_to_send = d.returnPacketContent();

                do {

                    socket.setSoTimeout(TIMEOUTTIME);

                    try {

                        receive = new DatagramPacket(new byte[MAXSIZE], MAXSIZE);
                        socket.receive(receive);
                        if(checkServer(receive)){
                            receivedResponse = true;
                            lost = "";
                        }
                        if(compareOpcode(receive.getData(), packet_block_num).equals("ERROR")){
                            sendError(socket, 04, "Packet incorrectly formed.");
                            System.out.println(SEPARATION + "ERROR ---->");
                            receivedResponse = false;
                            break;

                        }

                    } catch (SocketTimeoutException e) {
                        tries++;
                        packets_lost++;
                        lost = "(R) ";
                    }

                    request = new DatagramPacket(bytes_to_send, bytes_to_send.length, address, port);
                    socket.send(request);
                    System.out.println(("----> DATA " + lost + (packet_block_num) + " " + (bytes_to_send.length-4) + "bytes"));

                    packets_sent++;


                } while ((!receivedResponse) && (tries < MAXRETRY));

                if(!receivedResponse) break;
                packet_block_num++;
                socket.setSoTimeout(0);


            }while(d.getData().length >= 512);
            System.out.println(packets_lost + "Packets lost, " + (packets_sent-packets_lost) + "Packets retransmitted");

        }catch(IOException ex){ex.printStackTrace();}
    }


    /*
     *  This method is done. The only other thing left to do is the random lost packets which will be left to decide if doing
     *  another method or just implementing it using a boolean which toggles the feature.
     *
     */


    private static void readRequest() throws IOException{
        int packets_lost = 0, packets_sent = 0;
        boolean exit = false;
        int tries;
        int packet_block_num = 1;
        String lost = "";

        boolean receivedResponse;
        byte [] buffer = new byte[0];
        DATA d;
        DatagramSocket socket = new DatagramSocket();
        DatagramPacket request,
                receive = null;

        try{
            byte[] bytes_to_send = Factory.returnPacket("RRQ",filename, mode).returnPacketContent();

            request = new DatagramPacket(bytes_to_send, bytes_to_send.length, address, port);
            socket.send(request);
            System.out.println(("\n\n----> RRQ " + lost + filename + " " + mode));


            do{

                tries = 0;
                receivedResponse = false;


                do{
                    socket.setSoTimeout(TIMEOUTTIME);

                    try {

                        receive = new DatagramPacket(new byte[MAXSIZE], MAXSIZE);
                        socket.receive(receive);


                        if(compareOpcode(receive.getData(), packet_block_num).equals("ERROR")){
                            sendError(socket,01,"Terminate Connection");
                            System.out.println(SEPARATION + "ERROR ---->");
                            exit = true;
                            receivedResponse = false;
                            break;
                        }

                        if(checkServer(receive)) {
                            receivedResponse = true;
                            lost = "";
                            request = new DatagramPacket(bytes_to_send, bytes_to_send.length, address, port);
                            socket.send(request);
                            packets_sent++;
                            System.out.println("----> ACK " + lost + new DATA(receive.getData()).getBlockNum());
                        }


                    } catch (SocketTimeoutException e) {
                        tries++;
                        packets_lost++;
                        lost = "(R) ";
                    }catch (PacketFormatException ex){
                        sendError(socket, 04, "Packet incorrectly formed.");
                        System.out.println(SEPARATION + "ERROR ---->");
                        receivedResponse = false;
                        break;
                    }
                }while((!receivedResponse) && (tries < MAXRETRY));

                if(receivedResponse){
                    d = new DATA(receive.getData());
                    buffer = joinByteArrays(buffer, d.getData());
                    packet_block_num++;
                    bytes_to_send = Factory.returnPacket((short)packet_block_num).returnPacketContent();
                    socket.setSoTimeout(0);
                } else break;

            }while(!exit && d.getData().length >= 512);

            if(receivedResponse){
                FileCreator.createFileFromContentsBin(filename, buffer);

                System.out.println("\n\nContents of the transmission: \n\n");
                if(mode.equals("ascii"))
                    System.out.println(new String(buffer));
                System.out.println("\n\n" + packets_lost + " Packets lost, " + packets_sent + " Packets sent\n");
            }else{
                System.out.println("The connection has failed unexpectedly");
            }
        }catch (PacketFormatException ex){
            sendError(socket, 04, "Packet incorrectly formed.");
        }catch (IOException ex){
            ex.printStackTrace();
        }
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

    /**
     *
     * @param receive DatagramPacket to send
     *
     */

    private static boolean checkServer(DatagramPacket receive){
        return receive.getPort() == port && receive.getAddress().equals(address);
    }


    /** This function prints a user friendly view of the communication between the server and the client
     *
     * @param buff the contents of the packet
     * @param packet_block_num the block num currently sent by the client
     *
     */
    private static String compareOpcode(byte[] buff, int packet_block_num) {
        try{
            if(buff[0]==0 && buff[1]==3){
                DATA data = new DATA(buff);
                if((int)data.getBlockNum()==packet_block_num){
                    System.out.println((SEPARATION + "<---- DATA " + packet_block_num + " " + (data.getData().length) + "bytes"));
                    return "DATA";
                }else return "ERROR";

            }else if(buff[0]==0 && buff[1]==4){
                ACK ack = new ACK(buff);
                if((int)ack.getBlocknum()==packet_block_num) {
                    System.out.println((SEPARATION + "<---- ACK " + ack.getBlocknum()));
                    return "ACK";
                }else return "ERROR";

            } else if(buff[0]==0 && buff[1]==5){
                    ERROR error = new ERROR(buff);
                    System.out.println((SEPARATION + "<---- ERROR " + error.getErrorCode() + " " + error.getErrMesg()));
                    return "ERROR";
            }
        }catch (PacketFormatException ex){
            //Nothing
        }
        return "ERROR";
    }

    private static byte[] joinByteArrays(byte[] array1, byte[] array2){
        byte[] joinedArray = Arrays.copyOf(array1, array1.length + array2.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }
}
