package packets;

public class ERROR {
    private byte[]  opcode;
    private byte[] errorCode;
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
    public ERROR(byte[] opcode, byte[] errorCode, String errMesg) {
        this.opcode = opcode;
        this.errorCode = errorCode;
        this.errMesg = errMesg;
    }

    public byte[] getOpcode() {
        return opcode;
    }

    public byte[] getErrorCode() {
        return errorCode;
    }

    public String getErrMesg() {
        return errMesg;
    }
}
