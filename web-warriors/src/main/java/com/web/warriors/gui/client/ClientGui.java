package com.web.warriors.gui.client;

import javax.swing.JFrame;

import com.web.warriors.game.GameEngine;
import com.web.warriors.game.objects.Player;
import com.web.warriors.gui.common.Map;
import com.web.warriors.web.client.Client;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class ClientGui {
    private Client client;
    private GameEngine gameEngine;
    private Map map;
    private Player player;
    private boolean isMovingLeft = false;
    private boolean isMovingRight = false;
    private boolean isMovingUp = false;
    private boolean isMovingDown = false;



    public ClientGui(Client client, GameEngine gameEngine) {
        this.client = client;
        this.gameEngine = gameEngine;
//        player = gameEngine.getPlayer(client.getId());
        JFrame frame = new JFrame("Client");// Another example player
        map = new Map(gameEngine);

        frame.add(map);
        frame.setSize(625, 625);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
    }


        private void movePlayer() {
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
        player = gameEngine.getPlayer(client.getId());
        player.setPosition(player.getX() + deltaX, player.getY() + deltaY);
    }


    public void setPlayer(Player player) {
        this.player = player;
    }

}
