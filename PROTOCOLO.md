# Protocolos UDP y TCP

> Ismael Moussaid Gómez y Adrián Morales Torrano

## 1. Vista general del proyecto y del protocolo

Este proyecto consiste en crear una "aplicación" de chat. Para ello, hemos implementado tres entidades generales:

- `Directory`: Se encarga de almacenar los servidores disponibles de chat (pudiendo registrarlos y darles de baja) y de comunicarse con los clientes para facilitarles la dirección de dichos servidores dependiendo de la solicitud.
- `Server`: Maneja los mensajes de los clientes, repartiéndolos en diferentes salas de chat.
- `Client`: El usuario final, capaz de conectarse a un servidor y chatear con otros clientes.

## 2. Protocolo de conexión con `Directory`

Este protocolo lo usamos para la conexión entre `Directory-Server` y `Directory-Client`

Hemos distinguido los siguientes tipos de mensajes transferidos mediante `UDP`:

| OpCode | Nombre de mensaje           | Abreviatura         | Tamaño (bytes) | Secciones del mensaje                    | Descripción                                                                                        |
| ------ | --------------------------- | ------------------- | -------------- | ---------------------------------------- | -------------------------------------------------------------------------------------------------- |
| 0      | OK                          | `ok`                | 1              | `OpCode[1]`                              | Confirma el mensaje anterior y la acción del mismo.                                                |
| 1      | NotOK                       | `not_ok`            | 1              | `OpCode[1]`                              | Confirma el mensaje anterior y deniega la acción del mismo.                                        |
| 2      | Registrar Servidor          | `reg_sv`            | 9              | `OpCode[1]`, `Protocolo[4]`, `Puerto[4]` | Un `Servidor` solicita el registro a un `Directorio`.                                              |
| 3      | Consular Servidor           | `qry_sv`            | 5              | `OpCode[1]`, `Protocolo[4]`              | Un `Cliente` consulta la dirección de `Servidor` a un `Directorio` basado en el protocolo pedido.  |
| 4      | Respuesta de Consulta       | `qry_sv_info`       | 9              | `OpCode[1]`, `IPv4[4]`, `Puerto[4]`      | Contiene la información del `Servidor` al que un `Cliente` debe conectarse basado en un protocolo. |
| 5      | Respuesta de Consulta vacía | `qry_sv_info_empty` | 1              | `OpCode[1]`                              | Repuesta a una consulta de `Server` que no existe en el `Directory`.                               |

### Autómatas de los procesos

#### Autómata Cliente-Directorio

![Client-Directory automata](./images/client-directory.png "client-directory")

#### Autómata Servidor-Directorio

![Server-Directory automata](./images/server-directory.png "server-directory")

#### Autómata Directorio

![Directory automata](./images/directory.png "directory")

## 3. Protocolo de conexión `TCP`

Este protocolo lo utilizamos para la conexión entre `Client-Server`.

Tipos de mensajes `TCP`:

| OpCode | Nombre          | Información                                                                            |
| ------ | --------------- | -------------------------------------------------------------------------------------- |
| 0      | RegNick         | Intenta registrarse con un `nick`. Recive `Ok` o `NickDuplicated`.                     |
| 1      | NickOk          | El nick es correcto                                                                    |
| 2      | NickDuplicated  | El nick con el que se intenta registrar está duplicado.                                |
| 3      | EnterRoom       | Intenta entrar en una sala. Recive `Ok`, `RoomFull` y `RoomDoesNotExist`.              |
| 4      | EnterRoomOk     | La entrada a la sala ha sido correcta.                                                 |
| 5      | EnterRoomFailed | No se ha podido entrar a la sala. El mensaje contiene una cadena que expresa la razón. |
| 6      | ExitRoom        | Sale de la sala.                                                                       |
| 7      | GetRoomList     | Pide la información de las salas. Recive `RoomListInfo`.                               |
| 8      | RoomListInfo    | Contiene la información de las salas.                                                  |
| 9      | GetRoomInfo     | Pide la información de una `sala` concreta. Recive `RoomInfo`                          |
| 10     | RoomInfo        | Contiene la información de una sala.                                                   |
| 11     | SendRoomMsg     | Envía la información de los mensajes.                                                  |
| 12     | ReceiveRoomMsg  | Recive la información de mensajes de otros clientes.                                   |
