package com.web.warriors.game;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.warriors.game.objects.Hostage;
import com.web.warriors.game.objects.Message;
import com.web.warriors.game.objects.Player;
import com.web.warriors.game.objects.Team;
import com.web.warriors.game.objects.Wall;
import com.web.warriors.game.objects.Bullet;

public class GameEngine {
    Collection<Player> players;
    Collection<Hostage> hostages;
    Collection<Bullet> bullets;
    Vector<Wall> walls;
    Vector<Point> hostagePoints;
    ObjectMapper mapper = new ObjectMapper();
    String currentDirectory = System.getProperty("user.dir") + "/web-warriors/src/main/resources";
    int terrorists = 0, counterTerrorists = 0, hostages_num = 0;
    boolean isPaused = true;

    public GameEngine() {
        players = new CopyOnWriteArrayList<>();
        hostages = new CopyOnWriteArrayList<>();
        bullets = new CopyOnWriteArrayList<>();
        loadMapWalls();
        loadHostagePoints();
        Timer bulletsTimer = new Timer();
        bulletsTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                moveBullets();
            }
        }, 1000, 1000 / 30);
    }

    public Player getPlayer(int id) {
        for (Player p : players) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    public Collection<Player> getPlayers() {
        return players;
    }

    public Collection<Hostage> getHostages() {
        return hostages;
    }

    public Collection<Bullet> getBullets() {
        return bullets;
    }

    public void addHostage() {
        Point point = new Point(hostagePoints.get(hostages_num % 4 + 1));
        Hostage newHostage = new Hostage(hostages_num++, (int) point.getX(), (int) point.getY());
        hostages.add(newHostage);
    }

    public void addWall(Wall wall) {
        walls.add(wall);
    }

    public void addBullet(Bullet bullet) {
        bullets.add(bullet);
    }

    public void removeBullet(Bullet bullet) {
        bullets.remove(bullet);
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

    private void loadHostagePoints() {
        hostagePoints = new Vector<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(currentDirectory + "/hostagePoints.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int x = Integer.parseInt(parts[0]);
                int y = Integer.parseInt(parts[1]);
                hostagePoints.add(new Point(x, y));
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

    public void processMessage(Map<String, Object> data, int id) {
        String type = (String) data.get("type");
        switch (type) {
            case "position":
                if (isPaused)
                    return;
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

                        // check if player carried hostage to ct spawh
                        if (p.getTeam() == Team.CounterTerrorists && p.getHostage() != null) {
                            // check if position is nearby
                            if (Math.abs(p.getX() - 95) < 3 && Math.abs(p.getY() - 137) < 15) {
                                p.leaveHostage();
                                addHostage();
                            }
                        }
                        break;
                    }
                }
                break;

            case "disconnect":
                removePlayer(id);
                break;

            case "shoot":
                if (isPaused)
                    return;
                String bullet_str;
                try {
                    bullet_str = mapper.writeValueAsString(data.get("bullet"));
                    Bullet bullet = mapper.readValue(bullet_str, new TypeReference<Bullet>() {
                    });
                    addBullet(bullet);
                } catch (JsonProcessingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            default:
                System.out.println("Unknown message type: " + type);
                break;
        }
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void pause() {
        System.out.println("Pausing game");
        isPaused = true;
    }

    public void resume() {
        System.out.println("Resuming game");
        isPaused = false;
    }

    public void moveBullets() {
        for (Bullet bullet : bullets) {
            bullet.move();
            if (bullet.isOutOfBounds() || bullet.isColliding(walls)) {
                bullets.remove(bullet);
                continue;
            }
            for (Player player : players) {
                if (!player.isAlive())
                    continue;
                if (player.getTeam() == bullet.getTeam())
                    continue;
                if (bullet.isColliding(player)) {
                    System.out.println("Player " + player.getId() + " was shot");
                    bullets.remove(bullet);
                    player.die();

                    break;
                }
            }
        }
    }

    public Vector<Integer> getWhoCanSee(int id) {
        Player player = getPlayer(id);
        Vector<Integer> whoCanSee = new Vector<>();
        for (Player p : players) {
            if (p.getId() == id)
                continue;
            if (p.getTeam() == player.getTeam())
                whoCanSee.add(p.getId());
            else if (!isWallBetween(p.getX(), p.getY(), player.getX(), player.getY()))
                whoCanSee.add(p.getId());
        }
        return whoCanSee;
    }
}
