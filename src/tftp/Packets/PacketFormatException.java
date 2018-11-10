package tftp.Packets;

public class PacketFormatException extends Exception{
    public PacketFormatException() {
        super();
    }
    public PacketFormatException(String msg) {
        super();
        System.err.println(msg);
    }
}
