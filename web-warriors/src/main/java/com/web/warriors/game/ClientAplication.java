package com.web.warriors.game;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Vector;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.warriors.game.objects.Hostage;
import com.web.warriors.game.objects.Player;
import com.web.warriors.game.objects.Team;
import com.web.warriors.gui.client.ClientGui;
import com.web.warriors.web.client.Client;

public class ClientAplication {
    GameEngine gameEngine;
    Client client;
    ClientGui clientGui;
    Player myPlayer = null;
    ObjectMapper mapper = new ObjectMapper();

    public ClientAplication() {
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
            // TODO Auto-generated catch block
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
    public Vector<Hostage> getHostages() {
        Vector <Hostage> hostages = new Vector<>();
        for (Player p : gameEngine.getPlayers()) {
            if(p.getHostage() != null) {
                hostages.add(p.getHostage());
            }
        }
        return hostages;
    }
    public Vector<Hostage> getHostagesFromGE() {
        return gameEngine.getHostages();
    }

    public void updateHostages(List<Hostage> hostages) {
        gameEngine.updateHostages(hostages);
    }

    public void removePlayer(int id) {
        gameEngine.removePlayer(id);
    }

    public static void main(String[] args) {
        ClientAplication clientAplication = new ClientAplication();
        clientAplication.start();
    }
}
