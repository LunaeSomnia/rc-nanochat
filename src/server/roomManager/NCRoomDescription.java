package server.roomManager;

import java.util.Date;
import java.util.List;

public class NCRoomDescription {
    // Campos de los que, al menos, se compone una descripción de una sala
    public String roomName;
    public List<String> members;
    public int current_capacity;
    public int max_capacity;
    public long timeLastMessage;

    // Constructor a partir de los valores para los campos
    public NCRoomDescription(String roomName, List<String> members, int current_capacity, int max_capacity,
            long timeLastMessage) {
        this.roomName = roomName;
        this.members = members;

        this.current_capacity = current_capacity;
        this.max_capacity = max_capacity;

        this.timeLastMessage = timeLastMessage;
    }

    // Método que devuelve una representación de la Descripción lista para ser
    // impresa por pantalla
    public String toPrintableString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Room Name: " + roomName + "\t Capacity: " + current_capacity + "/" + max_capacity + "\t Members ("
                + members.size() + ") : ");
        for (String member : members) {
            sb.append(member + " ");
        }
        if (timeLastMessage != 0)
            sb.append("\tLast message: " + new Date(timeLastMessage).toString());
        else
            sb.append("\tLast message: not yet");
        return sb.toString();
    }
}
