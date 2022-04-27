package server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import messageML.NCMessage;
import messageML.NCUnParametro;
import server.roomManager.NCRoom;
import server.roomManager.NCRoomDescription;
import server.roomManager.NCRoomManager;

/**
 * Esta clase contiene el estado general del servidor (sin la lógica relacionada
 * con cada sala particular)
 */
class NCServerManager {

    final static int ROOM_SIZE = 10;

    // Primera habitación del servidor
    final static int INITIAL_ROOM = 0;
    final static String ROOM_PREFIX = "Room";
    // Siguiente habitación que se creará
    int nextRoom;
    // Usuarios registrados en el servidor
    private Set<String> users = new HashSet<String>();
    // Habitaciones actuales asociadas a sus correspondientes RoomManagers
    private Map<String, NCRoomManager> rooms = new HashMap<String, NCRoomManager>();

    private String last_room;

    NCServerManager() {
        nextRoom = INITIAL_ROOM;
    }

    // Método para registrar un RoomManager
    public void registerRoomManager(NCRoomManager rm) {
        String roomName = ROOM_PREFIX + nextRoom;
        rooms.put(roomName, rm);
        rm.setRoomName(roomName);
        //// TO!DO Dar soporte para que pueda haber más de una sala en el servidor
        nextRoom++;

        last_room = roomName;
    }

    // Devuelve la descripción de las salas existentes
    public synchronized List<NCRoomDescription> getRoomList() {

        ArrayList<NCRoomDescription> lista = new ArrayList<>();

        for (NCRoomManager rm : rooms.values()) {
            //// TO!DO Pregunta a cada RoomManager cuál es la descripción actual de su sala
            //// TO!DO Añade la información al ArrayList
            lista.add(rm.getDescription());
        }

        return lista;
    }

    // Intenta registrar al usuario en el servidor.
    public synchronized boolean addUser(String user) {
        //// TO!DO Devuelve true si no hay otro usuario con su nombre
        if (!users.contains(user)) {
            users.add(user);
            return true;
        }
        //// TO!DO Devuelve false si ya hay un usuario con su nombre
        return false;
    }

    // Elimina al usuario del servidor
    public synchronized void removeUser(String user) {
        //// TO!DO Elimina al usuario del servidor
        if (users.contains(user)) {
            users.remove(user);
        }
    }

    // Un usuario solicita acceso para entrar a una sala y registrar su conexión en
    // ella
    public synchronized NCRoomManager enterRoom(String u, String room, Socket s) throws IOException {

        String error_reason = null;

        //// TO!DO Verificamos si la sala existe
        if (rooms.containsKey(room)) {
            if (room == last_room) {
                registerRoomManager(new NCRoom(ROOM_SIZE));
            }

            //// TO!DO Si la sala existe y si es aceptado en la sala entonces devolvemos el
            //// RoomManager de la sala

            NCRoomManager room_manager = rooms.get(room);

            if (room_manager.registerUser(u, s)) {
                return room_manager;
            } else
                error_reason = "Room is full";
        } else {
            //// TO!DO Decidimos qué hacer si la sala no existe (devolver error O crear la
            //// sala)
            error_reason = "Room does not exist";
        }

        DataOutputStream dos = new DataOutputStream(s.getOutputStream());
        dos.writeUTF(new NCUnParametro(NCMessage.OP_ENTERROOMFAILED, error_reason).toEncodedString());
        return null;

    }

    // Un usuario deja la sala en la que estaba
    public synchronized void leaveRoom(String u, String room) {
        //// TO!DO Verificamos si la sala existe
        if (rooms.containsKey(room)) {
            //// TO!DO Si la sala existe sacamos al usuario de la sala
            rooms.get(room).removeUser(u);

            //// TO!DO Decidir qué hacer si la sala se queda vacía
            rooms.remove(room);
        }
    }
}
