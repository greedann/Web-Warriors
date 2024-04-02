package com.web.warriors.web.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.warriors.game.GameEngine;
import com.web.warriors.gui.server.ServerGUI;

public class Server implements Runnable {
    static int port = 8080;
    private Hashtable<Integer, ConnectionHandler> ConnectionHandlers = new Hashtable<Integer, ConnectionHandler>();
    private ServerGUI serverGUI;
    private GameEngine gameEngine;
    ObjectMapper mapper = new ObjectMapper();

    public Server(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port);) {
            int clientCount = 0;
            while (true) {
                System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + "...");
                Socket fromClientSocket = serverSocket.accept();
                ConnectionHandler handler = new ConnectionHandler(fromClientSocket, this, clientCount);
                ConnectionHandlers.put(clientCount, handler);
                addClient(clientCount);
                System.out.println("Client " + clientCount + " connected");
                clientCount++;
                Thread t = new Thread(handler);
                t.start();
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    public void setServerGUI(ServerGUI serverGUI) {
        this.serverGUI = serverGUI;
    }

    public void addClient(int id) {
        serverGUI.addClient(id);
        gameEngine.addPlayer(id, "test");
        Map<String, Object> data = new HashMap<>();
        data.put("type", "set_id");
        data.put("id", id);
        try {
            String json = mapper.writeValueAsString(data);
            sendToOne(json, id);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    public void removeClient(int id) {
        ConnectionHandlers.remove(id);
        serverGUI.removeClient(id);
        gameEngine.removePlayer(id);
    }

    public void handleMessage(String message, int id) {
        gameEngine.handleMessage(message, id);
    }

    public void sendToAll(String message) {
        for (ConnectionHandler handler : ConnectionHandlers.values()) {
            handler.send(message);
        }
    }

    public void sendToOne(String message, int id) {
        ConnectionHandlers.get(id).send(message);
    }

}
