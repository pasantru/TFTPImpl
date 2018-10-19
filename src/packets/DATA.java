package packets;

import java.lang.reflect.Array;
import java.util.Arrays;

public class DATA{
    private short opcode;
    private short block_num;
    private byte[] data;

    /*
    TODO TFTP Formats

        Type    Op #    Format without header

              2 bytes     2 bytes       n bytes
              ---------------------------------
        DATA | 03     |   Block #   |   Data   |
              ---------------------------------
    */

    public DATA(short block_num, byte[] data) {
        this.opcode = (short)03;
        this.block_num = block_num;
        this.data = data;
    }

    public short getOpcode() {
        return opcode;
    }

    public short getBlockNum() {
        return block_num;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof DATA &&
                ((DATA) obj).getOpcode()==this.opcode &&
                ((DATA) obj).getBlockNum()==this.block_num &&
                Arrays.equals(((DATA) obj).getData(),this.data);
    }
}
