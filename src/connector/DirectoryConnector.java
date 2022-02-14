package connector;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

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
        /*
         * 
         * // get a datagram socket
         * DatagramSocket socket = new DatagramSocket();
         * // allocate buffer and prepare message to be sent
         * byte[] req = new byte [MAX_MSG_SIZE_BYTES];
         * // send request
         * InetSocketAddress addr =
         * new InetSocketAddress(InetAddress.getByName(serverName),
         * PORT);
         * DatagramPacket packet = new DatagramPacket(req, req.length, addr);
         * socket.send(packet);
         * // receive response
         * byte[] response = new byte [MAX_MSG_SIZE_BYTES];
         * packet = new DatagramPacket(response, response.length);
         * socket.setSoTimeout(1000);
         * socket.receive(packet);
         * // Do something with response in “response”
         * ByteArrayInputStream response =
         * new ByteArrayInputStream(packet.getData());
         * ...
         * socket.close();
         * 
         * 
         */
    }

    /**
     * Envía una solicitud para obtener el servidor de chat asociado a un
     * determinado protocolo
     * 
     */
    public InetSocketAddress getServerForProtocol(int protocol) throws IOException {

        //// TO!DO Generar el mensaje de consulta llamando a buildQuery()
        byte[] msg = buildQuery(0);

        //// TO!DO Construir el datagrama con la consulta
        DatagramPacket packet = new DatagramPacket(msg, msg.length, directoryAddress);

        //// TO!DO Enviar datagrama por el socket
        socket.send(packet);

        System.out.println("DBG: Sending message");

        //// TO!DO preparar el buffer para la respuesta
        byte[] response = new byte[PACKET_MAX_SIZE];
        DatagramPacket r_packet = new DatagramPacket(response, response.length);

        //// TO!DO Establecer el temporizador para el caso en que no haya respuesta
        socket.setSoTimeout(1000);

        try {
            socket.receive(r_packet); // Esperamos 1000ms a recibir

            //// TO!DO Recibir la respuesta
            System.out.println("DBG: Bounced message received");

        } catch (IOException e) {

            // Si el timeout se ha excecido: reenviamos el mismo paquete

            System.out.println("DBG: Timeout exceeded. Resending...");
            socket.send(packet);

            try {
                socket.receive(r_packet);
            } catch (IOException i) {
                System.out.println("DBG: The package wasn't able to be bounced back.");
            }

        }

        // TODO Procesamos la respuesta para devolver la dirección que hay en ella

        return null;
    }

    // Método para generar el mensaje de consulta (para obtener el servidor asociado
    // a un protocolo)
    private byte[] buildQuery(int protocol) {
        // TODO Devolvemos el mensaje codificado en binario según el formato acordado

        byte[] msg = new byte[PACKET_MAX_SIZE];

        for (int i = 0; i < 20; i++) {
            msg[i] = (byte) 1;
        }

        return msg;
    }

    // Método para obtener la dirección de internet a partir del mensaje UDP de
    // respuesta
    private InetSocketAddress getAddressFromResponse(DatagramPacket packet) throws UnknownHostException {
        // TODO Analizar si la respuesta no contiene dirección (devolver null)
        // TODO Si la respuesta no está vacía, devolver la dirección (extraerla del
        // mensaje)
        return null;
    }

    /**
     * Envía una solicitud para registrar el servidor de chat asociado a un
     * determinado protocolo
     * 
     */
    public boolean registerServerForProtocol(int protocol, int port) throws IOException {

        // TODO Construir solicitud de registro (buildRegistration)
        // TODO Enviar solicitud
        // TODO Recibe respuesta
        // TODO Procesamos la respuesta para ver si se ha podido registrar correctamente
        return false;
    }

    // Método para construir una solicitud de registro de servidor
    // OJO: No hace falta proporcionar la dirección porque se toma la misma desde la
    // que se envió el mensaje
    private byte[] buildRegistration(int protocol, int port) {
        // TODO Devolvemos el mensaje codificado en binario según el formato acordado
        return null;
    }

    public void close() {
        socket.close();
    }
}
