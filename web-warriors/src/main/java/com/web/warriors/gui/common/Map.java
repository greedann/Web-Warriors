package com.web.warriors.gui.common;

import com.web.warriors.game.GameEngine;
import com.web.warriors.game.objects.Hostage;
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
    private Vector<Hostage> hostages = new Vector<>();
    private Vector<Wall> walls = new Vector<>();
    private Player player = null;

    public Map(GameEngine gameEngine) {
        setPreferredSize(new Dimension(cellSize * mapSize, cellSize * mapSize));
        setBackground(Color.WHITE);
        setFocusable(true);
        this.players = gameEngine.getPlayers();
        this.walls = gameEngine.getWalls();
        this.hostages = gameEngine.getHostages();

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

    private void paintPlayer(Graphics g, Player player, int size, Color color) {
        g.setColor(color);
        int centerX = player.getX() * cellSize / 6 + size;
        int centerY = player.getY() * cellSize / 6 + size;

        double angleRad = player.getAngle();
        int x1 = centerX + (int) (size * Math.cos(angleRad));
        int y1 = centerY - (int) (size * Math.sin(angleRad));
        int x2 = centerX + (int) (size * Math.cos(angleRad + 2 * Math.PI / 3));
        int y2 = centerY - (int) (size * Math.sin(angleRad + 2 * Math.PI / 3));
        int x3 = centerX + (int) (size * Math.cos(angleRad - 2 * Math.PI / 3));
        int y3 = centerY - (int) (size * Math.sin(angleRad - 2 * Math.PI / 3));

        int[] PointsX = { x1, x2, x3 };
        int[] PointsY = { y1, y2, y3 };
        g.fillPolygon(PointsX, PointsY, 3);

        Hostage hostage = player.getHostage();
        if (hostage != null) {
            g.setColor(Color.YELLOW);
            int radius = cellSize / 2;

            int hostageCenterX = player.getX() * cellSize / 6 + radius;
            int hostageCenterY = player.getY() * cellSize / 6 + radius;
            g.fillOval(hostageCenterX - radius, hostageCenterY - radius, cellSize, cellSize);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (Player player : players) {
            Color playerColor = Color.BLACK;
            switch (player.getTeam()) {
                case CounterTerrorists:
                    playerColor = Color.BLUE;
                    break;
                case Terrorists:
                    playerColor = Color.RED;
                    break;
                default:
                    g.setColor(Color.GREEN);
            }
            if (player != null && player == this.player) {
                paintPlayer(g, player, cellSize / 2 + 2, Color.black);
            }
            paintPlayer(g, player, cellSize / 2, playerColor);
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

        for (Hostage hostage : hostages) {
            // draw circle
            g.setColor(Color.YELLOW);

            int radius = cellSize / 2;

            int centerX = hostage.getX() * cellSize / 6 + radius;
            int centerY = hostage.getY() * cellSize / 6 + radius;
            g.fillOval(centerX - radius, centerY - radius, cellSize, cellSize);

        }
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void setUserPlayer(Player player) {
        this.player = player;
    }
}