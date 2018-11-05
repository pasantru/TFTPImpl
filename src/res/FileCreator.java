package res;

import java.nio.file.Files;
import java.io.File;
import java.io.*;

public class FileCreator{

    private static final int MAX_LENGTH = 512;
    public static byte[] contentsOfFile(String file_name, int off) throws FileNotFoundException{
        File file = new File(file_name);
        FileInputStream fis = new FileInputStream(file.getAbsolutePath());

        int size = MAX_LENGTH<=((int)file.length()-off)?MAX_LENGTH:((int)file.length()-off);
        byte[] buffer = new byte[size];

        file.getAbsolutePath();
        try{
            fis.read(buffer, off, size);
        }catch (IOException ex){ex.printStackTrace();}
        return buffer;
    }

    public static void createFileFromContentsBin(String file_name, byte[] contents){
        new File(file_name);
        try (FileOutputStream fos = new FileOutputStream(file_name)) {
            fos.write(contents);
        }catch (IOException ex){ex.printStackTrace();}
    }
}
