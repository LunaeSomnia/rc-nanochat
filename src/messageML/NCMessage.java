package messageML;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import server.roomManager.NCRoomDescription;

public abstract class NCMessage {

    protected byte opcode;

    //// TO!DO: IMPLEMENTAR TODAS LAS CONSTANTES RELACIONADAS CON LOS CODIGOS DE
    //// OPERACION
    public static final byte OP_INVALID_CODE = -1;
    public static final byte OP_REGNICK = 0;
    public static final byte OP_NICKOK = 1;
    public static final byte OP_NICKDUPLICATED = 2;
    public static final byte OP_ENTERROOM = 3;
    public static final byte OP_ENTERROOMOK = 4;
    public static final byte OP_ENTERROOMFAILED = 5;
    public static final byte OP_EXITROOM = 6;
    public static final byte OP_GETROOMLIST = 7;
    public static final byte OP_ROOMLISTINFO = 8;
    public static final byte OP_GETROOMINFO = 9;
    public static final byte OP_ROOMINFO = 10;
    public static final byte OP_SENDROOMMSG = 11;
    public static final byte OP_RECEIVEROOMMSG = 12;

    public static final char DELIMITER = ':'; // Define el delimitador
    public static final char END_LINE = '\n'; // Define el carácter de fin de línea

    public static final String OPERATION_MARK = "opcode";
    public static final String MESSAGE_MARK = "msg";

    /**
     * Códigos de los opcodes válidos El orden
     * es importante para relacionarlos con la cadena
     * que aparece en los mensajes
     */
    private static final Byte[] _valid_opcodes = {
            OP_REGNICK,
            OP_NICKOK,
            OP_NICKDUPLICATED,
            OP_ENTERROOM,
            OP_ENTERROOMOK,
            OP_ENTERROOMFAILED,
            OP_EXITROOM,
            OP_GETROOMLIST,
            OP_ROOMLISTINFO,
            OP_GETROOMINFO,
            OP_ROOMINFO,
            OP_SENDROOMMSG,
            OP_RECEIVEROOMMSG
    };

    /**
     * cadena exacta de cada orden
     */
    private static final String[] _valid_operations_str = {
            "RegNick",
            "NickOk",
            "NickDuplicated",
            "EnterRoom",
            "EnterRoomOk",
            "EnterRoomFailed",
            "ExitRoom",
            "GetRoomList",
            "RoomListInfo",
            "GetRoomInfo",
            "RoomInfo",
            "SendRoomMsg",
            "ReceiveRoomMsg"
    };

    private static Map<String, Byte> _operation_to_opcode;
    private static Map<Byte, String> _opcode_to_operation;

    static {
        _operation_to_opcode = new TreeMap<>();
        _opcode_to_operation = new TreeMap<>();
        for (int i = 0; i < _valid_operations_str.length; ++i) {
            _operation_to_opcode.put(_valid_operations_str[i].toLowerCase(), _valid_opcodes[i]);
            _opcode_to_operation.put(_valid_opcodes[i], _valid_operations_str[i]);
        }
    }

    /**
     * Transforma una cadena en el opcode correspondiente
     */
    protected static byte stringToOpcode(String opStr) {
        return _operation_to_opcode.getOrDefault(opStr.toLowerCase(), OP_INVALID_CODE);
    }

    /**
     * Transforma un opcode en la cadena correspondiente
     */
    protected static String opcodeToString(byte opcode) {
        return _opcode_to_operation.getOrDefault(opcode, null);
    }

    // Devuelve el opcode del mensaje
    public byte getOpcode() {
        return opcode;
    }

    // Método que debe ser implementado por cada subclase de NCMessage
    protected abstract String toEncodedString();

    // Analiza la operación de cada mensaje y usa el método readFromString() de cada
    // subclase para parsear

    public static NCMessage readMessageFromSocket(DataInputStream dis) throws IOException {
        Pattern pat = Pattern.compile("<" + MESSAGE_MARK + ">(.*?)</" + MESSAGE_MARK + ">", Pattern.DOTALL);
        Pattern pat1 = Pattern.compile("<" + OPERATION_MARK + ">(.*?)</" + OPERATION_MARK + ">");

        String message = dis.readUTF();
        Matcher mat = pat.matcher(message);
        if (!mat.find()) {
            System.out.println("Mensaje mal formado:\n" + message);
            return null;
            // Message not found
        }
        String inner_msg = mat.group(1); // extraemos el mensaje

        Matcher mat1 = pat1.matcher(inner_msg);
        if (!mat1.find()) {
            System.out.println("Mensaje mal formado:\n" + message);
            return null;
            // Operation not found
        }
        String operation = mat1.group(1); // extraemos la operación

        byte code = stringToOpcode(operation);
        if (code == OP_INVALID_CODE)
            return null;

        switch (code) {
            //// TO!DO Parsear el resto de mensajes
            case OP_REGNICK: {
                return NCUnParametro.readFromString(code, message);
            }
            case OP_NICKOK: {
                return NCControl.readFromString(code, message);
            }
            case OP_NICKDUPLICATED: {
                return NCControl.readFromString(code, message);
            }
            case OP_ENTERROOM: {
                return NCUnParametro.readFromString(code, message);
            }
            case OP_ENTERROOMOK: {
                return NCControl.readFromString(code, message);
            }
            case OP_ENTERROOMFAILED: {
                return NCUnParametro.readFromString(code, message);
            }
            case OP_EXITROOM: {
                return NCControl.readFromString(code, message);
            }
            case OP_GETROOMLIST: {
                return NCControl.readFromString(code, message);
            }
            case OP_ROOMLISTINFO: {
                return NCListaSala.readFromString(code, message);
            }
            case OP_GETROOMINFO: {
                return NCControl.readFromString(code, message);
            }
            case OP_ROOMINFO: {
                return NCListaSala.readFromString(code, message);
            }
            case OP_SENDROOMMSG: {
                return NCUnParametro.readFromString(code, message);
            }
            case OP_RECEIVEROOMMSG: {
                return NCDosParametros.readFromString(code, message);
            }
            default:
                System.err.println("Unknown message type received:" + code);
                return null;
        }

    }

    //// TO!DO Programar el resto de métodos para crear otros tipos de mensajes

    public static NCMessage makeControl(byte code) {
        return new NCControl(code);
    }

    public static NCMessage makeOneParam(byte code, String param) {
        return new NCUnParametro(code, param);
    }

    public static NCMessage makeTwoParam(byte code, String param1, String param2) {
        return new NCDosParametros(code, param1, param2);
    }

    public static NCMessage makeRoomList(byte code, List<NCRoomDescription> rooms) {
        return new NCListaSala(code, rooms);
    }
}
