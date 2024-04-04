package com.web.warriors.game;

import java.util.Map;
import java.util.Vector;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.warriors.game.objects.Player;
import com.web.warriors.game.objects.Wall;

public class GameEngine {
    Vector<Player> players;
    Vector<Wall> walls;
    ObjectMapper mapper = new ObjectMapper();

    public GameEngine() {
        players = new Vector<Player>();
        walls = new Vector<Wall>();
    }

    public Player getPlayer(int id) {
        for (Player p : players) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
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

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void removePlayer(Integer id) {
        System.out.println("Removing player " + id);
        for (Player p : players) {
            if (p.getId() == id) {
                System.out.println("Player " + p.getId() + " disconnected");
                players.remove(p);
                break;
            }
        }
    }

    public void updatePlayer(Player player) {
        for (Player p : players) {
            if (p.getId() == player.getId()) {
                p.move(player.getX(), player.getY());
            }
        }
    }

    public void handleMessage(Map<String, Object> data, int id) {
        String type = (String) data.get("type");
        switch (type) {
            case "position":
                int x = (int) data.get("x");
                int y = (int) data.get("y");
                for (Player p : players) {
                    if (p.getId() == id) {
                        p.move(x, y);
                        break;
                    }
                }
                break;

            case "disconnect":
                removePlayer(id);
                break;

            default:
                break;
        }
    }
}
