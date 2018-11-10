package tftpRachelcita;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;

public class CrearFichero {
    private static final int TAMMAX = 512;
    public static byte[] contenidoFichero(String fileName, int desp){
        File file = new File(fileName);
        byte[] contenidoFichero = new byte[512];
        try{
            FileInputStream fileInputStream = new FileInputStream(file.getAbsolutePath());
            fileInputStream.skip(desp);
            int tam = fileInputStream.read(contenidoFichero,0,TAMMAX);
            if(tam!=-1) contenidoFichero = Arrays.copyOfRange(contenidoFichero, 0, tam);
            else contenidoFichero = new byte[0];
        }catch (IOException ex){
            ex.printStackTrace();
        }
        return contenidoFichero;
    }
    public static void crearFichero(String file_name, byte[] contents){
        new File(file_name);
        try (FileOutputStream fos = new FileOutputStream(file_name)) {
            fos.write(contents);
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }
}
