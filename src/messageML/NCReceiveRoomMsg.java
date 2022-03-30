package messageML;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
<message>
    <opcode>0</opcode>
    <nick>NICK</nick>
    <text>TEXT</text>
</message>
*/

public class NCReceiveRoomMsg extends NCMessage {

    private String nick;
    private String text;

    private static final String RE_NICK = "<nick>(.*?)</nick>";
    private static final String NICK_MARK = "nick";

    private static final String RE_TEXT = "<text>(.*?)</text>";
    private static final String TEXT_MARK = "text";

    /**
     * Creamos un mensaje de tipo Room a partir del código de operación y del nombre
     */
    public NCReceiveRoomMsg(byte opcode, String nick, String text) {
        this.opcode = opcode;
        this.nick = nick;
        this.text = text;
    }

    @Override
    public String toEncodedString() {
        StringBuffer sb = new StringBuffer();

        sb.append("<" + MESSAGE_MARK + ">" + END_LINE);
        sb.append("<" + OPERATION_MARK + ">" + opcodeToString(opcode) + "</" + OPERATION_MARK + ">" + END_LINE); // Construimos
                                                                                                                 // el
                                                                                                                 // campo
        sb.append("<" + NICK_MARK + ">" + nick + "</" + NICK_MARK + ">" + END_LINE);
        sb.append("<" + TEXT_MARK + ">" + text + "</" + TEXT_MARK + ">" + END_LINE);

        sb.append("</" + MESSAGE_MARK + ">" + END_LINE);

        return sb.toString();

    }

    public static NCReceiveRoomMsg readFromString(byte code, String message) {
        Pattern pat_nick = Pattern.compile(RE_NICK);
        Matcher mat_nick = pat_nick.matcher(message);

        Pattern pat_text = Pattern.compile(RE_TEXT);
        Matcher mat_text = pat_text.matcher(message);

        String found_nick = null;
        String found_text = null;

        if (mat_nick.find()) {
            found_nick = mat_nick.group(1);
            if (mat_text.find())
                found_text = mat_text.group(1);
            else
                System.out.println("Error en ReceiveRoomMsg: no se ha encontrado parametro 'text'.");

        } else {
            System.out.println("Error en ReceiveRoomMsg: no se ha encontrado parametro 'nick'.");
            return null;
        }

        return new NCReceiveRoomMsg(code, found_nick, found_text);
    }

    public String getNick() {
        return nick;
    }

    public String getText() {
        return text;
    }

}
