package com.web.warriors.gui.client;

import javax.swing.JFrame;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.warriors.game.ClientAplication;
import com.web.warriors.game.GameEngine;
import com.web.warriors.game.objects.Player;
import com.web.warriors.game.objects.Wall;
import com.web.warriors.gui.common.Map;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

public class ClientGui {
    private ClientAplication clientAplication;
    private Map map;
    private Player player = null;
    ObjectMapper mapper = new ObjectMapper();

    private boolean isMovingLeft = false;
    private boolean isMovingRight = false;
    private boolean isMovingUp = false;
    private boolean isMovingDown = false;

    public ClientGui(ClientAplication clientAplication, GameEngine gameEngine) {
        this.clientAplication = clientAplication;
        JFrame frame = new JFrame("Client");
        gameEngine.setWalls(createMapWalls());
        map = new Map(gameEngine);

        frame.add(map);
        frame.setSize(625, 625);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.requestFocus();
        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_A) {
                    isMovingLeft = true;
                }
                if (key == KeyEvent.VK_D) {
                    isMovingRight = true;
                }
                if (key == KeyEvent.VK_W) {
                    isMovingUp = true;
                }
                if (key == KeyEvent.VK_S) {
                    isMovingDown = true;
                }
                movePlayer();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_A) {
                    isMovingLeft = false;
                }
                if (key == KeyEvent.VK_D) {
                    isMovingRight = false;
                }
                if (key == KeyEvent.VK_W) {
                    isMovingUp = false;
                }
                if (key == KeyEvent.VK_S) {
                    isMovingDown = false;
                }
                movePlayer();
            }

            @Override
            public void keyTyped(KeyEvent e) {
            }
        });

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                clientAplication.exit();
                frame.dispose();
            }
        });
    }

    private void movePlayer() {
        if (player == null) {
            player = clientAplication.getPlayer();
            return;
        }
        int deltaX = 0;
        int deltaY = 0;
        if (isMovingLeft) {
            deltaX--;
        }
        if (isMovingRight) {
            deltaX++;
        }

        if (isMovingUp) {
            deltaY--;
        }
        if (isMovingDown) {
            deltaY++;
        }
        player.move(player.getX() + deltaX, player.getY() + deltaY);
    }
    private Vector<Wall> createMapWalls() {
        Vector<Wall> walls = new Vector<>();
        //b
        walls.add(new Wall(50, 0, 50, 35));
        walls.add(new Wall(50, 45, 50, 60));
        walls.add(new Wall(0, 60, 15, 60));
        walls.add(new Wall(35, 60, 50, 60));
        //temka
        walls.add(new Wall(0, 90, 35, 90));
        walls.add(new Wall(48, 90, 55, 90));
        //mid
        walls.add(new Wall(90, 0, 90, 15));
        walls.add(new Wall(90, 25, 90, 60));
        walls.add(new Wall(50, 60, 60, 60));
        walls.add(new Wall(80, 60, 90, 60));
        //second mid
        walls.add(new Wall(55, 60, 55, 65));
        walls.add(new Wall(55, 75, 55, 130));
        walls.add(new Wall(55,130, 80, 130));
        //column
        walls.add(new Wall(65, 118, 73, 118));
        walls.add(new Wall(73, 118, 73, 110));
        walls.add(new Wall(73, 110, 65, 110));
        walls.add(new Wall(65, 110, 65, 118));
        //ct spawn
        walls.add(new Wall(120, 0, 120, 25));
        walls.add(new Wall(90, 40, 120, 40));
        //long
        walls.add(new Wall(120, 55, 120, 130));
        walls.add(new Wall(120, 130, 105, 130));
        walls.add(new Wall(105, 130, 105, 136));
        walls.add(new Wall(105, 146, 105, 166));
        //short
        walls.add(new Wall(85, 60, 85, 100));
        walls.add(new Wall(120, 70, 105, 70));
        walls.add(new Wall(105, 70, 105, 85));
        walls.add(new Wall(105, 85, 120, 85));




        return walls;
    }
    public void setPlayer(Player player) {
        this.player = player;
    }
}
