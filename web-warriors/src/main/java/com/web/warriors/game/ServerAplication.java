package com.web.warriors.game;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.warriors.game.objects.Player;
import com.web.warriors.game.objects.Team;
import com.web.warriors.gui.server.ServerGUI;
import com.web.warriors.web.server.Server;

public class ServerAplication implements Runnable {
    GameEngine gameEngine;
    Server server;
    ServerGUI serverGUI;
    ObjectMapper mapper = new ObjectMapper();

    public ServerAplication() {
        gameEngine = new GameEngine();
    }

    @Override
    public void run() {
        server = new Server(this);
        serverGUI = new ServerGUI(server, gameEngine);

        Thread serverThread = new Thread(server);
        serverThread.start();

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
            String json = dataToJson(gameEngine.getUpdates(player.getId()));
            data.put("type", "updates");
            data.put("players", json);
            sendToOne(data, player.getId());
        }
    }

    public static void main(String[] args) {
        ServerAplication serverAplication = new ServerAplication();
        Thread serverThread = new Thread(serverAplication);
        serverThread.start();
    }

}
