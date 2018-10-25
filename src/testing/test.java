package testing;

import packets.DATA;
import packets.RRQ_WRQ;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class test {
    public static void main(String argv[]) throws IOException{
        RRQ_WRQ data = new RRQ_WRQ((short)01, "/tmp/var1/var2/var3/varx/OS.img", "netascii");
        byte[] array = data.returnPacketContent();
        RRQ_WRQ rq = new RRQ_WRQ(array);
        System.out.println(rq.getFilename());
        System.out.println(rq.getMode());

        for (int i = 0; i < array.length; i++) {
            System.out.print(array[i] + ", ");
        }
        System.out.println(array.length);
        int first_pos = -1,
                last_pos = -1;
        for (int i = 0; i < array.length; i++) {
            if(first_pos==-1 && array[i]==(byte)0) first_pos = i;
            else if (array[i]==(byte)0) last_pos = i;
        }
        System.out.println(first_pos - 1);
        System.out.println(last_pos - first_pos);

        System.out.println(data.equals(rq));
    }
}
