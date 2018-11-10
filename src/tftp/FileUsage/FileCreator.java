package tftp.FileUsage;

import java.io.File;
import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;

public class FileCreator{

    private static final int MAX_LENGTH = 512;

    public static byte[] contentsOfFile(String file_name, int off) throws FileNotFoundException{
        File file = new File(file_name);

       FileInputStream fis = new FileInputStream(file.getAbsolutePath());

        // int size = MAX_LENGTH<=((int)file.length()-off)?MAX_LENGTH:((int)file.length()-off);
        byte[] buffer = new byte[MAX_LENGTH];

        try{
            fis.skip(off);
            int size = fis.read(buffer, 0, MAX_LENGTH);
            if(size!=-1){
                buffer = Arrays.copyOfRange(buffer, 0, size);
            }else buffer = new byte[0];
            // buffer = Arrays.copyOfRange(Files.readAllBytes(file.toPath()),off,off+size);
        }catch (IOException ex){
            ex.printStackTrace();
        }
        return buffer;
    }

    public static void createFileFromContentsBin(String file_name, byte[] contents){
        /*new File(file_name);
        try (FileOutputStream fos = new FileOutputStream(file_name)) {
            fos.write(contents);
        }catch (IOException ex){ex.printStackTrace();}*/
        new File("pruebesilla.txt");
        try (FileOutputStream fos = new FileOutputStream("pruebesilla.txt")) {
            fos.write(contents);
        }catch (IOException ex){ex.printStackTrace();}
    }
    public static void getInfo(String filename){
        System.out.println("File size: " + new File(filename).length());
    }
}
