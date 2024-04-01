package com.web.warriors.gui.server;

import javax.swing.JFrame;

import com.web.warriors.web.server.Server;

public class ServerMain extends JFrame {
    private Server server;
    private ServerClients serverClients;

    public ServerMain() {
        super("Server");
        server = new Server(this);
        Thread thread = new Thread(server);
        thread.start();

        setTitle("Server");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        serverClients = new ServerClients(this);
        add(serverClients);

    }

    public void addClient(Integer clientID) {
        serverClients.addClient(clientID);
    }

    public void sendToOne(String message, int id) {
        server.sendToOne(message, id);
    }

    public void sendToAll(String message) {
        server.sendToAll(message);
    }

    public static void main(String[] args) {
        new ServerMain();
    }

   
}
