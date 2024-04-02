package com.web.warriors.game;

import java.util.Map;
import java.util.Vector;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
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

        try {
            Map<String, Object> data = mapper.readValue(message, Map.class);
            String type = (String) data.get("type");
            switch (type) {
                case "position":
                    int x = (int) data.get("x");
                    int y = (int) data.get("y");
                    for (Player p : players) {
                        if (p.getId() == id) {
                            System.out.println("Player " + p.getId() + " moved to " + x + " " + y);
                            p.move(x, y);
                            break;
                        }
                    }
                    break;

                default:
                    break;
            }

        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // for (Player p : players) {
        // System.out.println(p);
        // }
    }
}
