package com.web.warriors.web.client;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TimerTask;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.warriors.game.ClientAplication;
import com.web.warriors.game.GameEngine;
import com.web.warriors.game.objects.Player;
import com.web.warriors.gui.client.ClientGui;

public class Client implements Runnable {
    private final int TICKS_PER_SECOND = 3;
    private final int MILLISECONDS_PER_TICK = 1000 / TICKS_PER_SECOND;
    private static int port = 8080;
    private String json;
    private GameEngine gameEngine;
    private ClientGui clientGui;
    private ClientAplication clientAplication;
    private int id;
    String name;
    ObjectMapper mapper = new ObjectMapper();

    java.net.Socket socket;
    OutputStream out;
    ObjectOutputStream objectOut;
    InputStream in;
    ObjectInputStream objectIn;

    public Client(GameEngine gameEngine, ClientAplication clientAplication) {
       // gameEngine = new GameEngine();
        this.gameEngine = gameEngine;
        this.clientAplication = clientAplication;
    }

    @Override
    public void run() {
        try {
            socket = new java.net.Socket("localhost", port);
            out = socket.getOutputStream();
            objectOut = new ObjectOutputStream(out);
            in = socket.getInputStream();
            objectIn = new ObjectInputStream(in);

            System.out.println("Connected to server");
            ServerListner serverListner = new ServerListner(objectIn, this);
            Thread thread = new Thread(serverListner);
            thread.start();
            // connection established

            // send player pos to server
            new java.util.Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    sentToServer(clientAplication.getPlayerPosJson());
                }
            }, 0, MILLISECONDS_PER_TICK);

            // send message to server TICKS_PER_SECOND times per second

            int x = (int) (Math.random() * 140) + 10;
            int y = (int) (Math.random() * 140) + 10;

//            while (true) {
//                // rand bool
//                x = x + 1;
//                y = y + 2;
//
//                x = x % 150;
//                y = y % 150;
//
//                clientAplication.updatePlayerPos(x, y);
//                Thread.sleep(MILLISECONDS_PER_TICK);
//            }

            // socket.close();

        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    public void sentToServer(String message) {
        if (message == null) {
            return;
        }
        try {
            objectOut.writeObject(message);
            objectOut.flush();
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    public void setId(int id) {
        this.id = id;
        clientAplication.setPlayer(new Player(name, id));
    }

    public Integer getId() {
        return id;
    }

    public void setClientGui(ClientGui clientGui) {
        this.clientGui = clientGui;
    }

    public ClientGui getClientGui() {
        return clientGui;
    }
}