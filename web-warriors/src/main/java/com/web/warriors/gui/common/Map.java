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
            switch (player.getTeam()) {
                case CounterTerrorists:
                    g.setColor(Color.BLUE);
                    break;
                case Terrorists:
                    g.setColor(Color.RED);
                    break;
                default:
                    g.setColor(Color.GREEN);
            }
            int radius = cellSize / 2;
            int centerX = player.getX() * cellSize / 6 + radius;
            int centerY = player.getY() * cellSize / 6 + radius;

            double angleRad = player.getAngle();
            int x1 = centerX + (int) (radius * Math.cos(angleRad)); 
            int y1 = centerY - (int) (radius * Math.sin(angleRad));
            int x2 = centerX + (int) (radius * Math.cos(angleRad + 2 * Math.PI / 3));
            int y2 = centerY - (int) (radius * Math.sin(angleRad + 2 * Math.PI / 3));
            int x3 = centerX + (int) (radius * Math.cos(angleRad - 2 * Math.PI / 3));
            int y3 = centerY - (int) (radius * Math.sin(angleRad - 2 * Math.PI / 3));

            int[] PointsX = { x1, x2, x3 };
            int[] PointsY = { y1, y2, y3 };
            g.fillPolygon(PointsX, PointsY, 3);
        }

        for (Wall wall : walls) {

            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.BLACK);
            // set width of linr
            g2d.setStroke(new BasicStroke(5));

            // draw straight line
            g2d.drawLine(wall.getStart_x() * cellSize / 6, wall.getStart_y() * cellSize / 6,
                    wall.getEnd_x() * cellSize / 6, wall.getEnd_y() * cellSize / 6);
        }
    }

    public void addPlayer(Player player) {
        players.add(player);
    }
}