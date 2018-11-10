package tftpRachelcita;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.*;
import java.nio.file.*;
import java.util.Arrays;


public class Servidor {
    private static final int        TIMEOUTTIME = 1000;
    private static final int        MAXRETRY = 3;
    private static final int        TAM_MAX = 516;
    private static final int        PORT = 6790;
    private static SocketAddress fullAddress;
    private static InetAddress      address;
    private static int              port;

    public static void main(String argv[]){

        try{
            DatagramSocket socket = new DatagramSocket(PORT);

            while(true){
                try {
                    DatagramPacket recibido;
                    byte[] contenidoPaquete;
                    recibido = new DatagramPacket(new byte[TAM_MAX], TAM_MAX);
                    socket.receive(recibido);
                    contenidoPaquete = recibido.getData();
                    address = recibido.getAddress();
                    port = recibido.getPort();
                    fullAddress = recibido.getSocketAddress();

                    if (contenidoPaquete[0]==0 && contenidoPaquete[1]==1) {

                        RRQ paquete = new RRQ(recibido.getData());
                        new File(paquete.getFilename());
                        metodoGet(socket, recibido);

                    } else if (contenidoPaquete[0]==0 && contenidoPaquete[1]==2) {

                        WRQ paquete = new WRQ(recibido.getData());
                        new File(paquete.getFilename());
                        metodoPut(socket, recibido);

                    }

                }catch (FileNotFoundException ex){
                    try{
                        byte[] contenidoError = new ERROR((short) 01, "Fichero no se ha encontrado en el servidor.").getContenidoPaquete();
                        DatagramPacket request = new DatagramPacket(contenidoError, contenidoError.length, address, port);
                        socket.send(request);

                    }catch (IOException e){
                        //Error al enviar el paquete sin retransmision
                    }

                }catch (AccessDeniedException e) {
                    try{
                        byte[] bytes_to_send = new ERROR((short) 02, "Acceso prohibido.").getContenidoPaquete();
                        DatagramPacket request = new DatagramPacket(bytes_to_send, bytes_to_send.length, address, port);
                        socket.send(request);

                    }catch (IOException ex){
                        //Error al enviar el paquete sin retransmision
                    }

                }catch (FileAlreadyExistsException ex){
                    try{
                        byte[] contenidoError = new ERROR((short) 06, "El archivo ya existe en el servidor.").getContenidoPaquete();
                        DatagramPacket request = new DatagramPacket(contenidoError, contenidoError.length, address, port);
                        socket.send(request);

                    }catch (IOException e){
                        //Error al enviar el paquete sin retransmision
                    }

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }catch(SocketException ex){
            ex.printStackTrace();
        }
    }



    private static void metodoGet(DatagramSocket socket, DatagramPacket receive){
        try {
            int tries;
            byte[] bytes_to_send;
            boolean receivedResponse;
            String filename;
            RRQ packet = new RRQ(receive.getData());
            filename = packet.getFilename();

            int packet_num = 1;
            int start_pos;
            DATA data;
            DatagramPacket send;


            do {
                tries = 0;
                receivedResponse = false;
                start_pos = (packet_num-1) * 512;
                data = new DATA((short) (packet_num), CrearFichero.contenidoFichero(filename, start_pos));
                bytes_to_send = data.getContenidoPaquete();
                do {
                    send = new DatagramPacket(bytes_to_send, bytes_to_send.length, address, port);
                    socket.setSoTimeout(TIMEOUTTIME);
                    socket.send(send);

                    try {
                        receive = new DatagramPacket(new byte[TAM_MAX], TAM_MAX);
                        socket.receive(receive);
                        receivedResponse = true;

                        byte[] contenidoRecibido = receive.getData();
                        if(contenidoRecibido[0]==0 && contenidoRecibido[1]==5){
                            System.out.println("Error recibico");
                            receivedResponse = false;
                            break;
                        }
                        if (!receive.getSocketAddress().equals(fullAddress)) {
                            System.out.println("Emisor desconocido");
                            bytes_to_send = new ERROR((short) 02, "Emisor desconocido").getContenidoPaquete();
                            DatagramPacket request = new DatagramPacket(bytes_to_send, bytes_to_send.length, address, port);
                            socket.send(request);
                            receivedResponse = false;
                            break;
                        }
                    } catch (SocketTimeoutException ex) {
                        tries++;
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                } while ((!receivedResponse) && (tries < MAXRETRY));

                socket.setSoTimeout(0);
                if (!receivedResponse) break;
                if (receive.getSocketAddress().equals(fullAddress)) {
                    packet_num++;
                }

            } while (data.getData().length >= 512);

        }catch(IOException ex){
            ex.printStackTrace();
        }

    }

    //TODO error on the client
    /*
     * > WRQ file.txt ascii
     * > DATA 1 512bytes
     * < ACK 1
     * > DATA 2 507bytes
     * > DATA (R) 2 507bytes
     * > DATA (R) 2 507bytes
     * 3paquetes perdidos, 1paquetes retransmitidos
     */
    private static void metodoPut(DatagramSocket socket, DatagramPacket receive){
        try {
            DatagramPacket send;
            byte[] bytes_to_send;
            byte[] buffer = new byte[0];
            boolean receivedResponse;
            String filename;
            WRQ packet = new WRQ(receive.getData());
            filename = packet.getFilename();

            int packet_num = 0;
            DATA d = null;

            do {
                int tries = 0;
                receivedResponse = false;
                bytes_to_send = new ACK((short) packet_num).getContenidoPaquete();

                do {
                    socket.setSoTimeout(TIMEOUTTIME);

                    try {
                        send = new DatagramPacket(bytes_to_send, bytes_to_send.length, receive.getAddress(), receive.getPort());
                        socket.send(send);

                        System.out.println("Paquete enviado");
                        receive = new DatagramPacket(new byte[TAM_MAX], TAM_MAX);
                        socket.receive(receive);

                        if(packet_num != (new ACK(receive.getData()).getBlocknum())){
                            System.out.println("Paquete enviado fuera de orden.");
                            bytes_to_send = new ERROR((short) 02, "Paquete enviado fuera de orden").getContenidoPaquete();
                            DatagramPacket request = new DatagramPacket(bytes_to_send, bytes_to_send.length, address, port);
                            socket.send(request);
                            receivedResponse = false;
                            break;
                        }

                        /*  Checks the condition that the packet is from the same address and the same port that the initial
                         *  connection otherwise it will completely ignore the packet and continue with the execution */
                        if (receive.getSocketAddress().equals(fullAddress)) {
                            d = new DATA(receive.getData());
                            buffer = joinByteArrays(buffer, d.getData());
                            packet_num++;
                            receivedResponse = true;
                            System.out.println("Paquete recibido");
                        } else{
                            bytes_to_send = new ERROR((short) 02, "Emisor desconocido").getContenidoPaquete();
                            DatagramPacket request = new DatagramPacket(bytes_to_send, bytes_to_send.length, address, port);
                            socket.send(request);
                            receivedResponse = false;
                            break;
                        }


                        byte[] contenidoRecibido = receive.getData();
                        if(contenidoRecibido[0]==0 && contenidoRecibido[1]==5){
                            System.out.println("Error recibido cerrando conexion.");
                            receivedResponse = false;
                            break;
                        }


                    } catch (SocketTimeoutException e) {
                        tries++;
                        System.out.println("Timeout, " + (TAM_MAX - tries) + "left.");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                } while ((!receivedResponse) && (tries < MAXRETRY));

                socket.setSoTimeout(0);
                if(!receivedResponse) break;

            } while (d.getData().length >= 512);

            if (receivedResponse) CrearFichero.crearFichero(filename, buffer);
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }



    private static byte[] joinByteArrays(byte[] array1, byte[] array2){
        byte[] joinedArray = Arrays.copyOf(array1, array1.length + array2.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }



}