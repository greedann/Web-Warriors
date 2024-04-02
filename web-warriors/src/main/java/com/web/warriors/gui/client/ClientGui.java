package com.web.warriors.gui.client;

import javax.swing.JFrame;

import com.web.warriors.game.GameEngine;
import com.web.warriors.gui.common.Map;
import com.web.warriors.web.client.Client;

public class ClientGui {
    private Client client;
    private GameEngine gameEngine;
    private Map map;

    public ClientGui(Client client, GameEngine gameEngine) {
        this.client = client;
        this.gameEngine = gameEngine;

        JFrame frame = new JFrame("Map with Players");// Another example player
        map = new Map(gameEngine);

        frame.add(map);
        frame.setSize(625, 625);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

}
