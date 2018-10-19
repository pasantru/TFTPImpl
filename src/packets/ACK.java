package packets;

public class ACK{
    private short opcode;
    private short blocknum;

        /*
    TODO TFTP Formats

        Type    Op #    Format without header

              2 bytes 2 bytes
             -------------------
        ACK |  04   |  Block #  |
             --------------------
    */

    public ACK(short blocknum) {
        this.opcode = (short)04;
        this.blocknum = blocknum;
    }

    public short getOpcode() {
        return opcode;
    }

    public short getBlocknum() {
        return blocknum;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ACK &&
                ((ACK) obj).getOpcode()==this.opcode &&
                ((ACK) obj).getBlocknum()==this.blocknum;
    }
}
