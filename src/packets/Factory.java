package packets;

public class Factory {
    private short opcode;
    /*
        This constructor returns RRQ_WRQ depending on the packet_type which will
        be or a RRQ or a WRQ packet.
     */
    public RRQ_WRQ returnPacket(String packet_type, String filename, String mode){
        if(packet_type.equals("RRQ")) this.opcode = (short)01;
        else if (packet_type.equals("WRQ")) this.opcode = (short)02;
        return new RRQ_WRQ(this.opcode, filename, mode);
    }

    //This is the Constructor for DATA packets
    public DATA returnPacket(short block_num, byte[] data){
        return new DATA(block_num, data);
    }

    //This is the Constructor for the ACK packet
    public ACK returnPacket(short block_num){
        return new ACK(block_num);
    }

    //This constructor returns a ERROR packet
    public ERROR returnPacket(short error_code, String error_message){
        return new ERROR(error_code, error_message);
    }

}
