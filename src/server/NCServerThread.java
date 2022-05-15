package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import messageML.NCControl;
import messageML.NCListaSala;
import messageML.NCMessage;
import messageML.NCUnParametro;
import server.roomManager.NCRoomDescription;
import server.roomManager.NCRoomManager;

/**
 * A new thread runs for each connected client
 */
public class NCServerThread extends Thread {

    private Socket socket = null;
    // Manager global compartido entre los Threads
    private NCServerManager serverManager = null;
    // Input and Output Streams
    private DataInputStream dis;
    private DataOutputStream dos;
    // Usuario actual al que atiende este Thread
    String user;
    // RoomManager actual (dependerá de la sala a la que entre el usuario)
    NCRoomManager roomManager;
    // Sala actual
    String currentRoom;

    // Inicialización de la sala
    public NCServerThread(NCServerManager manager, Socket socket) throws IOException {
        super("NCServerThread");
        this.socket = socket;
        this.serverManager = manager;
    }

    // Main loop
    public void run() {
        try {
            // Se obtienen los streams a partir del Socket
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
            // En primer lugar hay que recibir y verificar el nick
            receiveAndVerifyNickname();
            // Mientras que la conexión esté activa entonces...
            while (true) {
                //// TO!DO Obtenemos el mensaje que llega y analizamos su código de operación
                NCMessage message;
                try {
                    message = NCMessage.readMessageFromSocket(dis);
                } catch (Exception e) {
                    // El usuario ha salido del cliente y no va a poder encontrar otro mensaje
                    throw e;
                }

                switch (message.getOpcode()) {

                    //// TO!DO 1) si se nos pide la lista de salas se envía llamando a
                    //// sendRoomList();
                    case NCMessage.OP_GETROOMLIST:
                        sendRoomList();
                        break;

                    //// TO!DO 2) Si se nos pide entrar en la sala entonces obtenemos el RoomManager
                    //// de
                    //// la sala,
                    case NCMessage.OP_ENTERROOM:

                        NCUnParametro msg = (NCUnParametro) message;

                        roomManager = serverManager.enterRoom(user, msg.getParam(), socket);
                        roomManager.broadcastMessage(NCServerManager.SYSTEM_NAME, user + " entered the room.");

                        //// TO!DO 2) notificamos al usuario que ha sido aceptado y procesamos mensajes
                        if (roomManager != null) {
                            currentRoom = msg.getParam();
                            processRoomMessages();
                        }
                        break;
                }
            }
        } catch (Exception e) {
            // If an error occurs with the communications the user is removed from all the
            // managers and the connection is closed
            System.out.println("* User " + user + " disconnected.");
            serverManager.leaveRoom(user, currentRoom);
            serverManager.removeUser(user);
        } finally {
            if (!socket.isClosed())
                try {
                    socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
        }
    }

    // Obtenemos el nick y solicitamos al ServerManager que verifique si está
    // duplicado
    private void receiveAndVerifyNickname() {
        // La lógica de nuestro programa nos obliga a que haya un nick registrado antes
        // de proseguir
        //// TO!DO Entramos en un bucle hasta comprobar que alguno de los nicks
        //// proporcionados no está duplicado
        boolean valid = false;
        while (!valid) {
            try {
                //// TO!DO Extraer el nick del mensaje
                NCUnParametro nick_msg = (NCUnParametro) NCMessage.readMessageFromSocket(dis);

                //// TO!DO Validar el nick utilizando el ServerManager - addUser()
                valid = serverManager.addUser(nick_msg.getParam());

                //// TO!DO Contestar al cliente con el resultado (éxito o duplicado)
                if (valid) {
                    dos.writeUTF(new NCControl(NCMessage.OP_NICKOK).toEncodedString());
                    user = nick_msg.getParam();
                } else
                    dos.writeUTF(new NCControl(NCMessage.OP_NICKDUPLICATED).toEncodedString());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Mandamos al cliente la lista de salas existentes
    private void sendRoomList() {
        //// TO!DO La lista de salas debe obtenerse a partir del RoomManager y después
        //// enviarse mediante su mensaje correspondiente
        ArrayList<NCRoomDescription> rms = (ArrayList<NCRoomDescription>) serverManager.getRoomList();

        NCListaSala message = new NCListaSala(NCMessage.OP_ROOMLISTINFO, rms);
        String rawMessage = message.toEncodedString();

        try {
            dos.writeUTF(rawMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processRoomMessages() throws IOException {
        //// TO!DO Comprobamos los mensajes que llegan hasta que el usuario decida salir
        //// de
        //// la sala
        boolean exit = false;
        while (!exit) {

            //// TO!DO Se recibe el mensaje enviado por el usuario
            NCMessage message = NCMessage.readMessageFromSocket(dis);

            //// TO!DO Se analiza el código de operación del mensaje y se trata en
            //// consecuencia
            switch (message.getOpcode()) {
                case NCMessage.OP_GETROOMINFO:

                    var info = serverManager.getRoomList();
                    NCListaSala packet = (NCListaSala) NCMessage.makeRoomList(NCMessage.OP_ROOMINFO, info);
                    dos.writeUTF(packet.toEncodedString());

                    break;

                case NCMessage.OP_RENAMEROOM:

                    NCUnParametro renameMsg = (NCUnParametro) message;
                    String newRoomName = renameMsg.getParam();
                    serverManager.renameRoom(currentRoom, newRoomName);
                    currentRoom = newRoomName;
                    roomManager.setRoomName(newRoomName);

                    break;

                case NCMessage.OP_SENDROOMMSG:
                    NCUnParametro msg = (NCUnParametro) message;
                    roomManager.broadcastMessage(user, msg.getParam());

                    break;

                case NCMessage.OP_EXITROOM:
                    serverManager.leaveRoom(user, currentRoom);
                    roomManager.broadcastMessage(NCServerManager.SYSTEM_NAME, user + " left the room.");
                    currentRoom = null;
                    exit = true;

                    break;
            }

        }
    }
}
