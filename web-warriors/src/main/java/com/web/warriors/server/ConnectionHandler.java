package com.web.warriors.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ConnectionHandler implements Runnable {
    private final Socket ClientSocket;
    private final Server server;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private ClientListner clientListner;

    public ConnectionHandler(final Socket ClientSocket, final Server server) {
        this.ClientSocket = ClientSocket;
        this.server = server;

        try {
            this.outputStream = new ObjectOutputStream(this.ClientSocket.getOutputStream());
            this.inputStream = new ObjectInputStream(this.ClientSocket.getInputStream());
            this.clientListner = new ClientListner(inputStream);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            System.out.println("Connection established");

            Thread thread = new Thread(clientListner);
            thread.start();

        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    public void send(String message) {
        try {
            outputStream.writeObject(message);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
}