package tftpRachelcita;

import java.io.*;

public class ERROR{
    private short opcode;
    private short errorCode;
    private String errMesg;

    public ERROR(short errorCode, String errMesg) {
        this.opcode = (short)05;
        this.errorCode = errorCode;
        this.errMesg = errMesg;
    }
    
    public ERROR(byte[] array){
        ByteArrayInputStream byteStream = new ByteArrayInputStream(array);
        DataInputStream dis = new DataInputStream(byteStream);
        byte[] aux;
        try{
            this.opcode = dis.readShort();
            this.errorCode = dis.readShort();
            aux = new byte[array.length-5];
            dis.readFully(aux, 0, array.length-5);
            this.errMesg = new String(aux);
            byteStream.close();
            dis.close();
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }


    public byte[] getContenidoPaquete() throws IOException{
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream in = new DataOutputStream(byteStream);
        byte[] return_bytes;
        try{
            in.writeShort(this.opcode);
            in.writeShort(this.errorCode);
            in.writeBytes(this.errMesg);
            in.write(0);
        }catch (IOException e) {
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

    public short getErrorCode() {
        return errorCode;
    }

    public String getErrMesg() {
        return errMesg;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ERROR &&
                ((ERROR) obj).getOpcode()==this.opcode &&
                ((ERROR) obj).getErrMesg().equals(this.errMesg) &&
                ((ERROR) obj).getErrorCode()==this.errorCode;
    }
}
