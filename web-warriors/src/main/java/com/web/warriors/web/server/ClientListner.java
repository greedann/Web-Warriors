package com.web.warriors.web.server;

import java.io.ObjectInputStream;

public class ClientListner implements Runnable {
    ObjectInputStream objectIn;
    Server server;
    int id;

    public ClientListner(ObjectInputStream objectIn, Server server, int id) {
        this.objectIn = objectIn;
        this.server = server;
        this.id = id;
    }

    public void run() {
        try {
            while (true) {
                String message = objectIn.readObject().toString();
                // System.out.println("Client sent: " + message);
                server.handleMessage(message, id);
                if (message.equals("exit")) {
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
            // disconnect client from server
            server.removeClient(id);
            
        }
    }
}
