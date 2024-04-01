package com.web.warriors.game;

import javax.swing.*;
import java.awt.*;

public class main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Map with Players");
        Map map = new Map();
        map.addPlayer(new Player(0, 0, Color.BLUE));
        map.addPlayer(new Player(5, 5, Color.RED));
        map.addPlayer(new Player(7, 7, Color.GREEN));
        frame.add(map);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
