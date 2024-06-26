package com.web.warriors.game;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.warriors.game.objects.Bullet;
import com.web.warriors.game.objects.Hostage;
import com.web.warriors.game.objects.Message;
import com.web.warriors.game.objects.Player;
import com.web.warriors.game.objects.Team;
import com.web.warriors.gui.client.ClientGui;
import com.web.warriors.web.client.Client;

public class ClientApplication {
    GameEngine gameEngine;
    Client client;
    ClientGui clientGui;
    Player myPlayer = null;
    ObjectMapper mapper = new ObjectMapper();

    public ClientApplication() {
        gameEngine = new GameEngine();
        client = new Client(this);
        clientGui = new ClientGui(this, gameEngine);
    }

    public void start() {
        client.run();
    }

    public void setPlayer(Player player) {
        myPlayer = player;
        gameEngine.addPlayer(player);
    }

    public void updatePlayerPos(int x, int y) {
        if (myPlayer == null) {
            return;
        }
        myPlayer.move(x, y);
    }

    public String getPlayerPosJson() {
        if (myPlayer == null) {
            return null;
        }
        Map<String, Object> data = new HashMap<>();
        data.put("type", "position");
        data.put("x", myPlayer.getX());
        data.put("y", myPlayer.getY());
        data.put("angle", myPlayer.getAngle());
        try {
            return mapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Player getPlayer() {
        return myPlayer;
    }

    public void init(int id, Team team) {
        if (myPlayer == null) {
            setPlayer(new Player("Me", id, team));
        } else {
            myPlayer.setId(id);
        }
        clientGui.setPlayer(myPlayer);
    }

    public int getId() {
        return myPlayer.getId();
    }

    public void exit() {
        client.disconnect();
        System.exit(0);
    }

    public void setTeam(Team team) {
        myPlayer.setTeam(team);
    }

    public void updatePlayers(List<Player> players) {
        for (Player p : players) {
            if (p.getId() == myPlayer.getId()) {
                myPlayer.setHostage(p.getHostage());
                continue;
            }
            gameEngine.updatePlayer(p);
        }
    }

    public Hostage getHostage() {
        for (Player p : gameEngine.getPlayers()) {
            if (p.getId() == myPlayer.getId()) {
                return p.getHostage();
            }
        }
        return null;
    }

    public Collection<Player> getPlayers() {
        return gameEngine.getPlayers();
    }

    public Collection<Hostage> getHostagesFromGE() {
        return gameEngine.getHostages();
    }

    public void updateHostages(List<Hostage> hostages) {
        gameEngine.updateHostages(hostages);
    }

    public void removePlayer(int id) {
        gameEngine.removePlayer(id);
    }

    public boolean isPaused() {
        return gameEngine.isPaused();
    }

    public void pause() {
        gameEngine.pause();
    }

    public void resume() {
        gameEngine.resume();
    }

    public void notifyTeam(Message message) {
        if (myPlayer == null) {
            return;
        }
        Map<String, Object> data = new HashMap<>();
        data.put("type", "message");
        data.put("message", message);
        data.put("team", myPlayer.getTeam().toString());
        client.sendToServer(data);
    }

    public void processTeamMessage(Message message) {
        clientGui.processTeamMessage(message);
    }

    public void shoot(Player player) {
        Double angle = player.getAngle() + (Math.random() * 0.1 - 0.05);
        Bullet bullet = new Bullet(player.getX(), player.getY(), angle, player.getTeam());
        gameEngine.addBullet(bullet);
        Map<String, Object> data = new HashMap<>();
        data.put("type", "shoot");
        data.put("bullet", bullet);
        client.sendToServer(data);
    }

    public void processMessage(Map<String, Object> data, int id) {
        gameEngine.processMessage(data, id);
    }

    public static void main(String[] args) {
        ClientApplication clientApplication = new ClientApplication();
        clientApplication.start();
    }
}
