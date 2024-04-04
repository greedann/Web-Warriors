package com.web.warriors.gui.client;

import javax.swing.JFrame;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.warriors.game.ClientAplication;
import com.web.warriors.game.GameEngine;
import com.web.warriors.game.objects.Player;
import com.web.warriors.gui.common.Map;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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

    public void setPlayer(Player player) {
        this.player = player;
    }
}
