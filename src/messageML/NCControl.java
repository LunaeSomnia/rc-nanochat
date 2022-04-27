package messageML;

/*
<message>
    <opcode>1</opcode>
</message>
*/

public class NCControl extends NCMessage {

    public NCControl(byte opcode) {
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

    public static NCControl readFromString(byte code, String message) {
        return new NCControl(code);
    }

}
