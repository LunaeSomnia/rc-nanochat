package messageML;

/*
<message>
    <opcode>6</opcode>
</message>
*/

public class NCExitRoom extends NCMessage {

    public NCExitRoom(byte opcode) {
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

    public static NCExitRoom readFromString(byte code, String message) {
        return new NCExitRoom(code);
    }

}
