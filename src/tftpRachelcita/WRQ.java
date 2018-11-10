package tftpRachelcita;

import java.io.*;

public class WRQ{
    private short opcode;
    private String filename;
    private String mode;

    public WRQ(String filename, String mode) {
        this.opcode = (short)02;
        this.filename = filename;
        this.mode = mode;
    }

    public WRQ(byte[] array){
        ByteArrayInputStream byteStream = new ByteArrayInputStream(array);
        DataInputStream dis = new DataInputStream(byteStream);
        byte[] aux;
        int primerDelimitador = -1,
                segundoDelimitador = -1;

        for (int i = 2; i < array.length; i++) {
            if(primerDelimitador==-1 && array[i]==(byte)0){
                primerDelimitador = i;
            }else if (segundoDelimitador==-1 && array[i]==(byte)0){
                segundoDelimitador = i;
            }
        }
        try{

            this.opcode = dis.readShort();
            aux = new byte[primerDelimitador-2];
            dis.readFully(aux, 0, primerDelimitador-2);
            this.filename = new String(aux);
            aux = new byte[segundoDelimitador-primerDelimitador-1];
            dis.skipBytes(1);
            dis.readFully(aux, 0, segundoDelimitador-primerDelimitador-1);
            this.mode = new String(aux);
            dis.skipBytes(1);
            dis.close();
            byteStream.close();

        }catch (IOException ex){
            ex.printStackTrace();
        }
    }


    public byte[] getContenidoPaquete(){
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(byteStream);
        byte[] aux = null;
        try{
            dos.writeShort(this.opcode);
            dos.writeBytes(filename);
            dos.write(0);
            dos.writeBytes(mode);
            dos.write(0);
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

    public String getFilename() {
        return filename;
    }

    public String getMode() {
        return mode;
    }
}