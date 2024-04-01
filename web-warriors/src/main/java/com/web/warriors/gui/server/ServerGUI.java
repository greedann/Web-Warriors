package com.web.warriors.gui.server;

import javax.swing.JFrame;

import com.web.warriors.game.GameEngine;
import com.web.warriors.gui.server.common.Map;
import com.web.warriors.web.server.Server;

public class ServerGUI extends JFrame {
    private Server server;
    private ServerClients serverClients;
    private GameEngine gameEngine;
    Map map;

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
        JFrame frame = new JFrame("Map with Players");// Another example player
        map = new Map(gameEngine);

        frame.add(map);
        frame.setSize(625, 625);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

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
