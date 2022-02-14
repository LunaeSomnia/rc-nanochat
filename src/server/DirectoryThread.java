package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

public class DirectoryThread extends Thread {

    // Tamaño máximo del paquete UDP
    private static final int PACKET_MAX_SIZE = 128;
    // Estructura para guardar las asociaciones ID_PROTOCOLO -> Dirección del
    // servidor
    protected Map<Integer, InetSocketAddress> servers;

    // Socket de comunicación UDP
    protected DatagramSocket socket = null;
    // Probabilidad de descarte del mensaje
    protected double messageDiscardProbability;

    public DirectoryThread(String name, int directoryPort,
            double corruptionProbability)
            throws SocketException {
        super(name);
        //// TO!DO Anotar la dirección en la que escucha el servidor de Directorio
        InetSocketAddress serverAddress = new InetSocketAddress(directoryPort);

        //// TO!DO Crear un socket de servidor
        this.socket = new DatagramSocket(serverAddress);

        this.messageDiscardProbability = corruptionProbability;
        // Inicialización del mapa
        this.servers = new HashMap<Integer, InetSocketAddress>();
    }

    @Override
    public void run() {
        byte[] buf = new byte[PACKET_MAX_SIZE];

        System.out.println("Directory starting...");
        boolean running = true;
        while (running) {

            try {
                //// TO!DO 1) Recibir la solicitud por el socket
                DatagramPacket pckt = new DatagramPacket(buf, buf.length);
                // Receive request message
                socket.receive(pckt);

                //// TO!DO 2) Extraer quién es el cliente (su dirección)
                InetSocketAddress ca = (InetSocketAddress) pckt.getSocketAddress();

                System.out.println("DBG: Package received from address '" + ca + "'");

                //// TO!DO 3) Vemos si el mensaje debe ser descartado por la probabilidad de
                //// descarte
                if (Math.random() > messageDiscardProbability) {
                    System.err.println("Directory DISCARDED corrupt request from... ");
                    continue;
                }

                //// TO!DO (Solo Boletín 2) Devolver una respuesta idéntica en contenido a la
                //// solicitud
                pckt = new DatagramPacket(buf, buf.length, ca);
                socket.send(pckt);

                System.out.println("DBG: Bouncing back the package...");

                // TODO 4) Analizar y procesar la solicitud (llamada a processRequestFromCLient)

                // DEBUGGING: Si recivimos el ejemplo (20 unos) se cierra el socket

                // Si recibimos uno de los packetes correctamente, salimos.
                byte[] datos = pckt.getData();
                boolean salir = true;
                for (int i = 0; i < 20; i++) {
                    salir &= datos[i] == 1;
                }

                if (salir) {
                    System.out.println("DBG: Received message told us to close... so we did.");
                    running = false;
                }

            } catch (IOException e) {
                System.err.println("DBG: It doesn't work :sadface:");
            }

            //// TO!DO 5) Tratar las excepciones que puedan producirse
        }
        // Closes this datagram socket
        socket.close();
        System.out.println("DBG: Closing the socket");
    }

    // Método para procesar la solicitud enviada por clientAddr
    public void processRequestFromClient(byte[] data, InetSocketAddress clientAddr) throws IOException {
        // TODO 1) Extraemos el tipo de mensaje recibido
        // TODO 2) Procesar el caso de que sea un registro y enviar mediante sendOK
        // TODO 3) Procesar el caso de que sea una consulta
        // TODO 3.1) Devolver una dirección si existe un servidor (sendServerInfo)
        // TODO 3.2) Devolver una notificación si no existe un servidor (sendEmpty)
    }

    // Método para enviar una respuesta vacía (no hay servidor)
    private void sendEmpty(InetSocketAddress clientAddr) throws IOException {
        // TODO Construir respuesta
        // TODO Enviar respuesta
    }

    // Método para enviar la dirección del servidor al cliente
    private void sendServerInfo(InetSocketAddress serverAddress, InetSocketAddress clientAddr) throws IOException {
        // TODO Obtener la representación binaria de la dirección
        // TODO Construir respuesta
        // TODO Enviar respuesta
    }

    // Método para enviar la confirmación del registro
    private void sendOK(InetSocketAddress clientAddr) throws IOException {
        // TODO Construir respuesta
        // TODO Enviar respuesta
    }
}
