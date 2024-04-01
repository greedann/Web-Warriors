package com.web.warriors.web.client;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Scanner;

public class Client {
    private static int port = 8080;
    private static String message;

    public static void main(String[] args) {
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

            // send a number of objects to the server
            while (true) {
                message = scanner.nextLine();
                if (message.equals("exit")) {
                    break;
                }
                objectOut.writeObject(message);
            }

            socket.close();

        } catch (Exception e) {
            System.out.println("Error: " + e);
        }

    }
}