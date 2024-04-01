package com.web.warriors.web.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

import com.web.warriors.gui.server.ServerMain;

public class Server implements Runnable {
    static int port = 8080;
    private Hashtable<Integer, ConnectionHandler> ConnectionHandlers = new Hashtable<Integer, ConnectionHandler>();
    private ServerMain serverMain;

    public Server(ServerMain serverMain) {
        this.serverMain = serverMain;
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
                serverMain.addClient(clientCount);
                System.out.println("Client " + clientCount + " connected");
                clientCount++;
                Thread t = new Thread(handler);
                t.start();
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
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
