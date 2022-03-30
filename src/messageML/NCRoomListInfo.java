package messageML;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
<message>
    <opcode>8</opcode>
    <rooms>
        <room>
            <roomname>ROOMNAME1</roomname>
            <curcap>CURCAP1</curcap>
            <maxcap>MAXCAP1</maxcap>
        </room>
        ...
    </rooms>
</message>
*/

public class NCRoomListInfo extends NCMessage {

    private String[] rooms;
    private int[] cur_capacities;
    private int[] max_capacities;

    // Constantes asociadas a las marcas específicas de este tipo de mensaje

    private static final String RE_ROOMS = "<rooms>(.*?)</rooms>";
    private static final String ROOMS_MARK = "rooms";

    private static final String RE_ROOM = "<room>(.*?)</room>";
    private static final String ROOM_MARK = "room";

    private static final String RE_ROOMNAME = "<roomname>(.*?)</roomname>";
    private static final String ROOMNAME_MARK = "roomname";

    private static final String RE_CURCAP = "<curcap>(.*?)</curcap>";
    private static final String CURCAP_MARK = "curcap";

    private static final String RE_MAXCAP = "<maxcap>(.*?)</maxcap>";
    private static final String MAXCAP_MARK = "maxcap";

    /**
     * Creamos un mensaje de tipo Room a partir del código de operación y del nombre
     */
    public NCRoomListInfo(byte opcode, String[] rooms, int[] curr, int[] max) {

        if (rooms.length != curr.length || curr.length != max.length)
            System.err.println("Error: RoomListInfo ha encontrado arrays de tamaños diferentes.");

        this.opcode = opcode;
        this.rooms = rooms;
        cur_capacities = curr;
        max_capacities = max;
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
        for (int i = 0; i < rooms.length; i++) {

            sb.append("<" + ROOM_MARK + ">" + END_LINE);

            sb.append("<" + ROOMNAME_MARK + ">" + rooms[i] + "</" + ROOMNAME_MARK + ">" + END_LINE);
            sb.append("<" + CURCAP_MARK + ">" + cur_capacities[i] + "</" + CURCAP_MARK + ">" + END_LINE);
            sb.append("<" + MAXCAP_MARK + ">" + max_capacities[i] + "</" + MAXCAP_MARK + ">" + END_LINE);

            sb.append("</" + ROOMS_MARK + ">" + END_LINE);
        }
        sb.append("</" + ROOMS_MARK + ">" + END_LINE);

        sb.append("</" + MESSAGE_MARK + ">" + END_LINE);

        return sb.toString(); // Se obtiene el mensaje

    }

    // Parseamos el mensaje contenido en message con el fin de obtener los distintos
    // campos
    public static NCRoomListInfo readFromString(byte code, String message) {
        LinkedList<String> found_rooms = new LinkedList<String>();
        LinkedList<Integer> found_curcapacities = new LinkedList<Integer>();
        LinkedList<Integer> found_maxcapacities = new LinkedList<Integer>();

        // Tienen que estar los campos porque el mensaje es de tipo RoomMessage
        Pattern pat_rooms = Pattern.compile(RE_ROOMS);
        Matcher mat_rooms = pat_rooms.matcher(message);

        Pattern pat_room = Pattern.compile(RE_ROOM);
        Matcher mat_room = pat_room.matcher(message);

        Pattern pat_roomname = Pattern.compile(RE_ROOMNAME);
        Matcher mat_roomname = pat_roomname.matcher(message);

        Pattern pat_curcap = Pattern.compile(RE_CURCAP);
        Matcher mat_curcap = pat_curcap.matcher(message);

        Pattern pat_maxcap = Pattern.compile(RE_MAXCAP);
        Matcher mat_maxcap = pat_maxcap.matcher(message);

        if (mat_rooms.find()) {
            // Rooms found
            boolean next = mat_room.find();
            while (next) {

                found_rooms.add(mat_roomname.group(1));
                found_curcapacities.add(Integer.parseInt(mat_curcap.group(1)));
                found_maxcapacities.add(Integer.parseInt(mat_maxcap.group(1)));

                next = mat_room.find();
            }

        } else {
            System.out.println("Error en RoomListInfo: no se ha encontrado parametro.");
            return null;
        }

        return new NCRoomListInfo(code, (String[]) found_rooms.toArray(), toIntArray(found_curcapacities),
                toIntArray(found_maxcapacities));
    }

    // Devolvemos el nombre contenido en el mensaje
    public String[] getRooms() {
        return rooms;
    }

    public int[] getCurCapacities() {
        return cur_capacities;
    }

    public int[] getMaxCapacities() {
        return max_capacities;
    }

    // Helper function
    public static int[] toIntArray(List<Integer> l) {
        int size = l.size();

        int[] out = new int[size];

        for (int i = 0; i < size; i++) {
            out[i] = l.get(i);
        }

        return out;
    }

}