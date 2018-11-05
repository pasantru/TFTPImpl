package testing;

import res.FileCreator;

import java.io.*;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class test {
    private static final int MAX_LENGTH = 512;

    public static void main(String argv[]) throws IOException {
        InetAddress host = null;
        if(argv.length == 1) host = InetAddress.getByName(argv[0]);
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        String command;
        while(true){
            System.out.printf("%s ","tftp>");
            command = stdIn.readLine();
            String filename = command.substring(command.lastIndexOf(' ')+1);
            System.out.println(filename);
            System.out.println(filename.length());

//            System.out.println(host.toString());
        }
    }


    private static String getContentFromCommand(String command, String regexr){
        Pattern pattern = Pattern.compile(regexr);
        Matcher matcher = pattern.matcher(command);
        if (!matcher.find()) throw new IllegalArgumentException("Invalid format!");
        return matcher.group(0);
    }
}
