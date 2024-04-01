package com.web.warriors.game;

import com.web.warriors.gui.server.ServerGUI;
import com.web.warriors.web.server.Server;

public class ServerAplication implements Runnable {
    GameEngine gameEngine;
    Server server;
    ServerGUI serverGUI;

    public ServerAplication() {
        gameEngine = new GameEngine();
    }

    @Override
    public void run() {
        server = new Server(gameEngine);
        serverGUI = new ServerGUI(server, gameEngine);
        server.setServerGUI(serverGUI);
        Thread serverThread = new Thread(server);
        serverThread.start();

    }

    public static void main(String[] args) {
        ServerAplication serverAplication = new ServerAplication();
        Thread serverThread = new Thread(serverAplication);
        serverThread.start();
    }

}
