package implJavaTFTP;
import packets.DATA;
import packets.Factory;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketTimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TFTP {

    public static void tftp(){
        //TODO put
        filename = getContentFromCommand(command, "[a-zA-Z-_]+[.][a-z]{3}");
        File file = new File(filename);


        //TODO get
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

    }
    private static String getContentFromCommand(String command, String regexr){
        Pattern pattern = Pattern.compile(regexr);
        Matcher matcher = pattern.matcher(command);
        if (!matcher.find()) throw new IllegalArgumentException("Invalid format!");
        return matcher.find()? matcher.group(0) : null;
    }
}
