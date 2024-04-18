package com.web.warriors.gui.client;

import javax.swing.JFrame;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.warriors.game.ClientAplication;
import com.web.warriors.game.GameEngine;
import com.web.warriors.game.objects.Player;
import com.web.warriors.game.objects.Wall;
import com.web.warriors.gui.common.Map;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClientGui {
    private final int FPS  = 10;
    private final int MILLISECONDS_PER_TICK = 1000 / FPS;
    private ClientAplication clientAplication;
    private Map map;
    private Player player = null;
    ObjectMapper mapper = new ObjectMapper();

    private boolean isMovingLeft = false;
    private boolean isMovingRight = false;
    private boolean isMovingUp = false;
    private boolean isMovingDown = false;
    Vector<Vector<Integer>> PointsNeighbors;
    Vector<Point> points;


    private Integer AimPoint;
    private Timer autoMoveTimer;

    public ClientGui(ClientAplication clientAplication, GameEngine gameEngine) {
        this.clientAplication = clientAplication;
        JFrame frame = new JFrame("Client");
        gameEngine.setWalls(createMapWalls());
        map = new Map(gameEngine);
        setPoints();
        frame.add(map);
        frame.setSize(625, 625);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.requestFocus();
        autoMoveTimer = new Timer();
        autoMoveTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                autoMovePlayer();
            }
        }, 0, MILLISECONDS_PER_TICK);

        //real player
//        frame.addKeyListener(new KeyListener() {
//            @Override
//            public void keyPressed(KeyEvent e) {
//                int key = e.getKeyCode();
//                if (key == KeyEvent.VK_A) {
//                    isMovingLeft = true;
//                }
//                if (key == KeyEvent.VK_D) {
//                    isMovingRight = true;
//                }
//                if (key == KeyEvent.VK_W) {
//                    isMovingUp = true;
//                }
//                if (key == KeyEvent.VK_S) {
//                    isMovingDown = true;
//                }
//                movePlayer();
//            }
//
//            @Override
//            public void keyReleased(KeyEvent e) {
//                int key = e.getKeyCode();
//                if (key == KeyEvent.VK_A) {
//                    isMovingLeft = false;
//                }
//                if (key == KeyEvent.VK_D) {
//                    isMovingRight = false;
//                }
//                if (key == KeyEvent.VK_W) {
//                    isMovingUp = false;
//                }
//                if (key == KeyEvent.VK_S) {
//                    isMovingDown = false;
//                }
//                movePlayer();
//            }
//
//            @Override
//            public void keyTyped(KeyEvent e) {
//            }
//        });

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                clientAplication.exit();
                frame.dispose();
            }
        });
    }

    private void autoMovePlayer(){
        if(player == null){
            player = clientAplication.getPlayer()
                    ;
            return;



        }
        Random random = new Random();
        Point currentPoint = new Point(player.getX(), player.getY());

        if(currentPoint.equals(points.get(AimPoint))){
            int temp = random.nextInt(PointsNeighbors.get(AimPoint).size());
            AimPoint = PointsNeighbors.get(AimPoint).get(temp);
        }
        int deltaX = 0, deltaY = 0;

        if(points.get(AimPoint).x > currentPoint.x){
           deltaX = 1;}
        if(points.get(AimPoint).x < currentPoint.x){
            deltaX = -1;}
        if(points.get(AimPoint).y > currentPoint.y){
            deltaY = 1;}
        if(points.get(AimPoint).y < currentPoint.y) {
            deltaY = -1;
        }
        player.move(player.getX() + deltaX, player.getY() + deltaY);
    }
    private void movePlayer() {
        if (player == null) {
            player = clientAplication.getPlayer();

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
    private void setPoints(){
        String currentDirectory = System.getProperty("user.dir") +"/web-warriors/src/main/resources";
        Vector<Vector<Integer>> PointsNeighbors = new Vector<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(currentDirectory+"/PointsNeighbors.txt"))){
            String line;
            while ((line = reader.readLine()) != null) {
                List<Integer> neighbors = Stream.of(line.split(","))
                        .map(Integer::parseInt)
                        .collect(Collectors.toList());
                PointsNeighbors.add(new Vector<>(neighbors));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.PointsNeighbors = PointsNeighbors;
        points = new Vector<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(currentDirectory+"/Points.txt"))){
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int x = Integer.parseInt(parts[0]);
                int y = Integer.parseInt(parts[1]);
                points.add(new Point(x, y));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private Vector<Wall> createMapWalls() {
        Vector<Wall> walls = new Vector<>();
        String currentDirectory = System.getProperty("user.dir") +"/web-warriors/src/main/resources";
        try (BufferedReader reader = new BufferedReader(new FileReader(currentDirectory+"/walls.txt"))){
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int startX = Integer.parseInt(parts[0]);
                int startY = Integer.parseInt(parts[1]);
                int endX = Integer.parseInt(parts[2]);
                int endY = Integer.parseInt(parts[3]);
                walls.add(new Wall(startX, startY, endX, endY));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return walls;
    }
    public void setPlayer(Player player) {
        this.player = player;

        int randPoint = new Random().nextInt(15)+1;
        AimPoint = randPoint;
        player.move(points.get(randPoint).x, points.get(randPoint).y);

    }
}
