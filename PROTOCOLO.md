# Protocolo


> Ismael Moussaid Gómez y Adrián Morales Torrano

## Vista general del proyecto y del protocolo

En nuestro protocolo de aplicación hemos distinguido los siguientes tipos de mensajes transferidos mediante `UDP`:

ID Mensaje | Significado | Tamaño en bytes | Secciones del mensaje
--- | --- | --- | ---
0 | OK | 1 | `Tipo[1]`
1 | NotOK | 1 | `Tipo[1]`
2 | Registro de Servidor | 9 | `Tipo[1]`, `Protocolo[4]`, `Puerto[4]`
3 | Consula de Servidor | 5 | `Tipo[1]`, `Protocolo[4]`
4 | Respuesta de Consulta (Cliente) | 9 |`Tipo[1]`, `Direccion IPv4[4]`, `Puerto[4]`

> TODO: Insertar un diagrama con los tipos de mensajes entre entidades (cliente-servidor).