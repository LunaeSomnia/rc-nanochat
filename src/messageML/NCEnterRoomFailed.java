package messageML;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
<message>
    <opcode>5</opcode>
    <reason>REASON</reason>
</message>
*/

public class NCEnterRoomFailed extends NCMessage {

    private String reason;

    private static final String REASON_MARK = "reason";
    private static final String RE_REASON = "<reason>(.*?)</reason>";

    public NCEnterRoomFailed(byte opcode, String reason) {
        this.opcode = opcode;
        this.reason = reason;
    }

    @Override
    public String toEncodedString() {
        StringBuffer sb = new StringBuffer();

        sb.append("<" + MESSAGE_MARK + ">" + END_LINE);
        sb.append("<" + OPERATION_MARK + ">" + opcodeToString(opcode) + "</" + OPERATION_MARK + ">" + END_LINE);
        sb.append("<" + REASON_MARK + ">" + reason + "</" + REASON_MARK + ">" + END_LINE);
        sb.append("</" + MESSAGE_MARK + ">" + END_LINE);

        return sb.toString();

    }

    public static NCEnterRoomFailed readFromString(byte code, String message) {
        String found_reason = null;

        // Tienen que estar los campos porque el mensaje es de tipo RoomMessage
        Pattern pat_reason = Pattern.compile(RE_REASON);
        Matcher mat_reason = pat_reason.matcher(message);
        if (mat_reason.find()) {
            // Room found
            found_reason = mat_reason.group(1);
        } else {
            System.out.println("Error en EnterRoomFailed: no se ha encontrado parametro.");
            return null;
        }
        return new NCEnterRoomFailed(code, found_reason);
    }

}
