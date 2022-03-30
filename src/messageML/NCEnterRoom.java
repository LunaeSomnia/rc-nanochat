package messageML;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
<message>
    <opcode>3</opcode>
    <room>ROOM</room>
</message>
*/

public class NCEnterRoom extends NCMessage {

    private String room;

    // Constantes asociadas a las marcas específicas de este tipo de mensaje
    private static final String RE_ROOM = "<room>(.*?)</room>";
    private static final String ROOM_MARK = "room";

    /**
     * Creamos un mensaje de tipo Room a partir del código de operación y del nombre
     */
    public NCEnterRoom(byte opcode, String room) {
        this.opcode = opcode;
        this.room = room;
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
        sb.append("<" + ROOM_MARK + ">" + room + "</" + ROOM_MARK + ">" + END_LINE);
        sb.append("</" + MESSAGE_MARK + ">" + END_LINE);

        return sb.toString(); // Se obtiene el mensaje

    }

    // Parseamos el mensaje contenido en message con el fin de obtener los distintos
    // campos
    public static NCEnterRoom readFromString(byte code, String message) {
        String found_room = null;

        // Tienen que estar los campos porque el mensaje es de tipo RoomMessage
        Pattern pat_room = Pattern.compile(RE_ROOM);
        Matcher mat_room = pat_room.matcher(message);
        if (mat_room.find()) {
            // Room found
            found_room = mat_room.group(1);
        } else {
            System.out.println("Error en EnterRoom: no se ha encontrado parametro.");
            return null;
        }

        return new NCEnterRoom(code, found_room);
    }

    // Devolvemos el nombre contenido en el mensaje
    public String getRoom() {
        return room;
    }

}
