package tftp.Packets;

import java.io.*;
import java.util.Arrays;

public class DATA{
    private short opcode;
    private short block_num;
    private byte[] data;

    /*TFTP Formats

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

    public DATA(byte[] array) throws PacketFormatException{
        ByteArrayInputStream byteStream = new ByteArrayInputStream(array);
        DataInputStream in = new DataInputStream(byteStream);
        int curr_pos = 0;
        byte[] output;
        try{
            this.opcode = in.readShort();
            if(this.opcode != 03) throw new PacketFormatException("DATA");
            curr_pos += 2;
            this.block_num = in.readShort();
            curr_pos += 2;
            output = new byte[array.length-curr_pos];
            in.readFully(output, 0, array.length-curr_pos);
            this.data = trim(output);
            byteStream.close();
            in.close();
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }


    public byte[] returnPacketContent() throws IOException{
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream in = new DataOutputStream(byteStream);
        byte[] return_bytes;
        try{
            in.writeShort(this.opcode);
            in.writeShort(this.block_num);
            in.write(this.data);
        }catch (IOException e) {
            System.err.println("Error: Fuck!");
            e.printStackTrace();
        }

        return_bytes = byteStream.toByteArray();
        in.close();
        byteStream.close();
        return return_bytes;
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
    private byte[] trim(byte[] bytes){
        int i = bytes.length - 1;
        while (i >= 0 && bytes[i] == 0)
        {
            --i;
        }

        return Arrays.copyOf(bytes, i + 1);
    }
}
