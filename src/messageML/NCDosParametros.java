package messageML;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
<message>
    <opcode>0</opcode>
    <param1>PARAM1</param1>
    <param2>PARAM2</param2>
</message>
*/

public class NCDosParametros extends NCMessage {

    private String param1;
    private String param2;

    private static final String RE_PARAM1 = "<param1>(.*?)</param1>";
    private static final String PARAM1_MARK = "param1";

    private static final String RE_PARAM2 = "<param2>(.*?)</param2>";
    private static final String PARAM2_MARK = "param2";

    /**
     * Creamos un mensaje de tipo Room a partir del código de operación y del nombre
     */
    public NCDosParametros(byte opcode, String param1, String param2) {
        this.opcode = opcode;
        this.param1 = param1;
        this.param2 = param2;
    }

    @Override
    public String toEncodedString() {
        StringBuffer sb = new StringBuffer();

        sb.append("<" + MESSAGE_MARK + ">" + END_LINE);
        sb.append("<" + OPERATION_MARK + ">" + opcodeToString(opcode) + "</" + OPERATION_MARK + ">" + END_LINE);
        sb.append("<" + PARAM1_MARK + ">" + param1 + "</" + PARAM1_MARK + ">" + END_LINE);
        sb.append("<" + PARAM2_MARK + ">" + param2 + "</" + PARAM2_MARK + ">" + END_LINE);

        sb.append("</" + MESSAGE_MARK + ">" + END_LINE);

        return sb.toString();

    }

    public static NCDosParametros readFromString(byte code, String message) {
        Pattern pat_param1 = Pattern.compile(RE_PARAM1, Pattern.DOTALL);
        Matcher mat_param1 = pat_param1.matcher(message);

        Pattern pat_param2 = Pattern.compile(RE_PARAM2, Pattern.DOTALL);
        Matcher mat_param2 = pat_param2.matcher(message);

        String found_param1 = null;
        String found_param2 = null;

        if (mat_param1.find()) {
            found_param1 = mat_param1.group(1);
            if (mat_param2.find())
                found_param2 = mat_param2.group(1);
            else
                System.out.println("Error en NCDosParametros: no se ha encontrado parametro 'param2'.");

        } else {
            System.out.println("Error en NCDosParametros: no se ha encontrado parametro 'param1'.");
            return null;
        }

        return new NCDosParametros(code, found_param1, found_param2);
    }

    public String getParam1() {
        return param1;
    }

    public String getParam2() {
        return param2;
    }

}
