package packets;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class RRQ_WRQ{
    private short opcode;
    private String filename;
    private String mode;

    /*
    TODO TFTP Formats

        Type    Op #    Format without header

                2 bytes string 1 byte string 1 byte
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
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        byte[] bukkake_of_bytes;
        try{
            ba.write(this.opcode);
            ba.write(filename.getBytes());
            ba.write(0);
            ba.write(mode.getBytes());
            ba.write(0);

        }catch (IOException e) {
            e.printStackTrace();
        }

        bukkake_of_bytes = ba.toByteArray();
        ba.close();
        return bukkake_of_bytes;

    }
}