package messageML;

/*
<msg>
    <opcode>1</opcode>
</msg>
*/

public class NCNickOk extends NCMessage {

    /**
     * Creamos un mensaje de tipo Room a partir del código de operación y del nombre
     */
    public NCNickOk(byte opcode) {
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

    public static NCNickOk readFromString(byte code, String message) {
        return new NCNickOk(code);
    }

}
