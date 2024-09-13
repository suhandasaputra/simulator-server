package simulatorServer;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOPackager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import util.ISO8583MessageHandler;
import util.ISO8583MessageUtil;

public class ClientHandler extends Thread {

    private final Socket socket;
    private final ISO8583MessageUtil messageUtil;
    private final ISO8583MessageHandler messageHandler;

    ClientHandler(Socket socket, ISOPackager packager) {
        this.socket = socket;
        this.messageUtil = new ISO8583MessageUtil(packager);
        this.messageHandler = new ISO8583MessageHandler(packager);
    }

    @Override
    public void run() {
        try {
            boolean header = false;
            InputStream inputStream = socket.getInputStream();
            byte[] buffer = new byte[4096];

            while (true) {
                int bytesRead = inputStream.read(buffer);

                if (bytesRead == -1) {
                    // No more data to read, exit the loop
                    System.out.println("Client disconnected gracefully.");
                    System.out.println("ISO 8583 server listening on port " + socket.getPort());
                    break;
                }

                if (header) {
                    // Extract the ISO 8583 message from the received bytes header
                    byte[] isoMessageBytes = extractISO8583Message(buffer, bytesRead);

                    // Unpack and process the ISO 8583 message
                    ISOMsg isoMessage = messageUtil.unpackISO8583Message(isoMessageBytes);
                    // Check for a disconnection request
                    if (isoMessage.hasField(127) && "disconnect".equals(isoMessage.getString(127))) {
                        System.out.println("Disconnect request received. Closing connection.");
                        System.out.println("ISO 8583 server listening on port " + socket.getPort());
                        break; // Exit the loop and close the connection
                    }
                    // Process the ISO 8583 message
                    messageHandler.processISO8583Message(isoMessage, isoMessageBytes, socket);
                    // Send response
                    byte[] responseBytes = isoMessage.pack();
                    sendResponse(responseBytes, socket);
                    // Print the modified outgoing message fields
                    System.out.println("Outgoing Message Fields:");
                    System.out.println("MTI: " + isoMessage.getMTI());
                    for (int i = 1; i <= isoMessage.getMaxField(); i++) {
                        if (isoMessage.hasField(i)) {
                            System.out.println("Field " + i + ": " + isoMessage.getString(i));
                        }
                    }
                } else {
                    //Assuming the entire message is received in one go
                    byte[] messageBytes = new byte[bytesRead];
                    System.arraycopy(buffer, 0, messageBytes, 0, bytesRead);

                    // Unpack and process the ISO 8583 message
                    ISOMsg isoMessage = messageUtil.unpackISO8583Message(messageBytes);

                    // Check for a disconnection request
                    if (isoMessage.hasField(127) && "disconnect".equals(isoMessage.getString(127))) {
                        System.out.println("Disconnect request received. Closing connection.");
                        System.out.println("ISO 8583 server listening on port " + socket.getPort());
                        break; // Exit the loop and close the connection
                    }

                    // Process the ISO 8583 message
                    messageHandler.processISO8583Message(isoMessage, messageBytes, socket);

                    // Send response
                    byte[] responseBytes = isoMessage.pack();
                    sendResponse(responseBytes, socket);

                    // Print the modified outgoing message fields
                    System.out.println("Outgoing Message Fields:");
                    System.out.println("MTI: " + isoMessage.getMTI());
                    for (int i = 1; i <= isoMessage.getMaxField(); i++) {
                        if (isoMessage.hasField(i)) {
                            System.out.println("Field " + i + ": " + isoMessage.getString(i));
                        }
                    }
                }
            }
            // Close the socket after processing all messages or receiving a disconnect request
            socket.close();
        } catch (IOException | ISOException e) {
            System.out.println("Connection reset, client down................");
            System.out.println("ISO 8583 server listening on port " + socket.getPort());
            e.printStackTrace();
        }
    }

    private byte[] extractISO8583Message(byte[] receivedBytes, int bytesRead) {
        // Assuming your header length is 12 (modify as needed)
        int headerLength = 12;
        int isoMessageLength = bytesRead - headerLength;

        byte[] isoMessageBytes = new byte[isoMessageLength];
        System.arraycopy(receivedBytes, headerLength, isoMessageBytes, 0, isoMessageLength);
        String isoMessageString = new String(receivedBytes);

        byte[] headerBytes = new byte[headerLength];
        System.arraycopy(receivedBytes, 0, headerBytes, 0, headerLength);
        String headerString = new String(headerBytes);

        System.out.println("Received ISO 8583 message include header: " + isoMessageString);
        System.out.println("Length ISO 8583 message: " + bytesRead);
        System.out.println("Header: " + headerString);

        return isoMessageBytes;
    }

    private void sendResponse(byte[] responseBytes, Socket socket) throws IOException {
        if (socket.isConnected()) {
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(responseBytes);
            outputStream.flush();
            String outIsoMessageString = new String(responseBytes);
            System.out.println();
            System.out.println("Sent ISO 8583 message: " + outIsoMessageString);
        } else {
            System.out.println("Socket is not connected. Response not sent.");
        }
    }
}
