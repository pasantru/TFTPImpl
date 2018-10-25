package res;

import sun.misc.IOUtils;

import java.nio.file.Files;
import java.io.File;
import java.io.*;

class FileCreator{
    public static byte[] contentsOfFileText(String file_name) throws IOException{
        //TODO
        try(BufferedReader br = new BufferedReader(new FileReader(file_name))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            return sb.toString().getBytes();
        }
    }
    public static byte[] contentsOfFile(String file_name) throws IOException{
        return Files.readAllBytes(new File(file_name).toPath());
    }
    public static void createFileFromContentsBin(String file_name, byte[] contents){
        File newFile = new File(file_name);
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
