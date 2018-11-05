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
        //TODO the port is not 69 must change
        int port = 69;
        if(argv.length == 1) host = InetAddress.getByName(argv[0]);
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        TFTP_C client;
        String command,
                filename,
                    mode = "ascii";

        while(true){
            System.out.printf("%s ","tftp>");
            command = stdIn.readLine();
            if(command.equals("help")) getHelpPliz();
            else if(command.contains("connect")) host = InetAddress.getByName(getContentFromCommand(command,"[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}"));
            else if(command.contains("mode")) mode = getContentFromCommand(command.substring(4), "[a-z]+");
            else if(command.contains("put")) {
//                filename = getContentFromCommand(command.substring(3), "[a-zA-Z-_]+([.][a-z]{1,5})+");
                filename = command.substring(command.lastIndexOf(' ')+1);
                client = new TFTP_C(host, port, filename, mode, "put");
                client.tftp();
            } else if(command.contains("get")){
//                filename = getContentFromCommand(command.substring(3), "[a-zA-Z-_]+([.][a-z]{1,5})+");
                filename = command.substring(command.lastIndexOf(' ')+1);
                client = new TFTP_C(host, port, filename, mode, "get");
                client.tftp();
            } else if(command.equals("quit") || command.equals("q")) break;
        }
    }

    private static void getHelpPliz(){

        StringBuilder sb = new StringBuilder();
        sb.append("NAME\n");
        sb.append("\ttftp — trivial file transfer program\n\n");
        sb.append("SYNOPSIS\n");
        sb.append("\ttftp [host]\n\n");
        sb.append("DESCRIPTION\n");
        sb.append("\tTftp is the user interface to the Internet TFTP (Trivial File Transfer Protocol), which allows users to transfer files to and\n");
        sb.append("\trom a remote machine.  The remote host may be specified on the command line, in which case tftp uses host as the default host\n");
        sb.append("\tfor future transfers (see the connect command below).\n\n");
        sb.append("COMMANDS\n");
        sb.append("\tOnce tftp is running, it issues the prompt and recognizes the following commands:\n");
        sb.append("\tascii    Shorthand for \"mode ascii\"\n");
        sb.append("\tbinary   Shorthand for \"mode binary\"\n\n");

        sb.append("\tconnect host-name [port]\n");
        sb.append("\tSet the host (and optionally port) for transfers.  Note that the TFTP protocol, unlike the FTP protocol, does not\n");
        sb.append("\t\tmaintain connections betwen transfers; thus, the connect command does not actually create a connection, but merely\n");
        sb.append("\t\tremembers what host is to be used for transfers.  You do not have to use the connect command; the remote host can be\n");
        sb.append("\t\tspecified as part of the get or put commands.\n\n");

        sb.append("\tget filename\n");
//        sb.append("\tget remotename localname\n");
//        sb.append("\tget file1 file2 ... fileN\n");
        /*sb.append("\t\tGet a file or set of files from the specified sources.  Source can be in one of two forms: a filename on the remote\n");
        sb.append("\t\thost, if the host has already been specified, or a string of the form hosts:filename to specify both a host and file‐\n");
        sb.append("\t\tname at the same time.  If the latter form is used, the last hostname specified becomes the default for future trans‐\n");
        sb.append("\t\tfers.\n\n");*/
        sb.append("\tmode transfer-mode\n");
        sb.append("\t\tSet the mode for transfers; transfer-mode may be one of ascii or binary.  The default is ascii.\n\n");

        sb.append("\tput file\n");
        sb.append("\tput localfile remotefile\n");
        /*sb.append("\tput file1 file2 ... fileN remote-directory\n");
        sb.append("\t\tPut a file or set of files to the specified remote file or directory.  The destination can be in one of two forms: a\n");
        sb.append("\t\tfilename on the remote host, if the host has already been specified, or a string of the form hosts:filename to spec‐\n");
        sb.append("\t\tify both a host and filename at the same time.  If the latter form is used, the hostname specified becomes the\n");
        sb.append("\t\tdefault for future transfers.  If the remote-directory form is used, the remote host is assumed to be a UNIX machine.\n\n");*/

        sb.append("\tquit     Exit tftp.  An end of file also exits.\n\n");

        sb.append("\tstatus   Show current status.\n\n");

        sb.append("\tverbose  Toggle verbose mode.\n\n");
        System.out.println(sb.toString());
    }

    private static String getContentFromCommand(String command, String regexr){
        Pattern pattern = Pattern.compile(regexr);
        Matcher matcher = pattern.matcher(command);
        if (!matcher.find()) throw new IllegalArgumentException("Invalid format!");
        return matcher.group(0);
    }
}
