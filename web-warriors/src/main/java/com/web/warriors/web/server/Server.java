package com.web.warriors.web.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.warriors.game.ServerAplication;

public class Server implements Runnable {
    private final int TICKS_PER_SECOND = 30;
    private final int MILLISECONDS_PER_TICK = 1000 / TICKS_PER_SECOND;
    static int port = 8080;
    private Hashtable<Integer, ConnectionHandler> ConnectionHandlers = new Hashtable<Integer, ConnectionHandler>();
    private ServerAplication aplication;
    ObjectMapper mapper = new ObjectMapper();
    Timer playersSender = new Timer();

    public Server(ServerAplication aplication) {
        this.aplication = aplication;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port);) {
            int clientCount = 0;
            playersSender.schedule(new TimerTask() {
                @Override
                public void run() {
                    aplication.sendUpdates();
                }
            }, 0, MILLISECONDS_PER_TICK);
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
            e.printStackTrace();
        }
    }

    public void addClient(int id) {
        aplication.addClient(id);
    }

    public void removeClient(int id) {
        ConnectionHandlers.get(id).close();
        ConnectionHandlers.remove(id);
        aplication.removeClient(id);
    }

    public void handleMessage(Map<String, Object> data, int id) {
        aplication.handleMessage(data, id);
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
