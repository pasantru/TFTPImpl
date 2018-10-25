package packets;

import java.io.*;

public class ACK{
    private short opcode;
    private short block_num;

        /*
    TODO TFTP Formats

        Type    Op #    Format without header

              2 bytes 2 bytes
             -------------------
        ACK |  04   |  Block #  |
             --------------------
    */

    public ACK(short block_num) {
        this.opcode = (short)04;
        this.block_num = block_num;
    }
    
    public ACK(byte[] array) {
        ByteArrayInputStream byteStream = new ByteArrayInputStream(array);
        DataInputStream in = new DataInputStream(byteStream);
        byte[] output;
        try{
            this.opcode = in.readShort();
            this.block_num = in.readShort();
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

    public short getBlocknum() {
        return block_num;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ACK &&
                ((ACK) obj).getOpcode()==this.opcode &&
                ((ACK) obj).getBlocknum()==this.block_num;
    }
}
