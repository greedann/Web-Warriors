package com.web.warriors.web.client;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Scanner;

public class Client implements Runnable {
    private final int TICKS_PER_SECOND = 1;
    private final int MILLISECONDS_PER_TICK = 1000 / TICKS_PER_SECOND;
    private static int port = 8080;
    private static String message;

    public static void main(String[] args) {
        Client client = new Client();
        Thread thread = new Thread(client);
        thread.start();
    }

    @Override
    public void run() {
        try (
                java.net.Socket socket = new java.net.Socket("localhost", port);
                OutputStream out = socket.getOutputStream();
                ObjectOutputStream objectOut = new ObjectOutputStream(out);
                InputStream in = socket.getInputStream();
                ObjectInputStream objectIn = new ObjectInputStream(in);
                Scanner scanner = new Scanner(System.in);) {

            System.out.println("Connected to server");
            ServerListner serverListner = new ServerListner(objectIn);
            Thread thread = new Thread(serverListner);
            thread.start();
            // connection established
            int x = 0;
            int y = 0;

            // send message to server TICKS_PER_SECOND times per second
            while (true) {
                message = "move " + x++ + " " + y++;
                objectOut.writeObject(message);
                objectOut.flush();
                x = x % 10;
                y = y % 10;
                Thread.sleep(MILLISECONDS_PER_TICK);
            }

            // socket.close();

        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }
}