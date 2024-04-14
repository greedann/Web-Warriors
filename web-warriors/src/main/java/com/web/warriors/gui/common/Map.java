package com.web.warriors.gui.common;

import com.web.warriors.game.GameEngine;
import com.web.warriors.game.objects.Player;
import com.web.warriors.game.objects.Wall;

import javax.swing.*;
import java.awt.*;
import java.util.TimerTask;
import java.util.Vector;

public class Map extends JPanel {
    private final int TICKS_PER_SECOND = 30;
    private final int MILLISECONDS_PER_TICK = 1000 / TICKS_PER_SECOND;
    private final int cellSize = 25; // Size of each cell in pixels
    private final int mapSize = 25; // Size of the map (10x10)
    private Vector<Player> players = new Vector<>();
    private Vector<Wall> walls = new Vector<>();

    public Map(GameEngine gameEngine) {
        setPreferredSize(new Dimension(cellSize * mapSize, cellSize * mapSize));
        setBackground(Color.WHITE);
        setFocusable(true);
        this.players = gameEngine.getPlayers();
        this.walls = gameEngine.getWalls();

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

        for(Wall wall : walls) {

            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.BLACK);
            // Устанавливаем толщину линии
            g2d.setStroke(new BasicStroke(5));

            // Рисуем прямую линию
            g2d.drawLine(wall.getStart_x()* cellSize / 6 , wall.getStart_y()* cellSize / 6 , wall.getEnd_x()* cellSize / 6 , wall.getEnd_y()* cellSize / 6 );
            System.out.println("Drawing wall");
        }
    }

    public void addPlayer(Player player) {
        players.add(player);
    }
}