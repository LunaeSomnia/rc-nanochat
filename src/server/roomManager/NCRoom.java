package server.roomManager;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import messageML.NCDosParametros;
import messageML.NCMessage;

public class NCRoom extends NCRoomManager {

    Map<String, Socket> users = new HashMap<String, Socket>();

    public NCRoom(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public boolean registerUser(String u, Socket s) {

        if (!users.containsKey(u) && users.size() < capacity) {
            users.put(u, s);
            return true;
        }

        return false;
    }

    @Override
    public void broadcastMessage(String u, String message) throws IOException {

        last_msg_time = Instant.now().getEpochSecond();
        for (String user : users.keySet()) {
            if (user != u) {
                DataOutputStream dos = new DataOutputStream(users.get(user).getOutputStream());
                dos.writeUTF(new NCDosParametros(NCMessage.OP_RECEIVEROOMMSG, u, message).toEncodedString());
            }
        }

    }

    @Override
    public void removeUser(String u) {

        if (users.containsKey(u)) {
            users.remove(u);
        }
    }

    @Override
    public void setRoomName(String roomName) {
        this.roomName = roomName;

    }

    @Override
    public NCRoomDescription getDescription() {

        ArrayList<String> user_list = new ArrayList<String>();
        user_list.addAll(users.keySet());

        return new NCRoomDescription(this.roomName, user_list, users.size(), capacity, last_msg_time);
    }

    @Override
    public int usersInRoom() {
        return users.size();
    }

}
