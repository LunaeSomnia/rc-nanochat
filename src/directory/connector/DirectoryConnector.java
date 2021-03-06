package directory.connector;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

/**
 * Cliente con métodos de consulta y actualización específicos del directorio
 */
public class DirectoryConnector {
    // Tamaño máximo del paquete UDP (los mensajes intercambiados son muy cortos)
    private static final int PACKET_MAX_SIZE = 128;
    // Puerto en el que atienden los servidores de directorio
    private static final int DEFAULT_PORT = 6868;
    // Valor del TIMEOUT
    private static final int TIMEOUT = 1000;

    private DatagramSocket socket; // socket UDP
    private InetSocketAddress directoryAddress; // dirección del servidor de directorio

    public DirectoryConnector(String agentAddress) throws IOException {
        //// TO!DO A partir de la dirección y del puerto generar la dirección de
        //// conexión
        //// para el Socket

        directoryAddress = new InetSocketAddress(InetAddress.getByName(agentAddress), DEFAULT_PORT);

        //// TO!DO Crear el socket UDP
        socket = new DatagramSocket();

    }

    /**
     * Envía una solicitud para obtener el servidor de chat asociado a un
     * determinado protocolo
     * 
     */
    public InetSocketAddress getServerForProtocol(int protocol) throws IOException {

        //// TO!DO Generar el mensaje de consulta llamando a buildQuery()
        byte[] msg = buildQuery(protocol);

        //// TO!DO Construir el datagrama con la consulta
        DatagramPacket packet = new DatagramPacket(msg, msg.length, directoryAddress);

        //// TO!DO Enviar datagrama por el socket
        socket.send(packet);

        //// TO!DO preparar el buffer para la respuesta
        byte[] response = new byte[PACKET_MAX_SIZE];
        DatagramPacket r_packet = new DatagramPacket(response, response.length);

        //// TO!DO Establecer el temporizador para el caso en que no haya respuesta
        socket.setSoTimeout(TIMEOUT);

        boolean received = false;
        try {
            //// TO!DO Recibir la respuesta
            socket.receive(r_packet); // Esperamos 1000ms a recibir
            received = true;

        } catch (IOException e) {

            // Si el timeout se ha excecido: reenviamos el mismo paquete
            socket.send(packet);

            try {
                socket.receive(r_packet);
                received = true;
            } catch (IOException i) {
                System.out.println("* Timeout exceeded.");
            }

        }

        if (received) {
            //// TO!DO Procesamos la respuesta para devolver la dirección que hay en ella
            return getAddressFromResponse(r_packet);
        }

        return null;
    }

    // Método para generar el mensaje de consulta (para obtener el servidor asociado
    // a un protocolo)
    private byte[] buildQuery(int protocol) {
        //// TO!DO Devolvemos el mensaje codificado en binario según el formato acordado

        ByteBuffer bf = ByteBuffer.allocate(5);

        bf.put((byte) 3);

        bf.putInt(protocol);

        return bf.array();
    }

    // Método para obtener la dirección de internet a partir del mensaje UDP de
    // respuesta
    private InetSocketAddress getAddressFromResponse(DatagramPacket packet) throws UnknownHostException {

        ByteBuffer bf = ByteBuffer.wrap(packet.getData());

        byte type = bf.get();

        if (type == 4) {

            //// TO!DO Si la respuesta no está vacía, devolver la dirección (extraerla del
            //// mensaje)
            byte[] addr = new byte[4];
            for (int i = 0; i < 4; i++) {
                addr[i] = bf.get();
            }

            int port = bf.getInt();

            return new InetSocketAddress(InetAddress.getByAddress(addr), port);
        } else { // type = 5
            // Respuesta vacía
            System.out.println(" - Server did not exist.");
        }
        return null;

    }

    /**
     * Envía una solicitud para registrar el servidor de chat asociado a un
     * determinado protocolo
     * 
     */
    public boolean registerServerForProtocol(int protocol, int port) throws IOException {

        //// TO!DO Construir solicitud de registro (buildRegistration)
        byte[] msg = buildRegistration(protocol, port);

        //// TO!DO Enviar solicitud
        DatagramPacket packet = new DatagramPacket(msg, msg.length, directoryAddress);
        socket.send(packet);

        //// TO!DO Recibe respuesta
        byte[] response = new byte[PACKET_MAX_SIZE];
        DatagramPacket r_packet = new DatagramPacket(response, response.length);
        socket.setSoTimeout(TIMEOUT);

        socket.receive(r_packet);
        //// TO!DO Procesamos la respuesta para ver si se ha podido registrar
        //// correctamente
        ByteBuffer bf = ByteBuffer.wrap(r_packet.getData());

        byte type = bf.get();

        return type != 1;
    }

    // Método para construir una solicitud de registro de servidor
    // OJO: No hace falta proporcionar la dirección porque se toma la misma desde la
    // que se envió el mensaje
    private byte[] buildRegistration(int protocol, int port) {
        //// TO!DO Devolvemos el mensaje codificado en binario según el formato acordado
        ByteBuffer bf = ByteBuffer.allocate(9);

        bf.put((byte) 2);

        bf.putInt(protocol);

        bf.putInt(port);

        return bf.array();
    }

    public void close() {
        socket.close();
    }
}
