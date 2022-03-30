package server.roomManager;

import java.io.IOException;
import java.net.Socket;

public class NCRoom extends NCRoomManager {

    int capacity;

    @Override
    public boolean registerUser(String u, Socket s) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void broadcastMessage(String u, String message) throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeUser(String u) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setRoomName(String roomName) {
        // TODO Auto-generated method stub

    }

    @Override
    public NCRoomDescription getDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int usersInRoom() {
        // TODO Auto-generated method stub
        return 0;
    }

}
