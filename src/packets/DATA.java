package packets;

public class DATA {
    byte[] opcode;
    byte[] BlockNum;
    byte[] data;

    /*
    TODO TFTP Formats

        Type    Op #    Format without header

              2 bytes     2 bytes       n bytes
              ---------------------------------
        DATA | 03     |   Block #   |   Data   |
              ---------------------------------
    */

    public DATA(byte[] opcode, byte[] blockNum, byte[] data) {
        this.opcode = opcode;
        BlockNum = blockNum;
        this.data = data;
    }

    public byte[] getOpcode() {
        return opcode;
    }

    public byte[] getBlockNum() {
        return BlockNum;
    }

    public byte[] getData() {
        return data;
    }
}
