package res;

public class PacketException extends Exception {
    public PacketException(){
        super();
    }
    public PacketException(String ex){
        super("Error: " + ex);
    }
}
