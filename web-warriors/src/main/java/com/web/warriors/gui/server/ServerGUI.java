package com.web.warriors.gui.server;

import javax.swing.JFrame;

import com.web.warriors.game.GameEngine;
import com.web.warriors.game.ServerAplication;
import com.web.warriors.game.objects.Wall;
import com.web.warriors.gui.common.Map;
import com.web.warriors.web.server.Server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

public class ServerGUI extends JFrame {
    private ServerAplication aplication;
    private ServerClients serverClients;
    private Map map;

    public ServerGUI(Server server, GameEngine gameEngine) {
        super("Server");

        setTitle("Server");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        serverClients = new ServerClients(this);
        add(serverClients);

        // create new window whitch will display game state
        JFrame frame = new JFrame("Server");// Another example player
        gameEngine.setWalls(createMapWalls());
        map = new Map(gameEngine);
        gameEngine.setWalls(createMapWalls());

        frame.add(map);
        frame.setSize(625, 625);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.repaint();

    }
    private Vector<Wall> createMapWalls() {
        Vector<Wall> walls = new Vector<>();
        String currentDirectory = System.getProperty("user.dir") +"/web-warriors/src/main/resources";
        try (BufferedReader reader = new BufferedReader(new FileReader(currentDirectory+"/walls.txt"))){
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int startX = Integer.parseInt(parts[0]);
                int startY = Integer.parseInt(parts[1]);
                int endX = Integer.parseInt(parts[2]);
                int endY = Integer.parseInt(parts[3]);
                walls.add(new Wall(startX, startY, endX, endY));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        return walls;
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
