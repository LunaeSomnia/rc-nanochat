package messageML;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
<message>
    <opcode>0</opcode>
    <text>TEXT</text>
</message>
*/

public class NCSendRoomMsg extends NCMessage {

    private String text;

    private static final String RE_TEXT = "<text>(.*?)</text>";
    private static final String TEXT_MARK = "text";

    /**
     * Creamos un mensaje de tipo Room a partir del código de operación y del nombre
     */
    public NCSendRoomMsg(byte opcode, String text) {
        this.opcode = opcode;
        this.text = text;
    }

    @Override
    public String toEncodedString() {
        StringBuffer sb = new StringBuffer();

        sb.append("<" + MESSAGE_MARK + ">" + END_LINE);
        sb.append("<" + OPERATION_MARK + ">" + opcodeToString(opcode) + "</" + OPERATION_MARK + ">" + END_LINE); // Construimos
                                                                                                                 // el
                                                                                                                 // campo
        sb.append("<" + TEXT_MARK + ">" + text + "</" + TEXT_MARK + ">" + END_LINE);
        sb.append("</" + MESSAGE_MARK + ">" + END_LINE);

        return sb.toString();

    }

    public static NCSendRoomMsg readFromString(byte code, String message) {
        Pattern pat_text = Pattern.compile(RE_TEXT);
        Matcher mat_text = pat_text.matcher(message);

        String found_text = null;
        if (mat_text.find()) {
            found_text = mat_text.group(1);
        } else {
            System.out.println("Error en SendRoomMsg: no se ha encontrado parametro 'text'.");
            return null;
        }

        return new NCSendRoomMsg(code, found_text);
    }

    public String getText() {
        return text;
    }

}
