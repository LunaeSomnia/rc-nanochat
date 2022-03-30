package messageML;

/*
<message>
    <opcode>2</opcode>
</message>
*/

public class NCNickDuplicated extends NCMessage {

    /**
     * Creamos un mensaje de tipo Room a partir del código de operación y del nombre
     */
    public NCNickDuplicated(byte opcode) {
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

    public static NCNickDuplicated readFromString(byte code, String message) {
        return new NCNickDuplicated(code);
    }

}
