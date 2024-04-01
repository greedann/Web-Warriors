package com.web.warriors.game;

import com.web.warriors.game.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class Map extends JPanel implements KeyListener {
    private final int cellSize = 50; // Size of each cell in pixels
    private final int mapSize = 10; // Size of the map (10x10)
    private final List<Player> players = new ArrayList<>();

    public Map() {
        setPreferredSize(new Dimension(cellSize * mapSize, cellSize * mapSize));
        setBackground(Color.WHITE);
        setFocusable(true);
        addKeyListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw grid
        for (int x = 0; x <= getWidth(); x += cellSize) {
            g.drawLine(x, 0, x, getHeight());
        }
        for (int y = 0; y <= getHeight(); y += cellSize) {
            g.drawLine(0, y, getWidth(), y);
        }
        // Draw players
        for (Player player : players) {
            g.setColor(player.getColor());
            g.fillRect(player.getX() * cellSize, player.getY() * cellSize, cellSize, cellSize);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        // Move players based on arrow keys
        for (Player player : players) {
            int newX = player.getX();
            int newY = player.getY();
            if (key == KeyEvent.VK_LEFT && newX > 0) {
                newX--;
            } else if (key == KeyEvent.VK_RIGHT && newX < mapSize - 1) {
                newX++;
            } else if (key == KeyEvent.VK_UP && newY > 0) {
                newY--;
            } else if (key == KeyEvent.VK_DOWN && newY < mapSize - 1) {
                newY++;
            }
            player.setPosition(newX, newY);
        }
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    public void addPlayer(Player player) {
        players.add(player);
    }
}