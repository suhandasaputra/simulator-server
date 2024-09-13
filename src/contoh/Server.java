/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package contoh;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
//import simulatorServer.ClientHandler;

public class Server {
    private ServerSocket serverSocket;
    private List<Clienthandler> clients = new ArrayList<>();

    public void start() {
        try {
            serverSocket = new ServerSocket(12345); // Listen on port 12345
            System.out.println("Server started. Waiting for clients...");

            while (true) {
                Socket clientSocket = serverSocket.accept(); // Accept incoming client connections
                System.out.println("Client connected from: " + clientSocket.getInetAddress());
                // Handle client connection in a separate thread
                Clienthandler clienthandler = new Clienthandler(clientSocket, this);
                clients.add(clienthandler);
                clienthandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            // Send a special message to all connected clients before stopping the server
            for (Clienthandler client : clients) {
                client.sendMessage("Server shutting down");
            }

            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                System.out.println("Server stopped.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeClient(Clienthandler client) {
        clients.remove(client);
    }
}
