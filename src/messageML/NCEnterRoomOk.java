package messageML;

/*
<message>
    <opcode>4</opcode>
</message>
*/

public class NCEnterRoomOk extends NCMessage {

    public NCEnterRoomOk(byte opcode) {
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

    public static NCEnterRoomOk readFromString(byte code, String message) {
        return new NCEnterRoomOk(code);
    }

}
