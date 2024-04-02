package com.web.warriors.game;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.warriors.game.objects.Player;
import com.web.warriors.gui.client.ClientGui;
import com.web.warriors.web.client.Client;

public class ClientAplication implements Runnable {
    GameEngine gameEngine;
    Client client;
    ClientGui clientGui;
    Player myPlayer;
    ObjectMapper mapper = new ObjectMapper();

    public ClientAplication() {
        gameEngine = new GameEngine();
    }

    @Override
    public void run() {
        client = new Client(gameEngine, this);
        clientGui = new ClientGui(client, gameEngine);
        client.setClientGui(clientGui);
        Thread clientThread = new Thread(client);
        clientThread.start();
        //client.getClientGui().setPlayer(gameEngine.getPlayer(client.getId()));

        //myPlayer = gameEngine.getPlayer(client.getId());
        //System.out.println("Client started"+client.getId());

    }

    public void setPlayer(Player player) {
        myPlayer = player;
        gameEngine.addPlayer(player);
    }

    public void updatePlayerPos(int x, int y) {
        if (myPlayer == null) {
            return;
        }
        myPlayer.setPosition(x, y);
    }

    public String getPlayerPosJson() {
        if (myPlayer == null) {
            return null;
        }
        Map<String, Object> data = new HashMap<>();
        data.put("type", "position");
        data.put("x", myPlayer.getX());
        data.put("y", myPlayer.getY());
        try {
            System.out.println(mapper.writeValueAsString(data));
            return mapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        ClientAplication clientAplication = new ClientAplication();
        Thread clientThread = new Thread(clientAplication);
        clientThread.start();
    }
}
