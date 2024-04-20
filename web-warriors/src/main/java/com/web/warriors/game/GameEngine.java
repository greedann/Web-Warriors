package com.web.warriors.game;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.warriors.game.objects.Hostage;
import com.web.warriors.game.objects.Player;
import com.web.warriors.game.objects.Team;
import com.web.warriors.game.objects.Wall;

public class GameEngine {
    Vector<Player> players;
    Vector<Hostage> hostages;
    Vector<Wall> walls;
    ObjectMapper mapper = new ObjectMapper();
    int terrorists = 0, counterTerrorists = 0, hostages_num = 0;

    public GameEngine() {
        players = new Vector<Player>();
        hostages = new Vector<Hostage>();
        loadMapWalls();
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

    public Vector<Hostage> getHostages() {
        return hostages;
    }

    public void addHostage() {
        Hostage newHostage = new Hostage(hostages_num++, 67, 67);
        hostages.add(newHostage);
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
                addHostage();
                counterTerrorists++;
                break;
            default:
                break;
        }
        players.add(player);
    }

    public Vector<Player> getUpdatesPlayers(int id) {
        Vector<Player> players_to_send = new Vector<>();
        for (Player pl : players) {
            if (pl.getTeam() == getPlayer(id).getTeam()
                    || !isWallBetween(pl.getX(), pl.getY(), getPlayer(id).getX(), getPlayer(id).getY()))
                players_to_send.add(pl);
            else {
                Player pl_copy = new Player(pl);
                pl_copy.hide();
                players_to_send.add(pl_copy);
            }
        }
        return players_to_send;
    }

    public Vector<Hostage> getUpdatesHostages(int id) {
        Vector<Hostage> hostages_to_send = new Vector<>();
        for (Hostage h : hostages) {
            hostages_to_send.add(h);
        }
        return hostages_to_send;
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void removePlayer(Integer id) {
        System.out.println("Removing player " + id);
        for (Player p : players) {
            if (p.getId() == id) {
                System.out.println("Player " + p.getId() + " disconnected");
                switch (p.getTeam()) {
                    case CounterTerrorists:
                        counterTerrorists--;
                        break;
                    case Terrorists:
                        terrorists--;
                        break;
                    default:
                        break;
                }
                players.remove(p);
                break;
            }
        }
    }

    public void updatePlayer(Player player) {
        for (Player p : players) {
            if (p.getId() == player.getId()) {
                p.setPosition(player.getX(), player.getY(), player.getAngle());
                p.setHostage(player.getHostage());
                return;
            }
        }
        players.add(player);
    }

    public void updateHostage(Hostage hostage) {
        for (Hostage h : hostages) {
            if (h.getId() == hostage.getId()) {
                h.setPosition(hostage.getX(), hostage.getY());
                return;
            }
        }
        hostages.add(hostage);
    }

    public void updateHostages(List<Hostage> hostages) {
        this.hostages.clear();
        for (Hostage h : hostages) {
            updateHostage(h);
        }
    }

    private void loadMapWalls() {
        walls = new Vector<>();
        String currentDirectory = System.getProperty("user.dir") + "/web-warriors/src/main/resources";
        try (BufferedReader reader = new BufferedReader(new FileReader(currentDirectory + "/walls.txt"))) {
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
    }

    public Boolean isWallBetween(int x1, int y1, int x2, int y2) {
        for (Wall wall : walls) {
            if (wall.intersects(x1, y1, x2, y2)) {
                return true;
            }
        }
        return false;
    }

    public void handleMessage(Map<String, Object> data, int id) {
        String type = (String) data.get("type");
        switch (type) {
            case "position":
                int x = (int) data.get("x");
                int y = (int) data.get("y");
                double angle = (double) data.get("angle");
                for (Player p : players) {
                    if (p.getId() == id) {
                        p.setPosition(x, y, angle);
                        for (Hostage h : hostages) {
                            if (Math.abs(h.getX() - p.getX()) < 15 && Math.abs(h.getY() - p.getY()) < 15) {
                                if (p.takeHostage(h)) {
                                    hostages.remove(h);
                                    break;
                                }
                            }
                        }
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
