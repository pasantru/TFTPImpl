package packets;

public class RRQ_WRQ {
    private byte[] opcode;
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

    public RRQ_WRQ(byte[] opcode, String filename, String mode) {
        //TODO filename terminated in zero byte
        //TODO modes netascii, octet, mail
        this.opcode = opcode;
        this.filename = filename;
        this.mode = mode;
    }

    public byte[] getOpcode() {
        return opcode;
    }

    public String getFilename() {
        return filename;
    }

    public String getMode() {
        return mode;
    }
}