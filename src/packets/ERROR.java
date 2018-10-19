package packets;

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
