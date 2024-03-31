package com.web.warriors.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Vector;

public class Server implements Runnable {
    static int port = 8080;
    static Vector<Socket> clients = new Vector<Socket>();
    private final ArrayList<ConnectionHandler> ConnectionHandlers;

    public Server() {
        ConnectionHandlers = new ArrayList<ConnectionHandler>();
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port);) {
            int clientCount = 0;
            while (true) {
                System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + "...");
                Socket fromClientSocket = serverSocket.accept();
                clients.add(fromClientSocket);
                ConnectionHandler handler = new ConnectionHandler(fromClientSocket, this);
                ConnectionHandlers.add(handler);
                Thread t = new Thread(handler);
                t.start();
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    public void sendToAll(String message) {
        for (ConnectionHandler handler : ConnectionHandlers) {
            handler.send(message);
        }
    }

    public static void main(String[] args) {
        new Server().run();
    }
}
