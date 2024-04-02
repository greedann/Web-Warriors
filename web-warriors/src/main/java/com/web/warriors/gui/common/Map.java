package com.web.warriors.gui.common;

import com.web.warriors.game.GameEngine;
import com.web.warriors.game.objects.Player;

import javax.swing.*;
import java.awt.*;
import java.util.TimerTask;
import java.util.Vector;

public class Map extends JPanel {
    private final int TICKS_PER_SECOND = 30;
    private final int MILLISECONDS_PER_TICK = 1000 / TICKS_PER_SECOND;
    private final int cellSize = 25; // Size of each cell in pixels
    private final int mapSize = 25; // Size of the map (10x10)
    private GameEngine gameEngine;
    private Vector<Player> players = new Vector<>();

    public Map(GameEngine gameEngine) {
        setPreferredSize(new Dimension(cellSize * mapSize, cellSize * mapSize));
        setBackground(Color.WHITE);
        setFocusable(true);
        this.players = gameEngine.getPlayers();
        this.gameEngine = gameEngine;
        // addKeyListener(this);

        // call refresh every MILLISECONDS_PER_TICK milliseconds
        new java.util.Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                refresh();
            }
        }, 0, MILLISECONDS_PER_TICK);

    }

    public void refresh() {
        SwingUtilities.invokeLater(() -> {
            this.repaint();
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (Player player : players) {
            g.setColor(Color.RED);
            int radius = cellSize / 2; // Радиус круга
            int centerX = player.getX() * cellSize / 6 + radius; // Центр круга по оси X
            int centerY = player.getY() * cellSize / 6 + radius; // Центр круга по оси Y
            g.fillOval(centerX - radius, centerY - radius, cellSize, cellSize);
        }
    }

    // @Override
    // public void keyPressed(KeyEvent e) {
    // int key = e.getKeyCode();
    // // Move players based on arrow keys
    // for (Player player : players) {
    // int newX = player.getX();
    // int newY = player.getY();
    // if (key == KeyEvent.VK_LEFT && newX > 0) {
    // newX--;
    // } else if (key == KeyEvent.VK_RIGHT && newX < mapSize - 1) {
    // newX++;
    // } else if (key == KeyEvent.VK_UP && newY > 0) {
    // newY--;
    // } else if (key == KeyEvent.VK_DOWN && newY < mapSize - 1) {
    // newY++;
    // }
    // player.setPosition(newX, newY);
    // }
    // repaint();
    // }
    //
    // @Override
    // public void keyTyped(KeyEvent e) {
    // }
    //
    // @Override
    // public void keyReleased(KeyEvent e) {
    // }

    public void addPlayer(Player player) {
        players.add(player);
    }
}