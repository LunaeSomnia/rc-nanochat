package messageML;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
<message>
    <opcode>10</opcode>
    <curcap>CURCAP1</curcap>
    <maxcap>MAXCAP1</maxcap>
    <nicks>
        <nick>NICK1</nick>
        ...
    </nicks>
</message>
*/

public class NCRoomInfo extends NCMessage {

    private int cur_capacity;
    private int max_capacity;
    private String[] nicks;

    // Constantes asociadas a las marcas específicas de este tipo de mensaje

    private static final String RE_NICKS = "<nicks>(.*?)</nicks>";
    private static final String NICKS_MARK = "nicks";

    private static final String RE_NICK = "<nick>(.*?)</nick>";
    private static final String NICK_MARK = "nick";

    private static final String RE_CURCAP = "<curcap>(.*?)</curcap>";
    private static final String CURCAP_MARK = "curcap";

    private static final String RE_MAXCAP = "<maxcap>(.*?)</maxcap>";
    private static final String MAXCAP_MARK = "maxcap";

    /**
     * Creamos un mensaje de tipo Nick a partir del código de operación y del nombre
     */
    public NCRoomInfo(byte opcode, int curr, int max, String[] nicks) {
        this.opcode = opcode;
        this.nicks = nicks;
        cur_capacity = curr;
        max_capacity = max;
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

        sb.append("<" + CURCAP_MARK + ">" + cur_capacity + "</" + CURCAP_MARK + ">" + END_LINE);
        sb.append("<" + MAXCAP_MARK + ">" + max_capacity + "</" + MAXCAP_MARK + ">" + END_LINE);

        sb.append("<" + NICKS_MARK + ">" + END_LINE);
        for (String nick : nicks) {

            sb.append("<" + NICK_MARK + ">" + nick + "</" + NICK_MARK + ">" + END_LINE);
        }
        sb.append("</" + NICKS_MARK + ">" + END_LINE);

        sb.append("</" + MESSAGE_MARK + ">" + END_LINE);

        return sb.toString(); // Se obtiene el mensaje

    }

    // Parseamos el mensaje contenido en message con el fin de obtener los distintos
    // campos
    public static NCRoomInfo readFromString(byte code, String message) {
        LinkedList<String> found_nicks = new LinkedList<>();
        int found_curcapacity = -1;
        int found_maxcapacity = -1;

        // Tienen que estar los campos porque el mensaje es de tipo NickMessage
        Pattern pat_curcap = Pattern.compile(RE_CURCAP);
        Matcher mat_curcap = pat_curcap.matcher(message);

        Pattern pat_maxcap = Pattern.compile(RE_MAXCAP);
        Matcher mat_maxcap = pat_maxcap.matcher(message);

        Pattern pat_nicks = Pattern.compile(RE_NICKS);
        Matcher mat_nicks = pat_nicks.matcher(message);

        Pattern pat_nick = Pattern.compile(RE_NICK);
        Matcher mat_nick = pat_nick.matcher(message);

        found_curcapacity = Integer.parseInt(mat_curcap.group(1));
        found_maxcapacity = Integer.parseInt(mat_maxcap.group(1));

        if (mat_nicks.find()) {
            // Nicks found
            boolean next = mat_nick.find();
            while (next) {

                found_nicks.add(mat_nick.group(1));

                next = mat_nick.find();
            }

        } else {
            System.out.println("Error en RoomInfo: no se ha encontrado parametro.");
            return null;
        }

        return new NCRoomInfo(code, found_curcapacity, found_maxcapacity,
                (String[]) found_nicks.toArray());
    }

    // Devolvemos el nombre contenido en el mensaje
    public String[] getNicks() {
        return nicks;
    }

    public int getCurCapacities() {
        return cur_capacity;
    }

    public int getMaxCapacities() {
        return max_capacity;
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
