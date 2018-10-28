package testing;

import res.FileCreator;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class test {
    public static void main(String argv[]) throws IOException {
//        String separation = "\t\t\t\t\t\t\t\t\t\t ";
//        System.out.println(("----> RRQ " + "filename" + " " + "mode"));
//        System.out.println((separation + "<---- ACK " + "0"));
//        System.out.println(("----> DATA 1" + "512 bytes"));
//        System.out.println((separation + "<---- ACK " + "1"));
//        System.out.println(("----> DATA 2" + "512 bytes"));
//        System.out.println((separation + "<---- ACK " + "2"));
//        System.out.println(("----> DATA 2" + "10 bytes"));
//        System.out.println((separation + "<---- ACK " + "3"));
//        System.out.println((separation + "<---- ERROR " + "03 " + "File not found "));
//
//        System.out.println(("% P: paquetes perdidos; % R: paquetes retransmitidos"));

        int compare = 3;
        short shortnum = (short)03;
        byte[] hello = {(byte)0,(byte)4,(byte)0,(byte)3};
        System.out.println(hello[2] + hello[3]);
        System.out.println(shortnum);

        if(hello[0]==0 && hello[1]==4 && shortnum==(short)compare) System.out.println(true);
        else System.out.println(false);
    }
}
