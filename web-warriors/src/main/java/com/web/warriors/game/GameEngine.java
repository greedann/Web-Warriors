package com.web.warriors.game;

import java.util.Vector;

import com.web.warriors.game.objects.Player;
import com.web.warriors.game.objects.Wall;
import com.web.warriors.gui.server.common.Map;

import javax.swing.*;

public class GameEngine {
    Vector<Player> players;
    Vector<Wall> walls;
    Map map;

    public GameEngine() {
        players = new Vector<Player>();
        walls = new Vector<Wall>();
        map = new Map(players);
        JFrame frame = new JFrame("Map with Players");// Another example player
        frame.add(map);
        frame.setSize(625, 625);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public Vector<Player> getPlayers() {
        return players;
    }

    public void addWall(Wall wall) {
        walls.add(wall);
    }

    public void addPlayer(int id, String name) {
        Player player = new Player(name, id);
        players.add(player);
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }

    public void updatePlayer(Player player) {
        for (Player p : players) {
            if (p.getId() == player.getId()) {
                p.move(player.getX(), player.getY());
            }
        }
    }

    public void handleMessage(String message, int id) {
        // TODO rewrite to json

        String[] parts = message.split(" ");
        if (parts[0].equals("move")) {
            int x = Integer.parseInt(parts[1]);
            int y = Integer.parseInt(parts[2]);
            for (Player p : players) {
                if (p.getId() == id) {
                    p.move(x, y);
                }
            }
        }

        for (Player p : players) {
            System.out.println(p);
        }
        map.refresh(players);
        SwingUtilities.invokeLater(() -> {
            map.repaint();
        });

    }
}
