package tftpRachelcita;

import java.io.*;

public class ACK{
    private short opcode;
    private short blockNum;

    public ACK(short blockNum) {
        this.opcode = (short)04;
        this.blockNum = blockNum;
    }
    
    public ACK(byte[] array){
        ByteArrayInputStream byteStream = new ByteArrayInputStream(array);
        DataInputStream dis = new DataInputStream(byteStream);

        try{
            this.opcode = dis.readShort();
            this.blockNum = dis.readShort();
            byteStream.close();
            dis.close();
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }
    
    public byte[] getContenidoPaquete(){
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(byteStream);
        byte[] aux = null;

        try{
            dos.writeShort(this.opcode);
            dos.writeShort(this.blockNum);
            aux = byteStream.toByteArray();
            dos.close();
            byteStream.close();
        }catch (IOException e) {
            e.printStackTrace();
        }

        return aux;
    }

    public short getOpcode() {
        return opcode;
    }

    public short getBlocknum() {
        return blockNum;
    }

}
