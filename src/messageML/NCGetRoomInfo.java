package messageML;

/*
<message>
    <opcode>9</opcode>
</message>
*/

public class NCGetRoomInfo extends NCMessage {

    private String room;

    public NCGetRoomInfo(byte opcode) {
        this.opcode = opcode;
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
        sb.append("</" + MESSAGE_MARK + ">" + END_LINE);

        return sb.toString(); // Se obtiene el mensaje

    }

    // Parseamos el mensaje contenido en message con el fin de obtener los distintos
    // campos
    public static NCGetRoomInfo readFromString(byte code, String message) {
        return new NCGetRoomInfo(code);
    }

    // Devolvemos el nombre contenido en el mensaje
    public String getRoom() {
        return room;
    }

}
