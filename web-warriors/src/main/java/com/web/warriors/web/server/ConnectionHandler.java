package com.web.warriors.web.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ConnectionHandler implements Runnable {
    private final Socket ClientSocket;
    private final int id;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private ClientListener clientListener;

    public ConnectionHandler(Socket ClientSocket, Server server, int id) {
        this.ClientSocket = ClientSocket;
        this.id = id;

        try {
            this.outputStream = new ObjectOutputStream(this.ClientSocket.getOutputStream());
            this.inputStream = new ObjectInputStream(this.ClientSocket.getInputStream());
            this.clientListener = new ClientListener(inputStream, server, id);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            System.out.println("Connection established with client " + id);

            Thread thread = new Thread(clientListener);
            thread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void send(String message) {
        try {
            outputStream.writeObject(message);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            ClientSocket.close();
            Thread.currentThread().interrupt();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
}