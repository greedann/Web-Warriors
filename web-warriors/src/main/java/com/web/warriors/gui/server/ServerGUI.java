package com.web.warriors.gui.server;

import javax.swing.JFrame;

import com.web.warriors.game.GameEngine;
import com.web.warriors.web.server.Server;

public class ServerGUI extends JFrame {
    private Server server;
    private ServerClients serverClients;
    private GameEngine gameEngine;

    public ServerGUI(Server server, GameEngine gameEngine) {
        super("Server");
        this.server = server;
        this.gameEngine = gameEngine;

        setTitle("Server");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        serverClients = new ServerClients(this);
        add(serverClients);

        // create new window whitch will display game state

    }

    public void addClient(Integer clientID) {
        serverClients.addClient(clientID);
    }

    public void removeClient(Integer clientID) {
        serverClients.removeClient(clientID);
    }

    public void sendToOne(String message, int id) {
        server.sendToOne(message, id);
    }

    public void sendToAll(String message) {
        server.sendToAll(message);
    }
}
