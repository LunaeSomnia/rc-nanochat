package messageML;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
<message>
    <opcode>0</opcode>
    <param>PARAM</param>
</message>
*/

public class NCUnParametro extends NCMessage {

    private String param;

    private static final String RE_PARAM = "<param>(.*?)</param>";
    private static final String PARAM_MARK = "param";

    /**
     * Creamos un mensaje de tipo Room a partir del código de operación y del nombre
     */
    public NCUnParametro(byte opcode, String param) {
        this.opcode = opcode;
        this.param = param;
    }

    @Override
    public String toEncodedString() {
        StringBuffer sb = new StringBuffer();

        sb.append("<" + MESSAGE_MARK + ">" + END_LINE);
        sb.append("<" + OPERATION_MARK + ">" + opcodeToString(opcode) + "</" + OPERATION_MARK + ">" + END_LINE);
        sb.append("<" + PARAM_MARK + ">" + param + "</" + PARAM_MARK + ">" + END_LINE);
        sb.append("</" + MESSAGE_MARK + ">" + END_LINE);

        return sb.toString();

    }

    public static NCUnParametro readFromString(byte code, String message) {
        Pattern pat_param = Pattern.compile(RE_PARAM);
        Matcher mat_param = pat_param.matcher(message);

        String found_param = null;
        if (mat_param.find()) {
            found_param = mat_param.group(1);
        } else {
            System.out.println("Error en NCUnParametro: no se ha encontrado parametro 'param'.");
            return null;
        }

        return new NCUnParametro(code, found_param);
    }

    public String getParam() {
        return param;
    }

}
