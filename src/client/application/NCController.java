package client.application;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

import client.comm.NCConnector;
import client.shell.NCCommands;
import client.shell.NCShell;
import directory.connector.DirectoryConnector;
import messageML.NCDosParametros;
import server.NCServerManager;
import server.roomManager.NCRoomDescription;
import server.roomManager.NCRoomDescriptionNameComparator;

public class NCController {
    // Diferentes estados del cliente de acuerdo con el autómata
    private static final byte PRE_REGISTER = 1;
    private static final byte CLIENT_REGISTERED = 2;
    private static final byte CLIENT_IN_ROOM = 3;
    // Código de protocolo implementado por este cliente
    //// TO!DO Cambiar para cada grupo
    private static final int PROTOCOL = 10;
    // Conector para enviar y recibir mensajes del directorio
    private DirectoryConnector directoryConnector;
    // Conector para enviar y recibir mensajes con el servidor de NanoChat
    private NCConnector ncConnector;
    // Shell para leer comandos de usuario de la entrada estándar
    private NCShell shell;
    // Último comando proporcionado por el usuario
    private byte currentCommand;
    // Nick del usuario
    private String nickname;
    // Sala de chat en la que se encuentra el usuario (si está en alguna)
    private String room;
    // Nuevo nombre de la sala en la que se encuentra el usuario (si está en alguna)
    private String newRoomName;
    // Mensaje enviado o por enviar al chat
    private String chatMessage;
    // Dirección de internet del servidor de NanoChat
    private InetSocketAddress serverAddress;
    // Estado actual del cliente, de acuerdo con el autómata
    private byte clientStatus = PRE_REGISTER;

    // Constructor
    public NCController() {
        shell = new NCShell();
    }

    // Devuelve el comando actual introducido por el usuario
    public byte getCurrentCommand() {
        return this.currentCommand;
    }

    // Establece el comando actual
    public void setCurrentCommand(byte command) {
        currentCommand = command;
    }

    // Registra en atributos internos los posibles parámetros del comando tecleado
    // por el usuario
    public void setCurrentCommandArguments(String[] args) {
        // Comprobaremos también si el comando es válido para el estado actual del
        // autómata
        switch (currentCommand) {
            case NCCommands.COM_NICK:
                if (clientStatus == PRE_REGISTER)
                    nickname = args[0];
                break;
            case NCCommands.COM_ENTER:
                room = args[0];
                break;
            case NCCommands.COM_SEND:
                chatMessage = args[0];
                break;
            case NCCommands.COM_RENAME:
                newRoomName = args[0];
                break;
            default:
        }
    }

    // Procesa los comandos introducidos por un usuario que aún no está dentro de
    // una sala
    public void processCommand() {
        switch (currentCommand) {
            case NCCommands.COM_NICK:
                if (clientStatus == PRE_REGISTER)
                    registerNickName();
                else
                    System.out.println("* You have already registered a nickname (" + nickname + ")");
                break;
            case NCCommands.COM_ROOMLIST:
                //// TO!DO LLamar a getAndShowRooms() si el estado actual del autómata lo
                //// permite
                if (clientStatus == CLIENT_REGISTERED)
                    getAndShowRooms();
                else
                    //// TO!DO Si no está permitido informar al usuario
                    System.out.println("* You can't ask for the room list. ");
                break;
            case NCCommands.COM_ENTER:
                //// TO!DO LLamar a enterChat() si el estado actual del autómata lo permite
                if (clientStatus == CLIENT_REGISTERED)
                    enterChat();
                else
                    //// TO!DO Si no está permitido informar al usuario
                    System.out.println("* You can't enter the room. ");
                break;
            case NCCommands.COM_QUIT:
                // Cuando salimos tenemos que cerrar todas las conexiones y sockets abiertos
                ncConnector.disconnect();
                directoryConnector.close();
                break;
            default:
        }
    }

    // Método para registrar el nick del usuario en el servidor de NanoChat
    private void registerNickName() {
        try {
            // Pedimos que se registre el nick (se comprobará si está duplicado)
            boolean registered = ncConnector.registerNickname(nickname);
            //// TO!DO: Cambiar la llamada anterior a registerNickname() al usar mensajes
            //// formateados --> Boletín 6
            if (registered) {
                //// TO!DO Si el registro fue exitoso pasamos al siguiente estado del autómata
                clientStatus = CLIENT_REGISTERED;
                System.out.println("* Your nickname is now " + nickname);
            } else
                // En este caso el nick ya existía
                System.out.println("* The nickname is duplicated. Try a different one.");
        } catch (IOException e) {
            System.out.println("* There was an error registering the nickname");
        }
    }

    // Método que solicita al servidor de NanoChat la lista de salas e imprime el
    // resultado obtenido
    private void getAndShowRooms() {
        try {
            //// TO!DO Lista que contendrá las descripciones de las salas existentes
            //// TO!DO Le pedimos al conector que obtenga la lista de salas
            //// ncConnector.getRooms()
            List<NCRoomDescription> rooms = ncConnector.getRooms();
            //// TO!DO Una vez recibidas iteramos sobre la lista para imprimir información
            //// de
            //// cada sala
            rooms.sort(new NCRoomDescriptionNameComparator());
            for (NCRoomDescription roomDescription : rooms) {
                System.out.println(roomDescription.toPrintableString());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método para tramitar la solicitud de acceso del usuario a una sala concreta
    private void enterChat() {
        try {
            //// TO!DO Se solicita al servidor la entrada en la sala correspondiente
            String couldEnter = ncConnector.enterRoom(room);

            //// TO!DO Si la respuesta es un rechazo entonces informamos al usuario y
            //// salimos
            if (couldEnter != null) {
                System.out.println("* You couldn't enter the room (" + couldEnter + ").");
            } else {
                //// TO!DO En caso contrario informamos que estamos dentro y seguimos
                System.out.println("* You entered the room \"" + room + "\".");
                //// TO!DO Cambiamos el estado del autómata para aceptar nuevos comandos
                clientStatus = CLIENT_IN_ROOM;

                do {
                    // Pasamos a aceptar sólo los comandos que son válidos dentro de una sala
                    readRoomCommandFromShell();
                    processRoomCommand();
                } while (currentCommand != NCCommands.COM_EXIT);
                System.out.println("* Your are out of the room");
                //// TO!DO Llegados a este punto el usuario ha querido salir de la sala,
                //// cambiamos
                //// el estado del autómata
                clientStatus = CLIENT_REGISTERED;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // Método para procesar los comandos específicos de una sala
    private void processRoomCommand() {
        switch (currentCommand) {
            case NCCommands.COM_ROOMINFO:
                // El usuario ha solicitado información sobre la sala y llamamos al método que
                // la obtendrá
                getAndShowInfo();
                break;
            case NCCommands.COM_RENAME:
                // El usuario ha solicitado cambiar el nombre de la sala
                renameRoom();
                break;
            case NCCommands.COM_SEND:
                // El usuario quiere enviar un mensaje al chat de la sala
                sendChatMessage();
                break;
            case NCCommands.COM_SOCKET_IN:
                // En este caso lo que ha sucedido es que hemos recibido un mensaje desde la
                // sala y hay que procesarlo
                processIncommingMessage();
                break;
            case NCCommands.COM_EXIT:
                // El usuario quiere salir de la sala
                exitTheRoom();
        }
    }

    // Método para solicitar al servidor la información sobre una sala y para
    // mostrarla por pantalla
    private void getAndShowInfo() {
        try {
            //// TO!DO Pedimos al servidor información sobre la sala en concreto
            NCRoomDescription roomDesc = ncConnector.getRoomInfo(room);
            //// TO!DO Mostramos por pantalla la información
            System.out.println(roomDesc.toPrintableString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método para solicitar el renombre de la sala en la que se encuentra.
    private void renameRoom() {
        try {
            ncConnector.renameRoom(newRoomName);
            room = newRoomName;
            System.out.println(" * Room renamed to '" + newRoomName + "'");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método para notificar al servidor que salimos de la sala
    private void exitTheRoom() {
        try {
            //// TO!DO Mandamos al servidor el mensaje de salida
            ncConnector.leaveRoom();
            //// TO!DO Cambiamos el estado del autómata para indicar que estamos fuera de la
            //// sala
            clientStatus = CLIENT_REGISTERED;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método para enviar un mensaje al chat de la sala
    private void sendChatMessage() {
        //// TO!DO Mandamos al servidor un mensaje de chat
        ncConnector.sendChatMessage(chatMessage);
    }

    // Método para procesar los mensajes recibidos del servidor mientras que el
    // shell estaba esperando un comando de usuario
    private void processIncommingMessage() {
        //// TO!DO Recibir el mensaje
        NCDosParametros chatMessage = ncConnector.receiveChatMessage();
        //// TO!DO En función del tipo de mensaje, actuar en consecuencia
        //// TO!DO (Ejemplo) En el caso de que fuera un mensaje de chat de broadcast
        //// mostramos la información de quién envía el mensaje y el mensaje en sí
        if (chatMessage.getParam1().equals(NCServerManager.SYSTEM_NAME)) {
            // Es un mensaje del servidor
            System.out.println(" ** " + chatMessage.getParam2());
        } else {
            // El mensaje es de un usuario
            System.out.println(chatMessage.getParam1() + ": " + chatMessage.getParam2());
        }
    }

    // Método para leer un comando de la sala
    public void readRoomCommandFromShell() {
        // Pedimos un nuevo comando de sala al shell (pasando el conector por si nos
        // llega un mensaje entrante)
        shell.readChatCommand(ncConnector);
        // Establecemos el comando tecleado (o el mensaje recibido) como comando actual
        setCurrentCommand(shell.getCommand());
        // Procesamos los posibles parámetros (si los hubiera)
        setCurrentCommandArguments(shell.getCommandArguments());
    }

    // Método para leer un comando general (fuera de una sala)
    public void readGeneralCommandFromShell() {
        // Pedimos el comando al shell
        shell.readGeneralCommand();
        // Establecemos que el comando actual es el que ha obtenido el shell
        setCurrentCommand(shell.getCommand());
        // Analizamos los posibles parámetros asociados al comando
        setCurrentCommandArguments(shell.getCommandArguments());
    }

    // Método para obtener el servidor de NanoChat que nos proporcione el directorio
    public boolean getServerFromDirectory(String directoryHostname) {
        // Inicializamos el conector con el directorio y el shell
        System.out.println("* Connecting to the directory...");
        // Intentamos obtener la dirección del servidor de NanoChat que trabaja con
        // nuestro protocolo
        try {
            directoryConnector = new DirectoryConnector(directoryHostname);
            serverAddress = directoryConnector.getServerForProtocol(PROTOCOL);
        } catch (IOException e1) {
            serverAddress = null;
        }
        // Si no hemos recibido la dirección entonces nos quedan menos intentos
        if (serverAddress == null) {
            System.out.println("* Check your connection, the directory is not available.");
            return false;
        } else
            return true;
    }

    // Método para establecer la conexión con el servidor de Chat (a través del
    // NCConnector)
    public boolean connectToChatServer() {
        try {
            // Inicializamos el conector para intercambiar mensajes con el servidor de
            // NanoChat (lo hace la clase NCConnector)
            ncConnector = new NCConnector(serverAddress);
        } catch (IOException e) {
            System.out.println("* Check your connection, the game server is not available.");
            serverAddress = null;
        }
        // Si la conexión se ha establecido con éxito informamos al usuario y cambiamos
        // el estado del autómata
        if (serverAddress != null) {
            System.out.println("* Connected to " + serverAddress);
            clientStatus = PRE_REGISTER;
            return true;
        } else
            return false;
    }

    // Método que comprueba si el usuario ha introducido el comando para salir de la
    // aplicación
    public boolean shouldQuit() {
        return currentCommand == NCCommands.COM_QUIT;
    }

}
