package implJavaTFTP;

import packets.DATA;
import packets.Factory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Client {
    private static final int MAXSIZE = 512;
    private static final int MAXRETRY = 3;
    private static final int TIMEOUTTIME = 1000;

    public static void main(String argv[]) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        Factory factory = new Factory();
        InetAddress host = null;
        int port = 69;
        if(argv.length == 1) host = InetAddress.getByName(argv[0]);
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        String command = stdIn.readLine(),
                filename,
                    mode = "netascii";

        while(true){
            if(command.contains("connect")) host = InetAddress.getByName(getContentFromCommand(command,"\\b\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\b"));
            else if(command.contains("mode")) mode = getContentFromCommand(command, "[a-z]+");
            else if(command.contains("put")) {
                filename = getContentFromCommand(command, "[a-zA-Z-_]+[.][a-z]{3}");
                File file = new File(filename);
                //TODO finish the put code

            } else if(command.contains("get")){
                filename = getContentFromCommand(command, "[a-zA-Z-_]+[.][a-z]{3}");
                byte [] bytes_to_send = factory.returnPacket("WRQ",filename, mode).returnPacketContent();
                int tries = 0;
                boolean receivedResponse = false;
                DatagramPacket request,
                                recieve = null;
                DATA data;
                do {
                    do {
                        request = new DatagramPacket(bytes_to_send, bytes_to_send.length, host, port);
                        socket.send(request);
                        socket.setSoTimeout(TIMEOUTTIME);

                        try {
                            recieve = new DatagramPacket(new byte[MAXSIZE], MAXSIZE);
                            socket.receive(recieve);
                            if (recieve.getPort() == port && recieve.getAddress() == host)
                                throw new IOException("Received packet from an unknown source");
                            receivedResponse = true;
                        } catch (SocketTimeoutException e) {
                            tries++;
                            System.out.println("Timeout, " + (MAXRETRY - tries) + "left.");
                        }
                        //TODO file storing and showing on the screen idk
                    } while ((!receivedResponse) && (tries < MAXRETRY));

                    data = new DATA(recieve.getData());
                    bytes_to_send = factory.returnPacket(data.getBlockNum()).returnPacketContent();
                    socket.setSoTimeout(0);

                    //TODO get a better expression to represent ending the transmission when the data is smaller than 512
                } while(data.getData().length==512);

            } else if(command.equals("quit")) break;
        }
        socket.close();
    }

    private static String getContentFromCommand(String command, String regexr){
        Pattern pattern = Pattern.compile(regexr);
        Matcher matcher = pattern.matcher(command);
        if (!matcher.find()) throw new IllegalArgumentException("Invalid format!");
        return matcher.find()? matcher.group(0) : null;
    }
}
