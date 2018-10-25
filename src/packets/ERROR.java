package packets;

import java.io.*;

public class ERROR{
    private short opcode;
    private short errorCode;
    private String errMesg;

    /*TODO Error messages
        Value       Meaning
        0           Not defined, see error message (if any).
        1           File not found.
        2           Access violation.
        3           Disk full or allocation exceeded.
        4           Illegal TFTP operation.
        5           Unknown transfer ID.
        6           File already exists.
        7           No such user.
    */

    /*
    TODO TFTP Formats

        Type    Op #    Format without header

                2 bytes   2 bytes      string  1 byte
               ----------------------------------------
        ERROR | 05      | ErrorCode |  ErrMsg  |   0   |
               ----------------------------------------
    */
    public ERROR(short errorCode, String errMesg) {
        this.opcode = (short)05;
        this.errorCode = errorCode;
        this.errMesg = errMesg;
    }
    
    public ERROR(byte[] array){
        ByteArrayInputStream byteStream = new ByteArrayInputStream(array);
        DataInputStream in = new DataInputStream(byteStream);
        int curr_pos = 0;
        byte[] output;
        try{
            this.opcode = in.readShort();
            curr_pos += 2;
            this.errorCode = in.readShort();
            curr_pos += 2;
            output = new byte[array.length-curr_pos];
            in.readFully(output, 0, array.length-curr_pos);
            this.errMesg = new String(output);
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
            in.writeShort(this.errorCode);
            in.writeBytes(this.errMesg);
            in.write(0);
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
