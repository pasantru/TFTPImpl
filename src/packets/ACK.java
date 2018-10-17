package packets;

public class ACK {
    private byte[] opcode;
    private byte[] blocknum;

        /*
    TODO TFTP Formats

        Type    Op #    Format without header

              2 bytes 2 bytes
             -------------------
        ACK |  04   |  Block #  |
             --------------------
    */

    public ACK(byte[] opcode, byte[] blocknum) {
        this.opcode = opcode;
        this.blocknum = blocknum;
    }

    public byte[] getOpcode() {
        return opcode;
    }

    public byte[] getBlocknum() {
        return blocknum;
    }
}
