package tftp.Packets;

public class Factory {
    /*
        This constructor returns RRQ_WRQ depending on the packet_type which will
        be or a RRQ or a WRQ packet.
     */
    public static RRQ_WRQ returnPacket(String packet_type, String filename, String mode){
        short opcode = (short)01;
        if(packet_type.equals("RRQ")) opcode = (short)01;
        else if (packet_type.equals("WRQ")) opcode = (short)02;
        return new RRQ_WRQ(opcode, filename, mode);
    }

    //This is the Constructor for DATA Packets
    public static DATA returnPacket(short block_num, byte[] data){
        return new DATA(block_num, data);
    }

    //This is the Constructor for the ACK packet
    public static ACK returnPacket(short block_num){
        return new ACK(block_num);
    }

    //This constructor returns a ERROR packet
    public static ERROR returnPacket(short error_code, String error_message){
        return new ERROR(error_code, error_message);
    }

}
