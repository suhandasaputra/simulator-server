package simulatorServer;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOPackager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import util.PackagerUtil;

public class SimulatorServer {

    public static void main(String[] args) throws ISOException {
        int port = 14000; // Specify the port number you want to listen on

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("ISO 8583 server listening on port " + port);

            ISOPackager packager = PackagerUtil.createISOPackager();

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Connection established with client: " + socket.getInetAddress());

                // Handle the client connection in a separate thread
                new ClientHandler(socket, packager).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
