package com.web.warriors.web.client;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.warriors.game.ClientAplication;
import com.web.warriors.game.objects.Hostage;
import com.web.warriors.game.objects.Player;
import com.web.warriors.game.objects.Team;

public class Client implements Runnable {
    private final int TICKS_PER_SECOND = 30;
    private final int MILLISECONDS_PER_TICK = 1000 / TICKS_PER_SECOND;
    private static int port = 8080;
    private ClientAplication clientAplication;
    ObjectMapper mapper = new ObjectMapper();
    Timer positionSender = new Timer();

    java.net.Socket socket;
    OutputStream out;
    ObjectOutputStream objectOut;
    InputStream in;
    ObjectInputStream objectIn;
    ServerListner serverListner = null;

    public Client(ClientAplication clientAplication) {
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
            serverListner = new ServerListner(objectIn, this);
            Thread thread = new Thread(serverListner);
            thread.start();
            // connection established

            // send player pos to server
            positionSender.schedule(new TimerTask() {
                @Override
                public void run() {
                    sendToServer(clientAplication.getPlayerPosJson());
                }
            }, 0, MILLISECONDS_PER_TICK);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendToServer(String message) {
        // System.out.println(message);
        if (message == null) {
            return;
        }
        try {
            objectOut.writeObject(message);
            objectOut.flush();
        } catch (java.net.SocketException e) {
            System.out.println("Server disconnected");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendToServer(Map<String, Object> data) {
        String msg;
        try {
            msg = mapper.writeValueAsString(data);
            sendToServer(msg);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void init(int id, Team team) {
        clientAplication.init(id, team);
    }

    public void removePlayer(int id) {
        clientAplication.removePlayer(id);
    }

    public int getId() {
        return clientAplication.getPlayer().getId();
    }

    public void setTeam(Team team) {
        clientAplication.setTeam(team);
    }

    public void updatePlayers(List<Player> players) {
        clientAplication.updatePlayers(players);
    }

    public void updateHostages(List<Hostage> hostages) {
        clientAplication.updateHostages(hostages);
    }

    public void disconnect() {
        Map<String, Object> data = new HashMap<>();
        data.put("type", "disconnect");
        sendToServer(data);

        exit();
    }

    public void exit() {
        serverListner.stop();
        positionSender.cancel();
        

        try {
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // stop client thread
        Thread.currentThread().interrupt();
        System.out.println("Client disconnected");
    }

    public void pause() {
        clientAplication.pause();
    }

    public void resume() {
        clientAplication.resume();
    }

}