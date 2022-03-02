package connector;

import java.io.IOException;

public class UDPTesterServer {

    public static final String SERVER_IP = "localhost";

    public static void main(String[] args) {
        try {
            DirectoryConnector dc = new DirectoryConnector(SERVER_IP);

            boolean res = dc.registerServerForProtocol(0, 6868);

            if (res) {
                System.out.println("DBG: Server registered succesfully");
            }

        } catch (IOException e) {
            System.err.println("Cannot create protocol");
            e.printStackTrace();
        }
    }

}
