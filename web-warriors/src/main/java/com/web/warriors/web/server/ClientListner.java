package com.web.warriors.web.server;

import java.io.ObjectInputStream;

public class ClientListner implements Runnable {
    ObjectInputStream objectIn;

    public ClientListner(ObjectInputStream objectIn) {
        this.objectIn = objectIn;
    }

    public void run() {
        try {
            while (true) {
                String message = objectIn.readObject().toString();
                System.out.println("Client sent: " + message);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }
}
