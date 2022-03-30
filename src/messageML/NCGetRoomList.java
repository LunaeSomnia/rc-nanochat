package messageML;

/*
<message>
    <opcode>7</opcode>
</message>
*/

public class NCGetRoomList extends NCMessage {

    public NCGetRoomList(byte opcode) {
        this.opcode = opcode;
    }

    @Override
    public String toEncodedString() {
        StringBuffer sb = new StringBuffer();

        sb.append("<" + MESSAGE_MARK + ">" + END_LINE);
        sb.append("<" + OPERATION_MARK + ">" + opcodeToString(opcode) + "</" + OPERATION_MARK + ">" + END_LINE);
        sb.append("</" + MESSAGE_MARK + ">" + END_LINE);

        return sb.toString();

    }

    public static NCGetRoomList readFromString(byte code, String message) {
        return new NCGetRoomList(code);
    }

}
