package server.roomManager;

import java.time.Instant;
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
        sb.append(" - " + roomName + " (" + current_capacity + "/" + max_capacity + ") ");
        if (timeLastMessage != 0)
            sb.append("Last message: " + Date.from(Instant.ofEpochSecond(timeLastMessage)).toString());
        else
            sb.append("No messages yet");
        if (members.size() != 0) {
            sb.append("\n   > ");
            for (String member : members) {
                if (!members.get(0).equals(member))
                    sb.append(", ");
                sb.append(member);
            }
        }

        return sb.toString();
    }
}