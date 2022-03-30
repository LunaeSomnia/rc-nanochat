package client.comm;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import messageML.NCMessage;
import messageML.NCEnterRoom;
import server.roomManager.NCRoomDescription;

//Esta clase proporciona la funcionalidad necesaria para intercambiar mensajes entre el cliente y el servidor de NanoChat
public class NCConnector {
    private Socket socket;

    protected DataOutputStream dos;
    protected DataInputStream dis;

    public NCConnector(InetSocketAddress serverAddress) throws UnknownHostException, IOException {

        //// TO!DO Se crea el socket a partir de la dirección proporcionada
        socket = new Socket(serverAddress.getAddress(), serverAddress.getPort());

        //// TO!DO Se extraen los streams de entrada y salida
        dos = new DataOutputStream(socket.getOutputStream());
        dis = new DataInputStream(socket.getInputStream());
    }

    // Método para registrar el nick en el servidor. Nos informa sobre si la
    // inscripción se hizo con éxito o no.
    public boolean registerNickname_UnformattedMessage(String nick) throws IOException {
        // Funcionamiento resumido: SEND(nick) and RCV(NICK_OK) or RCV(NICK_DUPLICATED)
        //// TO!DO Enviamos una cadena con el nick por el flujo de salida
        NCEnterRoom message = (NCEnterRoom) NCMessage.makeRoomMessage(NCMessage.OP_REGNICK, nick);
        String rawMessage = message.toEncodedString();
        dos.writeUTF(rawMessage);

        //// TO!DO Leemos la cadena recibida como respuesta por el flujo de entrada
        String response = dis.readUTF();
        //// TO!DO Si la cadena recibida es NICK_OK entonces no está duplicado (en
        //// función de ello modificar el return)
        return response.equals("NICK_OK");
    }

    // Método para registrar el nick en el servidor. Nos informa sobre si la
    // inscripción se hizo con éxito o no.
    public boolean registerNickname(String nick) throws IOException {
        // Funcionamiento resumido: SEND(nick) and RCV(NICK_OK) or RCV(NICK_DUPLICATED)
        // Creamos un mensaje de tipo RoomMessage con opcode OP_NICK en el que se
        // inserte el nick
        NCEnterRoom message = (NCEnterRoom) NCMessage.makeRoomMessage(NCMessage.OP_REGNICK, nick);
        // Obtenemos el mensaje de texto listo para enviar
        String rawMessage = message.toEncodedString();
        // Escribimos el mensaje en el flujo de salida, es decir, provocamos que se
        // envíe por la conexión TCP
        dos.writeUTF(rawMessage);
        //// TO!DO Leemos el mensaje recibido como respuesta por el flujo de entrada
        String response = dis.readUTF();
        //// TO!DO Analizamos el mensaje para saber si está duplicado el nick (modificar
        //// el return en consecuencia)
        return response.equals("NICK_OK");

    }

    // Método para obtener la lista de salas del servidor
    public List<NCRoomDescription> getRooms() throws IOException {
        // Funcionamiento resumido: SND(GET_ROOMS) and RCV(ROOM_LIST)
        // TODO completar el método
        return null;
    }

    // Método para solicitar la entrada en una sala
    public boolean enterRoom(String room) throws IOException {
        // Funcionamiento resumido: SND(ENTER_ROOM<room>) and RCV(IN_ROOM) or
        // RCV(REJECT)
        // TODO completar el método
        return true;
    }

    // Método para salir de una sala
    public void leaveRoom(String room) throws IOException {
        // Funcionamiento resumido: SND(EXIT_ROOM)
        // TODO completar el método
    }

    // Método que utiliza el Shell para ver si hay datos en el flujo de entrada
    public boolean isDataAvailable() throws IOException {
        return (dis.available() != 0);
    }

    // IMPORTANTE!!
    // TODO Es necesario implementar métodos para recibir y enviar mensajes de chat
    // a una sala

    // Método para pedir la descripción de una sala
    public NCRoomDescription getRoomInfo(String room) throws IOException {
        // Funcionamiento resumido: SND(GET_ROOMINFO) and RCV(ROOMINFO)
        // TODO Construimos el mensaje de solicitud de información de la sala específica
        // TODO Recibimos el mensaje de respuesta
        // TODO Devolvemos la descripción contenida en el mensaje
        return null;
    }

    // Método para cerrar la comunicación con la sala
    // TODO (Opcional) Enviar un mensaje de salida del servidor de Chat
    public void disconnect() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
        } finally {
            socket = null;
        }
    }

}
