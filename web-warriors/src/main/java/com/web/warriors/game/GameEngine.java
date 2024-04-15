package com.web.warriors.game;

import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.warriors.game.objects.Player;
import com.web.warriors.game.objects.Team;
import com.web.warriors.game.objects.Wall;

public class GameEngine {
    Vector<Player> players;
    Vector<Wall> walls;
    ObjectMapper mapper = new ObjectMapper();
    int terrorists = 0, counterTerrorists = 0;

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
    public void setWalls(Vector<Wall> walls) {
        this.walls = walls;
    }

    public Vector<Wall> getWalls() {
        return walls;
    }

    public Team autoSelectTeam() {
        Team newPlayerTeam = Team.NONE;
        if (terrorists >= counterTerrorists)
            newPlayerTeam = Team.CounterTerrorists;
        else
            newPlayerTeam = Team.Terrorists;
        return newPlayerTeam;
    }

    public void addPlayer(int id, String name, Team newPlayerTeam) {
        Player player = new Player(name, id, newPlayerTeam);
        switch (newPlayerTeam) {
            case Terrorists:
                terrorists++;
                break;
            case CounterTerrorists:
                counterTerrorists++;
                break;
            default:
                break;
        }
        players.add(player);
    }

    public Vector<Player> getUpdates(int id) {
        Vector<Player> players_to_send= new Vector<>();
        for (Player pl: players) {
            if (pl.getId() == id) continue;
            players_to_send.add(pl);
        }
        return players_to_send;
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
                return;
            }
        }
        players.add(player);
    }

    public void updatePlayers(List<Player> players) {
        for (Player p : players) {
            updatePlayer(p);
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
