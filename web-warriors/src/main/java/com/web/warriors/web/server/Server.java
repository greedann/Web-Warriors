package com.web.warriors.web.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.xml.crypto.Data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.warriors.game.ServerApplication;
import com.web.warriors.game.objects.Message;

import com.fasterxml.jackson.core.type.TypeReference;

public class Server implements Runnable {
    private final int TICKS_PER_SECOND = 30;
    private final int MILLISECONDS_PER_TICK = 1000 / TICKS_PER_SECOND;
    static int port = 8080;
    private Hashtable<Integer, ConnectionHandler> ConnectionHandlers = new Hashtable<Integer, ConnectionHandler>();
    private ServerApplication application;
    ObjectMapper mapper = new ObjectMapper();
    Timer playersSender = new Timer();

    public Server(ServerApplication application) {
        this.application = application;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port);) {
            int clientCount = 0;
            playersSender.schedule(new TimerTask() {
                @Override
                public void run() {
                    application.sendUpdates();
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
        application.addClient(id);
    }

    public void removeClient(int id) {
        ConnectionHandlers.get(id).close();
        ConnectionHandlers.remove(id);
        application.removeClient(id);
    }

    public void processMessage(Map<String, Object> data, int id) {
        String type = (String) data.get("type");
        if (type.equals("message")) {
            try {
                String msg = mapper.writeValueAsString(data.get("message"));
                Message message = mapper.readValue(msg, new TypeReference<Message>() {});
                for (Integer to_id : message.getReceives()) {
                    sendToOne(mapper.writeValueAsString(data), to_id);
                };
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            application.processMessage(data, id);
        }
    }

    public void sendToAll(String message) {
        for (ConnectionHandler handler : ConnectionHandlers.values()) {
            handler.send(message);
        }
    }

    public void sendToAll(Map<String, Object> data) {
        String json = application.dataToJson(data);
        sendToAll(json);
    }

    public void sendToOne(String message, int id) {
        ConnectionHandlers.get(id).send(message);
    }

    public void recastToAllExcept(String message, int id) {
        Vector<Integer> recipients = application.getWhoCanSee(id);
        for (Integer recipient : recipients) {
            if (recipient != id) {
                sendToOne(message, recipient);
            }
        }
    }

    public void shutdown() {
        Map<String, Object> data = new HashMap<>();
        data.put("type", "shutdown");
        sendToAll(data);
        for (ConnectionHandler handler : ConnectionHandlers.values()) {
            handler.close();
        }
    }

}
