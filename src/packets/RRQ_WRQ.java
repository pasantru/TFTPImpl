package packets;

import java.io.*;
import java.util.Arrays;

public class RRQ_WRQ{
    private short opcode;
    private String filename;
    private String mode;

    /*
    TODO TFTP Formats

        Type    Op #    Format without header

                2 bytes      string      1 byte string 1 byte
              -----------------------------------------------
        RRQ/ |   01/02   |   Filename   |  0  |  Mode  |  0  |
        WRQ   -----------------------------------------------

    */

    public RRQ_WRQ(short opcode, String filename, String mode) {
        //TODO filename terminated in zero byte
        //TODO modes netascii, octet
        this.opcode = opcode;
        this.filename = filename;
        this.mode = mode;
    }

    public RRQ_WRQ(byte[] array){
        ByteArrayInputStream byteStream = new ByteArrayInputStream(array);
        DataInputStream in = new DataInputStream(byteStream);
        byte[] output;
        int first_pos = -1,
                last_pos = -1;
        for (int i = 0; i < array.length; i++) {
            if(first_pos==-1 && array[i]==(byte)0) first_pos = i;
            else if (array[i]==(byte)0) last_pos = i;
        }
        try{
            this.opcode = in.readByte();
            output = new byte[first_pos-1];
            in.readFully(output, 0, first_pos-1);
            this.filename = new String(output);
            output = new byte[last_pos-first_pos-1];
            in.readByte();
            in.readFully(output, 0, last_pos-first_pos-1);
            this.mode = new String(output);
            in.readByte();
        }catch (IOException ex){
            ex.printStackTrace();
        }
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

    @Override
    public boolean equals(Object obj) {
        return obj instanceof RRQ_WRQ &&
                (((RRQ_WRQ) obj).getOpcode())==this.opcode &&
                ((RRQ_WRQ) obj).getFilename().equals(this.filename) &&
                ((RRQ_WRQ) obj).getMode().equals(this.mode);
    }

    public byte[] returnPacketContent() throws IOException {
        //TODO fix
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(byteStream);
        byte[] return_bytes;
        try{
            out.write(this.opcode);
            out.write(filename.getBytes());
            out.write(0);
            out.write(mode.getBytes());
            out.write(0);

        }catch (IOException e) {
            e.printStackTrace();
        }
        return_bytes = byteStream.toByteArray();
        byteStream.close();
        out.close();
        return return_bytes;

    }
}