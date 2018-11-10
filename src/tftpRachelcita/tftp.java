package tftpRachelcita;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.Scanner;

public class tftp {
    private static final int MAX_TAM = 516;
    private static final int MAXRETRY = 3;
    private static final int TIMEOUTTIME = 1000;


    public static void main(String argv[]) throws IOException {
        InetAddress host = null;
        int puerto = 6790;
        if(argv.length == 1) host = InetAddress.getByName(argv[0]);
        Scanner scanner = new Scanner(System.in);
        String[] comando;
        String filename;
        String mode = "ascii";

        while(true){
            System.out.print("tftp> ");
            comando = scanner.nextLine().split(" ");

            if(comando.equals("help")){
                help();
            }else if(comando[0].equals("connect")){
                host = InetAddress.getByName(comando[1]);

            }else if(comando[0].equals("mode")){
                if(!(mode = comando[1]).equals("ascii") && !mode.equals("binary")) System.out.println("Error: mode <ascii|binary>");
            }else if(comando[0].toUpperCase().equals("PUT")) {
                if(host == null){
                    System.out.println("Hay que conectarse a un servidor primero");
                }else{
                    filename = comando[1];
                    metodoPut(host, puerto, filename, mode);
                }
            } else if(comando[0].toUpperCase().equals("GET")){
                if(host == null){
                    System.out.println("Hay que conectarse a un servidor primero");
                }else {
                    filename = comando[1];
                    metodoGet(host, puerto, filename, mode);
                }
            } else if(comando[0].equals("quit")){
                break;
            }
        }
    }


    private static void metodoPut(InetAddress address, int port, String filename, String mode){
        int tries;
        String perdido = "";
        int posicionInicial, paquetesEnviados = 0, paquetesPerdidos = 0, numeroBloque = 0;
        boolean respuestaRecibida;
        DATA data;

        try{

            byte[] contenidoPaquete = new WRQ(filename, mode).getContenidoPaquete();

            //Creamos los packetes y el socket para la transmision de datos
            DatagramSocket socket = new DatagramSocket();
            DatagramPacket enviar;
            DatagramPacket recibir = null;

            //Inicializamos el paquete enviar que vamos a enviar para iniciar la conversacion con el servidor
            enviar = new DatagramPacket(contenidoPaquete, contenidoPaquete.length, address, port);
            socket.send(enviar);
            System.out.println(("> WRQ "  + filename + " " + mode));

//            recibir = new DatagramPacket(new byte[MAX_TAM], MAX_TAM);
//            socket.receive(recibir);

            do{

                tries = 0;
                respuestaRecibida = false;

                posicionInicial = numeroBloque*512;
                byte[] buff = CrearFichero.contenidoFichero(filename, posicionInicial);
                data = new DATA((short)(numeroBloque), buff);
                contenidoPaquete = data.getContenidoPaquete();

                do {

                    socket.setSoTimeout(TIMEOUTTIME);


                    try {

                        recibir = new DatagramPacket(new byte[MAX_TAM], MAX_TAM);
                        socket.receive(recibir);

                        /*
                         * Cuando el paquete recibido viene de otro servidor tenemos que envir un paquete de error y cerrar
                         * la comunicacion con el cliente
                         */
                        if(recibir.getPort() != port && !recibir.getAddress().equals(address)){
                            System.out.println("Emisor del paquete desconocido");
                            contenidoPaquete = new ERROR((short) 05, "El emisor del paquete es desconocido").getContenidoPaquete();
                            enviar = new DatagramPacket(contenidoPaquete, contenidoPaquete.length, address, port);
                            socket.send(enviar);
                            respuestaRecibida = false;
                            break;
                        }


                        /*
                         *  Cuando recibimos un paquete imprimimos por pantalla el contenido del paquete
                         *  de una forma en la que el usuario pueda entenderla. Y comprobamos si el paquete esta
                         *  en orden
                         */
                        byte[] bytesRecibidos = recibir.getData();
                        if(bytesRecibidos[0]==0 && bytesRecibidos[1]==4){
                            ACK aux = new ACK(bytesRecibidos);
                            if(numeroBloque==aux.getBlocknum()){
                                System.out.println(("< ACK " + aux.getBlocknum()));
                            }else{
                                System.out.println("Se ha recibido un paquete fuera de orden");
                                contenidoPaquete = new ERROR((short) 05, "Se ha recibido un paquete fuera de orden").getContenidoPaquete();
                                enviar = new DatagramPacket(contenidoPaquete, contenidoPaquete.length, address, port);
                                socket.send(enviar);
                                respuestaRecibida = false;
                                break;
                            }
                        } else if(buff[0]==0 && buff[1]==5) {
                            System.out.println("Error recibido");
                            ERROR aux = new ERROR(bytesRecibidos);
                            System.out.println(("< ERROR " + aux.getErrorCode() + " " + aux.getErrMesg()));
                            respuestaRecibida = false;
                            break;
                        }
                        /*
                         * Una vez comprobado que el paquete esta en orden y no viene de un servidor desconocido procedemos
                         * a imprimirlo por pantalla dependiendo de el contenido y el tipo de paquete se procedera a la terminacion
                         * o a continuar con la transmission
                         */
                        respuestaRecibida = true;
                        perdido = "";
                        enviar = new DatagramPacket(contenidoPaquete, contenidoPaquete.length, address, port);
                        socket.send(enviar);
                        System.out.println(("> DATA " + perdido + (numeroBloque) + " " + (contenidoPaquete.length-4) + "bytes"));
                        paquetesEnviados++;


                    } catch (SocketTimeoutException e) {
                        tries++;
                        paquetesPerdidos++;
                        perdido = "(R) ";
                    }

                } while ((!respuestaRecibida) && (tries < MAXRETRY));


                if(!respuestaRecibida) break;
                if(recibir.getPort() == port && recibir.getAddress().equals(address)){
                    numeroBloque++;
                    socket.setSoTimeout(0);
                }

            }while(data.getData().length >= 512);

            System.out.println(paquetesPerdidos + "paquetes perdidos, " + (paquetesEnviados-paquetesPerdidos) + "paquetes retransmitidos");

        }catch(IOException ex){ex.printStackTrace();}
    }




    private static void metodoGet(InetAddress address, int port, String filename, String mode) throws IOException{
        int tries;
        int numeroBloque = 1;
        String perdido = "";

        boolean respuestaRecibida;
        byte [] buffer = new byte[0];
        DATA d = null;
        DatagramSocket socket = new DatagramSocket();
        DatagramPacket enviar;
        DatagramPacket recibir = null;

        try{
            byte[] contenidoPaquete = new RRQ(filename, mode).getContenidoPaquete();

            enviar = new DatagramPacket(contenidoPaquete, contenidoPaquete.length, address, port);
            socket.send(enviar);
            System.out.println(("> RRQ " + perdido + filename + " " + mode));


            do{

                tries = 0;
                respuestaRecibida = false;

                do{
                    socket.setSoTimeout(TIMEOUTTIME);

                    try {

                        recibir = new DatagramPacket(new byte[MAX_TAM], MAX_TAM);
                        socket.receive(recibir);



                        if(recibir.getPort() != port && !recibir.getAddress().equals(address)){
                            contenidoPaquete = new ERROR((short) 05, "El emisor del paquete es desconocido").getContenidoPaquete();
                            enviar = new DatagramPacket(contenidoPaquete, contenidoPaquete.length, address, port);
                            socket.send(enviar);
                            respuestaRecibida = false;
                            break;
                        }

                        respuestaRecibida = true;
                        perdido = "";

                        /*
                         *  Cuando recibimos un paquete imprimimos por pantalla el contenido del paquete
                         *  de una forma en la que el usuario pueda entenderla. Y comprobamos si el paquete esta
                         *  en orden
                         */
                        byte[] bytesRecibidos = recibir.getData();
                        if(bytesRecibidos[0]==0 && bytesRecibidos[1]==3) {
                            DATA aux = new DATA(bytesRecibidos);
                            if(numeroBloque ==aux.getBlockNum()){
                                System.out.println(("< DATA " + (aux.getBlockNum()) + " " + (aux.getData().length) + "bytes"));
                                d = new DATA(recibir.getData());
                                buffer = unionArrays(buffer, d.getData());
                            }else{
                                System.out.println("Error paquete recibido fuera de orden");
                                contenidoPaquete = new ERROR((short) 05, "Se ha recibido un paquete fuera de orden").getContenidoPaquete();
                                enviar = new DatagramPacket(contenidoPaquete, contenidoPaquete.length, address, port);
                                socket.send(enviar);
                                respuestaRecibida = false;
                                break;
                            }
                        } else if(contenidoPaquete[0]==0 && contenidoPaquete[1]==5) {
                            ERROR aux = new ERROR(contenidoPaquete);
                            System.out.println(("< ERROR " + aux.getErrorCode() + " " + aux.getErrMesg()));
                            respuestaRecibida = false;
                            break;
                        }

                        contenidoPaquete = new ACK((short)numeroBloque).getContenidoPaquete();
                        enviar = new DatagramPacket(contenidoPaquete, contenidoPaquete.length, address, port);
                        socket.send(enviar);
                        System.out.println("> ACK " + perdido + numeroBloque);


                    } catch (SocketTimeoutException e) {
                        tries++;
                        perdido = "(R) ";
                    }

                }while((!respuestaRecibida) && (tries < MAXRETRY));

                if(respuestaRecibida){
                    numeroBloque++;
                    socket.setSoTimeout(0);
                } else break;

            }while(d.getData().length >= 512);

            if(respuestaRecibida){
                CrearFichero.crearFichero(filename, buffer);
                if(mode.equals("ascii")){
                    System.out.println(new String(buffer));
                }
            }
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }


    private static byte[] unionArrays(byte[] array1, byte[] array2){
        byte[] joinedArray = Arrays.copyOf(array1, array1.length + array2.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }

    private static void help(){
        //TODO help me pliz need some rachel pussy moar pussy moar moar
    }
}
