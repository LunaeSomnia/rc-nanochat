package client.comm;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import messageML.NCControl;
import messageML.NCDosParametros;
import messageML.NCListaSala;
import messageML.NCMessage;
import messageML.NCUnParametro;
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
        NCUnParametro message = (NCUnParametro) NCMessage.makeOneParam(NCMessage.OP_REGNICK, nick);
        String rawMessage = message.toEncodedString();
        dos.writeUTF(rawMessage);

        //// TO!DO Leemos el mensaje recibido como respuesta por el flujo de entrada
        String response = dis.readUTF();
        //// TO!DO Analizamos el mensaje para saber si está duplicado el nick (modificar
        //// el return en consecuencia)
        return response.equals("NICK_OK");
    }

    // Método para registrar el nick en el servidor. Nos informa sobre si la
    // inscripción se hizo con éxito o no.
    public boolean registerNickname(String nick) throws IOException {
        // Funcionamiento resumido: SEND(nick) and RCV(NICK_OK) or RCV(NICK_DUPLICATED)
        // Creamos un mensaje de tipo OneParameter con opcode OP_NICK en el que se
        // inserte el nick
        NCUnParametro message = NCMessage.makeOneParam(NCMessage.OP_REGNICK, nick);
        // Obtenemos el mensaje de texto listo para enviar
        String rawMessage = message.toEncodedString();
        // Escribimos el mensaje en el flujo de salida, es decir, provocamos que se
        // envíe por la conexión TCP
        dos.writeUTF(rawMessage);

        //// TO!DO Leemos la cadena recibida como respuesta por el flujo de entrada
        // String response = dis.readUTF();
        NCControl response = (NCControl) NCMessage.readMessageFromSocket(dis);
        //// TO!DO Si la cadena recibida es NICK_OK entonces no está duplicado (en
        //// función de ello modificar el return)
        return response.getOpcode() == NCMessage.OP_NICKOK;

    }

    // Método para obtener la lista de salas del servidor
    public List<NCRoomDescription> getRooms() throws IOException {
        // Funcionamiento resumido: SND(GET_ROOMS) and RCV(ROOM_LIST_INFO)
        //// TO!DO completar el método

        NCControl message = NCMessage.makeControl(NCMessage.OP_GETROOMLIST);
        dos.writeUTF(message.toEncodedString());

        NCListaSala response = (NCListaSala) NCMessage.readMessageFromSocket(dis);
        return response.getRooms();
    }

    // Método para solicitar la entrada en una sala
    public String enterRoom(String room) throws IOException {
        // Funcionamiento resumido: SND(ENTER_ROOM<room>) and RCV(ENTER_ROOM_OK) or
        // RCV(ENTER_ROOM_FAILED)
        //// TO!DO completar el método

        NCUnParametro message = NCMessage.makeOneParam(NCMessage.OP_ENTERROOM, room);
        String rawMessage = message.toEncodedString();
        dos.writeUTF(rawMessage);

        NCMessage response = NCMessage.readMessageFromSocket(dis);

        try {
            // Si es de tipo control, el mensaje es "ENTERROOMOK"
            NCControl response_c = (NCControl) response;
            return null;

        } catch (Exception e) {
            // El mensaje ha sido "ENTERROOMFAILED", con su respectiva razón
            NCUnParametro response_u = (NCUnParametro) response;
            return response_u.getParam();
        }

    }

    // Método para salir de una sala
    public void leaveRoom() throws IOException {
        // Funcionamiento resumido: SND(EXIT_ROOM)
        //// TO!DO completar el método

        NCControl message = NCMessage.makeControl(NCMessage.OP_EXITROOM);
        String rawMessage = message.toEncodedString();
        dos.writeUTF(rawMessage);
    }

    // Método que utiliza el Shell para ver si hay datos en el flujo de entrada
    public boolean isDataAvailable() throws IOException {
        return (dis.available() != 0);
    }

    // IMPORTANTE!!
    //// TO!DO Es necesario implementar métodos para recibir y enviar mensajes de
    //// chat a una sala

    public void sendChatMessage(String msg) {
        try {
            dos.writeUTF(new NCUnParametro(NCMessage.OP_SENDROOMMSG, msg).toEncodedString());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public NCDosParametros receiveChatMessage() {
        try {
            NCMessage readMesage = NCMessage.readMessageFromSocket(dis);
            return (NCDosParametros) readMesage;

        } catch (IOException e) {
            e.printStackTrace();

            return null;
        }
    }

    // Método para pedir la descripción de una sala
    public NCRoomDescription getRoomInfo(String room) throws IOException {
        // Funcionamiento resumido: SND(GET_ROOMINFO) and RCV(ROOMINFO)
        //// TO!DO Construimos el mensaje de solicitud de información de la sala
        //// específica
        NCUnParametro message = NCMessage.makeOneParam(NCMessage.OP_GETROOMINFO, room);
        String rawMessage = message.toEncodedString();
        dos.writeUTF(rawMessage);

        //// TO!DO Recibimos el mensaje de respuesta
        NCListaSala response = (NCListaSala) NCMessage.readMessageFromSocket(dis);

        //// TO!DO Devolvemos la descripción contenida en el mensaje
        return (NCRoomDescription) response.getRooms().stream().filter(r -> r.roomName.equals(room)).toArray()[0];
    }

    // Método para cerrar la comunicación con la sala
    //// TO!DO (Opcional) Enviar un mensaje de salida del servidor de Chat
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
