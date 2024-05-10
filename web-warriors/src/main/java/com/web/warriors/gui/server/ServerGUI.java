package com.web.warriors.gui.server;

import javax.swing.JFrame;

import com.web.warriors.game.GameEngine;
import com.web.warriors.game.ServerAplication;
import com.web.warriors.gui.common.Map;
import com.web.warriors.web.server.Server;

public class ServerGUI extends JFrame {
    private ServerAplication aplication;
    private ServerClients serverClients;
    private Map map;

    public ServerGUI(Server server, GameEngine gameEngine) {
        super("Server");

        setTitle("Server");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // setVisible(true); // uncomment to see window with server clients

        serverClients = new ServerClients(this);
        add(serverClients);

        // create new window whitch will display game state
        JFrame frame = new JFrame("Server");
        map = new Map(gameEngine);

        frame.add(map);
        frame.setSize(625, 625);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.repaint();

    }

    public void addClient(Integer clientID) {
        serverClients.addClient(clientID);
    }

    public void removeClient(Integer clientID) {
        serverClients.removeClient(clientID);
    }

    public void sendToOne(String message, int id) {
        aplication.sendToOne(message, id);
    }

    public void sendToAll(String message) {
        aplication.sendToAll(message);
    }
}
