package res;

import java.nio.file.Files;
import java.io.File;
import java.io.*;

public class FileCreator{
    public static byte[] contentsOfFileText(String file_name){
        try(BufferedReader br = new BufferedReader(new FileReader(file_name))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            return sb.toString().getBytes();
        } catch (IOException ex){
            ex.printStackTrace();
        }
        return null;
    }
    public static byte[] contentsOfFile(String file_name){
        try{
            return Files.readAllBytes(new File(file_name).toPath());
        }catch (IOException ex){
            ex.printStackTrace();
        }
        return null;
    }
    public static void createFileFromContentsBin(String file_name, byte[] contents){
        new File(file_name);
        try (FileOutputStream fos = new FileOutputStream(file_name)) {
            fos.write(contents);
        /* fos.close(); There is no more need for this line since you had
          created the instance of "fos" inside the try. And this will
          automatically close the OutputStream */
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }
}
