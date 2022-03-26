package directory.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
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
                // pckt = new DatagramPacket(buf, buf.length, ca);
                // socket.send(pckt);

                // System.out.println("DBG: Bouncing back the package...");

                System.out.println("DBG: Sending info to package source...");

                //// TO!DO 4) Analizar y procesar la solicitud (llamada a
                //// processRequestFromCLient)
                processRequestFromClient(pckt.getData(), ca);

            } catch (IOException e) {
                //// TO!DO 5) Tratar las excepciones que puedan producirse
                System.err.println("DBG: It doesn't work :sadface:");
            }

        }

        socket.close();
        System.out.println("DBG: Closing the socket");
    }

    // Método para procesar la solicitud enviada por clientAddr
    public void processRequestFromClient(byte[] data, InetSocketAddress clientAddr) throws IOException {

        ByteBuffer bf = ByteBuffer.wrap(data);

        //// TO!DO 1) Extraemos el tipo de mensaje recibido
        byte type = bf.get();

        int protocol = bf.getInt();

        switch (type) {
            //// TO!DO 2) Procesar el caso de que sea un registro y enviar mediante sendOK
            // REGISTER
            case 2:
                int port = bf.getInt();

                if (!servers.containsKey(protocol)) {

                    InetSocketAddress storeAddr = new InetSocketAddress(clientAddr.getAddress(), port);

                    servers.put(protocol, storeAddr);

                    System.out.println("DBG: Server registered with protocol '" + protocol + "'");

                    sendOK(clientAddr);
                } else {

                    System.out.println("DBG: Server with protocol '" + protocol
                            + "' attempted to register to an already existing protocol.");

                    sendNotOK(clientAddr);
                }

                break;
            //// TO!DO 3) Procesar el caso de que sea una consulta
            // QUERY
            case 3:
                if (servers.containsKey(protocol)) {
                    //// TO!DO 3.1) Devolver una dirección si existe un servidor (sendServerInfo)
                    sendServerInfo(servers.get(protocol), clientAddr);
                } else {
                    //// TO!DO 3.2) Devolver una notificación si no existe un servidor (sendEmpty)
                    sendEmpty(clientAddr);
                }
                break;
        }
    }

    // Método para enviar una respuesta vacía (no hay servidor)
    private void sendEmpty(InetSocketAddress clientAddr) throws IOException {
        //// TO!DO Construir respuesta
        ByteBuffer bf = ByteBuffer.allocate(9);

        bf.put((byte) 5);

        byte[] msg = bf.array();

        //// TO!DO Enviar respuesta

        DatagramPacket packet = new DatagramPacket(msg, msg.length, clientAddr);

        socket.send(packet);
    }

    // Método para enviar la dirección del servidor al cliente
    private void sendServerInfo(InetSocketAddress serverAddress, InetSocketAddress clientAddr) throws IOException {
        //// TO!DO Obtener la representación binaria de la dirección
        //// TO!DO Construir respuesta

        ByteBuffer bf = ByteBuffer.allocate(9);

        bf.put((byte) 4);

        // Insertamos los 4 bytes de la dirección IPv4 (QUE SON 4)
        bf.put(serverAddress.getAddress().getAddress());

        bf.putInt(serverAddress.getPort());

        byte[] msg = bf.array();

        //// TO!DO Enviar respuesta

        DatagramPacket packet = new DatagramPacket(msg, msg.length, clientAddr);

        socket.send(packet);
    }

    // Método para enviar la confirmación del registro
    private void sendOK(InetSocketAddress clientAddr) throws IOException {

        //// TO!DO Construir respuesta
        ByteBuffer bf = ByteBuffer.allocate(1);

        bf.put((byte) 0);

        byte[] array = bf.array();

        //// TO!DO Enviar respuesta
        DatagramPacket packet = new DatagramPacket(array, array.length, clientAddr);

        socket.send(packet);
    }

    // Método para enviar la denegación del registro
    private void sendNotOK(InetSocketAddress clientAddr) throws IOException {
        //// TO!DO Construir respuesta
        ByteBuffer bf = ByteBuffer.allocate(1);

        bf.put((byte) 1);

        byte[] msg = bf.array();

        //// TO!DO Enviar respuesta
        DatagramPacket packet = new DatagramPacket(msg, msg.length, clientAddr);

        socket.send(packet);
    }
}
