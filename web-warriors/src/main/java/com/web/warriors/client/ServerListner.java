package com.web.warriors.client;

import java.io.ObjectInputStream;

public class ServerListner implements Runnable {
    ObjectInputStream objectIn;

    public ServerListner(ObjectInputStream objectIn) {
        this.objectIn = objectIn;
    }

    public void run() {
        try {
            while (true) {
                String message = objectIn.readObject().toString();
                System.out.println("Server sent: " + message);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }
}
