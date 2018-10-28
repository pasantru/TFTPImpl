package implJavaTFTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Client {
    public static void main(String argv[]) throws IOException {
        InetAddress host = null;
        int port = 69;
        if(argv.length == 1) host = InetAddress.getByName(argv[0]);
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        TFTP_C client;
        String command = stdIn.readLine(),
                filename,
                    mode = "netascii";

        while(true){
            if(command.contains("connect")) host = InetAddress.getByName(getContentFromCommand(command,"\\b\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\b"));
            else if(command.contains("mode")) mode = getContentFromCommand(command, "[a-z]+");
            else if(command.contains("put")) {
                filename = getContentFromCommand(command, "[a-zA-Z-_]+[.][a-z]{3}");
                client = new TFTP_C(host, port, filename, mode, "put");
                client.tftp();
            } else if(command.contains("get")){
                filename = getContentFromCommand(command, "[a-zA-Z-_]+[.][a-z]{3}");
                client = new TFTP_C(host, port, filename, mode, "get");
                client.tftp();
            } else if(command.equals("quit")) break;
        }
    }

    private void getHelpPliz(){
        //TODO implement the help command
    }

    private static String getContentFromCommand(String command, String regexr){
        Pattern pattern = Pattern.compile(regexr);
        Matcher matcher = pattern.matcher(command);
        if (!matcher.find()) throw new IllegalArgumentException("Invalid format!");
        return matcher.find()? matcher.group(0) : null;
    }
}
