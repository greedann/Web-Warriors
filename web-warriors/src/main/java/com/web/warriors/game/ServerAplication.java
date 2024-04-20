package com.web.warriors.game;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.warriors.game.objects.Player;
import com.web.warriors.game.objects.Team;
import com.web.warriors.gui.server.ServerGUI;
import com.web.warriors.web.server.Server;

public class ServerAplication {
    GameEngine gameEngine;
    Server server;
    ServerGUI serverGUI;
    ObjectMapper mapper = new ObjectMapper();

    public ServerAplication() {
        gameEngine = new GameEngine();
        server = new Server(this);
        serverGUI = new ServerGUI(server, gameEngine);
    }

    public void start() {
        server.run();
    }

    public void sendToOne(String message, int id) {
        server.sendToOne(message, id);
    }

    public void sendToAll(String message) {
        server.sendToAll(message);
    }

    public void sendToOne(Map<String, Object> data, int id) {
        String json = dataToJson(data);
        sendToOne(json, id);
    }

    public void sendToAll(Map<String, Object> data) {
        String json = dataToJson(data);
        sendToAll(json);
    }

    public void addClient(int id) {
        serverGUI.addClient(id);
        Team team = gameEngine.autoSelectTeam();
        gameEngine.addPlayer(id, "test", team);
        Map<String, Object> data = new HashMap<>();
        data.put("type", "set_id");
        data.put("id", id);
        data.put("team", team);
        sendToOne(data, id);
    }

    public void removeClient(int id) {
        serverGUI.removeClient(id);
        gameEngine.removePlayer(id);
        // send to all players
        Map<String, Object> data = new HashMap<>();
        data.put("type", "remove_player");
        data.put("id", id);
        sendToAll(data);
    }

    public String dataToJson(Map<String, Object> data) {
        try {
            String json = mapper.writeValueAsString(data);
            return json;
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        return null;
    }

    public String dataToJson(Vector<Player> data) {
        try {
            String json = mapper.writeValueAsString(data);
            return json;
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        return null;
    }

    public void handleMessage(Map<String, Object> data, int id) {
        gameEngine.handleMessage(data, id);
    }

    public void sendUpdates() {
        for (Player player : gameEngine.getPlayers()) {
            Map<String, Object> data = new HashMap<>();
            data.put("type", "updates");
            data.put("players", gameEngine.getUpdatesPlayers(player.getId()));
            data.put("hostages", gameEngine.getUpdatesHostages(player.getId()));
            sendToOne(data, player.getId());
        }
    }

    public static void main(String[] args) {
        ServerAplication serverAplication = new ServerAplication();
        serverAplication.start();
    }
}
