package packets;

import java.io.*;

public class RRQ_WRQ{
    private short opcode;
    private String filename;
    private String mode;

    /*TFTP Formats

        Type    Op #    Format without header

                2 bytes      string      1 byte string 1 byte
              -----------------------------------------------
        RRQ/ |   01/02   |   Filename   |  0  |  Mode  |  0  |
        WRQ   -----------------------------------------------

    */

    public RRQ_WRQ(short opcode, String filename, String mode) {
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

        for (int i = 2; i < array.length; i++) {
            if(first_pos==-1 && array[i]==(byte)0) first_pos = i;
            else if (array[i]==(byte)0) last_pos = i;
        }
        System.out.println("First 0: " + first_pos + ", Second 0: " + last_pos);
        try{
            this.opcode = in.readShort();
            output = new byte[first_pos-2];
            in.readFully(output, 0, first_pos-2);
            this.filename = new String(output);
            output = new byte[last_pos-first_pos-1];
            in.skipBytes(1);
            in.readFully(output, 0, last_pos-first_pos-1);
            this.mode = new String(output);
            in.skipBytes(1);
            in.close();
            byteStream.close();
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }


    public byte[] returnPacketContent() throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream in = new DataOutputStream(byteStream);
        byte[] return_bytes;
        try{
            in.writeShort(this.opcode);
            in.writeBytes(filename);
            in.write(0);
            in.writeBytes(mode);
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
}