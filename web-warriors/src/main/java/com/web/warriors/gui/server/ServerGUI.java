package com.web.warriors.gui.server;

import javax.swing.JFrame;

import com.web.warriors.game.GameEngine;
import com.web.warriors.game.ServerAplication;
import com.web.warriors.game.objects.Wall;
import com.web.warriors.gui.common.Map;
import com.web.warriors.web.server.Server;

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
        //b
        walls.add(new Wall(50, 0, 50, 35));
        walls.add(new Wall(50, 45, 50, 60));
        walls.add(new Wall(0, 60, 15, 60));
        walls.add(new Wall(35, 60, 50, 60));
        //temka
        walls.add(new Wall(0, 90, 35, 90));
        walls.add(new Wall(48, 90, 55, 90));
        //mid
        walls.add(new Wall(90, 0, 90, 15));
        walls.add(new Wall(90, 25, 90, 60));
        walls.add(new Wall(50, 60, 60, 60));
        walls.add(new Wall(80, 60, 90, 60));
        //second mid
        walls.add(new Wall(55, 60, 55, 65));
        walls.add(new Wall(55, 75, 55, 130));
        walls.add(new Wall(55,130, 80, 130));
        //column
        walls.add(new Wall(65, 118, 73, 118));
        walls.add(new Wall(73, 118, 73, 110));
        walls.add(new Wall(73, 110, 65, 110));
        walls.add(new Wall(65, 110, 65, 118));
        //ct spawn
        walls.add(new Wall(120, 0, 120, 25));
        walls.add(new Wall(90, 40, 120, 40));
        //long
        walls.add(new Wall(120, 55, 120, 130));
        walls.add(new Wall(120, 130, 105, 130));
        walls.add(new Wall(105, 130, 105, 136));
        walls.add(new Wall(105, 146, 105, 166));
        //short
        walls.add(new Wall(85, 60, 85, 100));
        walls.add(new Wall(120, 70, 105, 70));
        walls.add(new Wall(105, 70, 105, 85));
        walls.add(new Wall(105, 85, 120, 85));




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
