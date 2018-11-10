package tftpRachelcita;

import java.io.*;
import java.util.Arrays;

public class DATA{
    private short opcode;
    private short blockNum;
    private byte[] data;


    public DATA(short blockNum, byte[] data) {
        this.opcode = (short)03;
        this.blockNum = blockNum;
        this.data = data;
    }

    public DATA(byte[] array){
        ByteArrayInputStream byteStream = new ByteArrayInputStream(array);
        DataInputStream dis = new DataInputStream(byteStream);
        byte[] aux;

        try{
            this.opcode = dis.readShort();
            this.blockNum = dis.readShort();
            aux = new byte[array.length-4];
            dis.readFully(aux, 0, array.length-4);
            this.data = limpiarBasura(aux);
            byteStream.close();dis.close();

        }catch(IOException ex){
            ex.printStackTrace();
        }
    }


    public byte[] getContenidoPaquete(){
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream dis = new DataOutputStream(byteStream);
        byte[] aux = null;
        try{
            dis.writeShort(this.opcode);
            dis.writeShort(this.blockNum);
            dis.write(this.data);
            aux = byteStream.toByteArray();
            dis.close();
            byteStream.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
        return aux;
    }

    public short getOpcode() {
        return opcode;
    }

    public short getBlockNum() {
        return blockNum;
    }

    public byte[] getData() {
        return data;
    }

    private byte[] limpiarBasura(byte[] array){
        int i = array.length-1;
        while (i >= 0 && array[i] == 0){
            --i;
        }

        return Arrays.copyOf(array, i + 1);
    }
}
