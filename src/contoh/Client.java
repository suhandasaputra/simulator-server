/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package contoh;

/**
 *
 * @author suhan
 */
import java.io.*;
import java.net.*;

public class Client {
    private Socket socket;
    private Thread clientThread;

    public void connect() {
        clientThread = new Thread(() -> {
            try {
                socket = new Socket("localhost", 12345); // Connect to localhost on port 12345
                System.out.println("Connected to server.");

                InputStream inputStream = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                while (true) {
                    String receivedMessage = reader.readLine();
                    if (receivedMessage != null) {
                        System.out.println("Received from server: " + receivedMessage);
                        if ("Server shutting down".equals(receivedMessage)) {
                            // Server is shutting down, handle accordingly
                            // For example, close the client socket
                            disconnect();
                            break;
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("Error connecting to server: " + e.getMessage());
            }
        });
        clientThread.start();
    }

    public void disconnect() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
                System.out.println("Disconnected from server.");
            }
        } catch (IOException e) {
            System.err.println("Error disconnecting from server: " + e.getMessage());
        }
    }
}
