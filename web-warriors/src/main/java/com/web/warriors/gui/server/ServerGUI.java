package com.web.warriors.gui.server;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import com.web.warriors.game.GameEngine;
import com.web.warriors.game.ServerApplication;
import com.web.warriors.gui.common.Map;
import com.web.warriors.web.server.Server;

public class ServerGUI {
    private ServerApplication application;
    private ServerClients serverClients;
    private Map map;

    public ServerGUI(Server server, GameEngine gameEngine, ServerApplication application) {
        this.application = application;

        serverClients = new ServerClients(this);

        // create new window which will display game state
        JFrame frame = new JFrame("Server");
        map = new Map(gameEngine);

        frame.add(map);
        frame.setSize(625, 625);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.requestFocus();
        frame.repaint();

        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }
        
            @Override
            public void keyReleased(KeyEvent e) {
            }
        
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_P) {
                    if (gameEngine.isPaused()){
                        application.resume();
                    } else {
                        application.pause();
                    }
                }
            }
        });

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                application.exit();
                frame.dispose();
            }
        });

    }

    public void addClient(Integer clientID) {
        serverClients.addClient(clientID);
    }

    public void removeClient(Integer clientID) {
        serverClients.removeClient(clientID);
    }

    public void sendToOne(String message, int id) {
        application.sendToOne(message, id);
    }

    public void sendToAll(String message) {
        application.sendToAll(message);
    }
}
