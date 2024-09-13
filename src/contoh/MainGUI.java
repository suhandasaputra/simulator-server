/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package contoh;

/**
 *
 * @author suhan
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainGUI extends JFrame {
    private JButton startServerButton;
    private JButton stopServerButton;
    private JButton connectClientButton;
    private JButton disconnectClientButton;

    private Server server;
    private Client client;

    public MainGUI() {
        setTitle("Simple Server-Client App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 200);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1));

        startServerButton = new JButton("Start Server");
        startServerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startServer();
            }
        });
        panel.add(startServerButton);

        stopServerButton = new JButton("Stop Server");
        stopServerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                stopServer();
            }
        });
        panel.add(stopServerButton);

        connectClientButton = new JButton("Connect Client");
        connectClientButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                connectClient();
            }
        });
        panel.add(connectClientButton);

        disconnectClientButton = new JButton("Disconnect Client");
        disconnectClientButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                disconnectClient();
            }
        });
        panel.add(disconnectClientButton);

        add(panel);
        setVisible(true);
    }

    private void startServer() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                server = new Server();
                server.start();
                return null;
            }

            @Override
            protected void done() {
                JOptionPane.showMessageDialog(MainGUI.this, "Server started.");
            }
        };
        worker.execute();
    }


    private void stopServer() {
        if (server != null) {
            server.stop();
            JOptionPane.showMessageDialog(this, "Server stopped.");
        } else {
            JOptionPane.showMessageDialog(this, "Server is not running.");
        }
    }

    private void connectClient() {
        client = new Client();
        client.connect();
        JOptionPane.showMessageDialog(this, "Client connected to server.");
    }

    private void disconnectClient() {
        if (client != null) {
            client.disconnect();
            JOptionPane.showMessageDialog(this, "Client disconnected from server.");
        } else {
            JOptionPane.showMessageDialog(this, "Client is not connected.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MainGUI();
            }
        });
    }
}
