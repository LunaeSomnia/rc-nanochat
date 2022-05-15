package messageML;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import server.roomManager.NCRoomDescription;

/*
<message>
    <opcode>8</opcode>
    <rooms>
        <room>
            <roomname>ROOMNAME1</roomname>
            <curcap>CURCAP1</curcap>
            <maxcap>MAXCAP1</maxcap>
            <lastmsg>LASTMSG1</lastmsg>
            <nicks>
                <nick>NICK1</nick>
                ...
            </nick>
        </room>
        ...
    </rooms>
</message>
*/

public class NCListaSala extends NCMessage {

    private List<NCRoomDescription> rooms;

    // Constantes asociadas a las marcas específicas de este tipo de mensaje

    private static final String RE_ROOMS = "<rooms>(.*?)</rooms>";
    private static final String ROOMS_MARK = "rooms";

    private static final String RE_ROOM = "<room>\n<roomname>(.*?)</roomname>\n<curcap>(.*?)</curcap>\n<maxcap>(.*?)</maxcap>\n<lastmsg>(.*?)</lastmsg>\n<nicks>(.*?)</nicks>\n</room>";
    private static final String ROOM_MARK = "room";
    private static final String ROOMNAME_MARK = "roomname";
    private static final String CURCAP_MARK = "curcap";
    private static final String MAXCAP_MARK = "maxcap";
    private static final String NICKS_MARK = "nicks";
    private static final String LASTMSG_MARK = "lastmsg";

    private static final String RE_NICK = "<nick>(.*?)</nick>";
    private static final String NICK_MARK = "nick";

    /**
     * Creamos un mensaje de tipo Room a partir del código de operación y del nombre
     */
    public NCListaSala(byte opcode, List<NCRoomDescription> rooms) {

        this.opcode = opcode;
        this.rooms = rooms;
    }

    @Override
    // Pasamos los campos del mensaje a la codificación correcta en lenguaje de
    // marcas
    public String toEncodedString() {
        StringBuffer sb = new StringBuffer();

        sb.append("<" + MESSAGE_MARK + ">" + END_LINE);
        sb.append("<" + OPERATION_MARK + ">" + opcodeToString(opcode) + "</" + OPERATION_MARK + ">" + END_LINE); // Construimos
                                                                                                                 // el
                                                                                                                 // campo

        sb.append("<" + ROOMS_MARK + ">" + END_LINE);
        for (int i = 0; i < rooms.size(); i++) {

            NCRoomDescription room = rooms.get(i);

            sb.append("<" + ROOM_MARK + ">" + END_LINE);

            sb.append("<" + ROOMNAME_MARK + ">" + room.roomName + "</" + ROOMNAME_MARK + ">" + END_LINE);
            sb.append("<" + CURCAP_MARK + ">" + room.members.size() + "</" + CURCAP_MARK + ">" + END_LINE);
            sb.append("<" + MAXCAP_MARK + ">" + room.max_capacity + "</" + MAXCAP_MARK + ">" + END_LINE);
            sb.append("<" + LASTMSG_MARK + ">" + room.timeLastMessage + "</" + LASTMSG_MARK + ">" + END_LINE);

            sb.append("<" + NICKS_MARK + ">" + END_LINE);
            for (String user : room.members) {
                sb.append("<" + NICK_MARK + ">" + user + "</" + NICK_MARK + ">" + END_LINE);
            }
            sb.append("</" + NICKS_MARK + ">" + END_LINE);

            sb.append("</" + ROOM_MARK + ">" + END_LINE);
        }
        sb.append("</" + ROOMS_MARK + ">" + END_LINE);

        sb.append("</" + MESSAGE_MARK + ">" + END_LINE);

        return sb.toString(); // Se obtiene el mensaje

    }

    // Parseamos el mensaje contenido en message con el fin de obtener los distintos
    // campos
    public static NCListaSala readFromString(byte code, String message) {
        LinkedList<NCRoomDescription> found_rooms = new LinkedList<>();

        // Tienen que estar los campos porque el mensaje es de tipo RoomMessage
        Pattern pat_rooms = Pattern.compile(RE_ROOMS, Pattern.DOTALL);
        Matcher mat_rooms = pat_rooms.matcher(message);

        Pattern pat_room = Pattern.compile(RE_ROOM, Pattern.DOTALL);
        Matcher mat_room = pat_room.matcher(message);

        Pattern pat_nick = Pattern.compile(RE_NICK, Pattern.DOTALL);

        if (mat_rooms.find()) {
            // Rooms found
            boolean next = mat_room.find();
            while (next) {

                String room_name = mat_room.group(1);
                int current_capacity = Integer.parseInt(mat_room.group(2));
                int max_capacity = Integer.parseInt(mat_room.group(3));
                long last_msg = Long.parseLong(mat_room.group(4));
                List<String> members = new ArrayList<String>();

                Matcher mat_nick = pat_nick.matcher(mat_room.group(5));

                boolean next_user = mat_nick.find();
                while (next_user) {
                    members.add(mat_nick.group(1));
                    next_user = mat_nick.find();
                }

                NCRoomDescription sala = new NCRoomDescription(room_name, members, current_capacity, max_capacity,
                        last_msg);
                found_rooms.add(sala);

                next = mat_room.find();
            }

        } else {
            System.out.println("Error en RoomListInfo: no se ha encontrado parametro 'rooms'.");
            return null;
        }

        return new NCListaSala(code, found_rooms);
    }

    // Devolvemos el nombre contenido en el mensaje
    public List<NCRoomDescription> getRooms() {
        return rooms;
    }

}