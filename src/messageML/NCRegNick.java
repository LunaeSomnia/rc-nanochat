package messageML;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
<msg>
    <opcode>0</opcode>
    <nick>NICK</nick>
</msg>
*/

public class NCRegNick extends NCMessage {

    private String nick;

    private static final String RE_NICK = "<nick>(.*?)</nick>";
    private static final String NICK_MARK = "nick";

    /**
     * Creamos un mensaje de tipo Room a partir del código de operación y del nombre
     */
    public NCRegNick(byte opcode, String nick) {
        this.opcode = opcode;
        this.nick = nick;
    }

    @Override
    public String toEncodedString() {
        StringBuffer sb = new StringBuffer();

        sb.append("<" + MESSAGE_MARK + ">" + END_LINE);
        sb.append("<" + OPERATION_MARK + ">" + opcodeToString(opcode) + "</" + OPERATION_MARK + ">" + END_LINE); // Construimos
                                                                                                                 // el
                                                                                                                 // campo
        sb.append("<" + NICK_MARK + ">" + nick + "</" + NICK_MARK + ">" + END_LINE);
        sb.append("</" + MESSAGE_MARK + ">" + END_LINE);

        return sb.toString();

    }

    public static NCRegNick readFromString(byte code, String message) {
        Pattern pat_nick = Pattern.compile(RE_NICK);
        Matcher mat_nick = pat_nick.matcher(message);

        String found_nick = null;
        if (mat_nick.find()) {
            found_nick = mat_nick.group(1);
        } else {
            System.out.println("Error en RegNick: no se ha encontrado parametro 'nick'.");
            return null;
        }

        return new NCRegNick(code, found_nick);
    }

    public String getNick() {
        return nick;
    }

}
