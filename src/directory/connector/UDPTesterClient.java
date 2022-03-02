package directory.connector;

import java.io.IOException;

public class UDPTesterClient {

    public static final String SERVER_IP = "localhost";

    public static void main(String[] args) {
        try {
            DirectoryConnector dc = new DirectoryConnector(SERVER_IP);
            dc.getServerForProtocol(0);
        } catch (IOException e) {
            System.err.println("Cannot create protocol");
            e.printStackTrace();
        }
    }

}
