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
        serverGUI = new ServerGUI(server, gameEngine, this);
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

    public String dataToJson(Object data) {
        try {
            String json = mapper.writeValueAsString(data);
            return json;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void processMessage(Map<String, Object> data, int id) {
        gameEngine.processMessage(data, id);
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

    public void pause() {
        gameEngine.pause();
        Map<String, Object> data = new HashMap<>();
        data.put("type", "pause");
        sendToAll(data);
    }

    public void resume() {
        gameEngine.resume();
        Map<String, Object> data = new HashMap<>();
        data.put("type", "resume");
        sendToAll(data);
    }

    public void exit() {
        server.shutdown();
        System.exit(0);
    }

    public static void main(String[] args) {
        ServerAplication serverAplication = new ServerAplication();
        serverAplication.start();
    }
}
