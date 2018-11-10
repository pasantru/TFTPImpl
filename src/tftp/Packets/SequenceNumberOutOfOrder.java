package tftp.Packets;

public class SequenceNumberOutOfOrder extends Exception{
    public SequenceNumberOutOfOrder() {
    }

    public SequenceNumberOutOfOrder(String message) {
        super(message);
    }
}
