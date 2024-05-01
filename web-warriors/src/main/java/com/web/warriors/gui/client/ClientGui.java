package com.web.warriors.gui.client;

import javax.swing.JFrame;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.warriors.game.ClientAplication;
import com.web.warriors.game.GameEngine;
import com.web.warriors.game.objects.Hostage;
import com.web.warriors.game.objects.Player;
import com.web.warriors.game.objects.Team;
import com.web.warriors.gui.common.Map;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClientGui {
    private final int MOVES_PER_SECONDS = 40;
    private final int MILLISECONDS_PER_TICK = 1000 / MOVES_PER_SECONDS;
    private ClientAplication clientAplication;
    private Map map;
    private Player player = null;
    ObjectMapper mapper = new ObjectMapper();

    private boolean isMovingLeft = false;
    private boolean isMovingRight = false;
    private boolean isMovingUp = false;
    private boolean isMovingDown = false;
    Vector<Vector<Integer>> PointsNeighbors;
    List<List<Integer> > graph;
    Vector<Point> points;

    private Integer nextPoint;
    private Integer aimPoint;
    private Integer currentPoint;
    private Timer autoMoveTimer;

    public ClientGui(ClientAplication clientAplication, GameEngine gameEngine) {
        this.clientAplication = clientAplication;
        JFrame frame = new JFrame("Client");
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
                makeMovePlayer();
                //autoMovePlayer();
            }
        }, 0, MILLISECONDS_PER_TICK);

        // real player
        // frame.addKeyListener(new KeyListener() {
        // @Override
        // public void keyPressed(KeyEvent e) {
        // int key = e.getKeyCode();
        // if (key == KeyEvent.VK_A) {
        // isMovingLeft = true;
        // }
        // if (key == KeyEvent.VK_D) {
        // isMovingRight = true;
        // }
        // if (key == KeyEvent.VK_W) {
        // isMovingUp = true;
        // }
        // if (key == KeyEvent.VK_S) {
        // isMovingDown = true;
        // }
        // movePlayer();
        // }
        //
        // @Override
        // public void keyReleased(KeyEvent e) {
        // int key = e.getKeyCode();
        // if (key == KeyEvent.VK_A) {
        // isMovingLeft = false;
        // }
        // if (key == KeyEvent.VK_D) {
        // isMovingRight = false;
        // }
        // if (key == KeyEvent.VK_W) {
        // isMovingUp = false;
        // }
        // if (key == KeyEvent.VK_S) {
        // isMovingDown = false;
        // }
        // movePlayer();
        // }
        //
        // @Override
        // public void keyTyped(KeyEvent e) {
        // }
        // });

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                clientAplication.exit();
                frame.dispose();
            }
        });
    }
    private void logicMovement(){
        if (player == null || player.getTeam() == null) {
            return;
        }
        Random random = new Random();
        if (player.getTeam() == Team.CounterTerrorists) {
            Hostage hostage = new Hostage();
            hostage = getHostage();
             if ((player.getX() == points.get(1).x && player.getY() == points.get(1).y) || (player.getX() == points.get(4).x && player.getY() == points.get(4).y) && hostage == null) {
                if (player.getX() == points.get(1).x) {
                    aimPoint = 4;
                } else {
                    aimPoint = 1;
                }
            }
            else if(hostage == null) {
                //make random beween 2 numbers (1 or 4)
                aimPoint = random.nextInt(2) == 0 ? 1 : 4;
            }
            else if(hostage != null){
                aimPoint = 15;
            }
            else {
                aimPoint = random.nextInt(15) + 1;
            }
        }
        else if (player.getTeam() == Team.Terrorists){
            Vector<Hostage> hostages = clientAplication.getHostages();
            Hostage randomHostage = hostages.get(random.nextInt(hostages.size()));
            if (randomHostage.getWhoTakes() != null){
                aimPoint = 15;
            }
            else if(player.getX() != points.get(1).x && player.getY() != points.get(1).y && randomHostage.getY() == 30){
                aimPoint = 1;
            }
            else if(player.getX() != points.get(4).x && player.getY() != points.get(4).y && randomHostage.getY() == 10)
                aimPoint = 4;
            }
            else {
              aimPoint = random.nextInt(15) + 1;
        }
    }
    private void makeMovePlayer(){
        if (player == null || player.getTeam() == null) {
            player = clientAplication.getPlayer();
            return;
        }


        if (aimPoint == null){
            logicMovement();
        }
        if (nextPoint == null){
            nextPoint = findShortestWay(graph, currentPoint, aimPoint, PointsNeighbors.size());
        }
        Point currentPosition = new Point(player.getX(), player.getY());
        if(currentPosition.equals(points.get(aimPoint))){
            currentPoint = aimPoint;
            nextPoint = null;
            aimPoint = null;
            return;
        }
        if (currentPosition.equals(points.get(nextPoint))){
            currentPoint = nextPoint;
            nextPoint = null;
            return;
        }

        int deltaX = 0, deltaY = 0;
        deltaX = points.get(nextPoint).x - currentPosition.x;
        deltaY = points.get(nextPoint).y - currentPosition.y;
        deltaX = deltaX == 0 ? 0 : deltaX / Math.abs(deltaX);
        deltaY = deltaY == 0 ? 0 : deltaY / Math.abs(deltaY);
        player.move(player.getX() + deltaX, player.getY() + deltaY);

    }

    private void autoMovePlayer() {
        if (player == null || player.getTeam() == null) {
            // player = clientAplication.getPlayer();
            return;
        }
        Random random = new Random();
        Point currentPoint = new Point(player.getX(), player.getY());

        if (currentPoint.equals(points.get(nextPoint))) {
            int temp = random.nextInt(PointsNeighbors.get(nextPoint).size());
            nextPoint = PointsNeighbors.get(nextPoint).get(temp);
            player.setNextPoint(nextPoint);
        }
        int deltaX = 0, deltaY = 0;

        deltaX = points.get(nextPoint).x - currentPoint.x;
        deltaY = points.get(nextPoint).y - currentPoint.y;
        deltaX = deltaX == 0 ? 0 : deltaX / Math.abs(deltaX);
        deltaY = deltaY == 0 ? 0 : deltaY / Math.abs(deltaY);

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
    static void bfs(List<List<Integer> > graph, int S,
                    List<Integer> par, List<Integer> dist)
    {
        // Queue to store the nodes in the order they are
        // visited
        Queue<Integer> q = new LinkedList<>();
        // Mark the distance of the source node as 0
        dist.set(S, 0);
        // Push the source node to the queue
        q.add(S);

        // Iterate until the queue is not empty
        while (!q.isEmpty()) {
            // Pop the node at the front of the queue
            int node = q.poll();

            // Explore all the neighbors of the current node
            for (int neighbor : graph.get(node)) {
                // Check if the neighboring node is not
                // visited
                if (dist.get(neighbor)
                        == Integer.MAX_VALUE) {
                    // Mark the current node as the parent
                    // of the neighboring node
                    par.set(neighbor, node);
                    // Mark the distance of the neighboring
                    // node as the distance of the current
                    // node + 1
                    dist.set(neighbor, dist.get(node) + 1);
                    // Insert the neighboring node to the
                    // queue
                    q.add(neighbor);
                }
            }
        }
    }
    static Integer
    findShortestWay(List<List<Integer> > graph, int S,
                          int D, int V)
    {
        // par[] array stores the parent of nodes
        List<Integer> par
                = new ArrayList<>(Collections.nCopies(V, -1));

        // dist[] array stores the distance of nodes from S
        List<Integer> dist = new ArrayList<>(
                Collections.nCopies(V, Integer.MAX_VALUE));

        // Function call to find the distance of all nodes
        // and their parent nodes
        bfs(graph, S, par, dist);

        if (dist.get(D) == Integer.MAX_VALUE) {
            System.out.println(
                    "Source and Destination are not connected");
            return -1;
        }

        // List path stores the shortest path
        List<Integer> path = new ArrayList<>();
        int currentNode = D;
        path.add(D);
        while (par.get(currentNode) != -1) {
            path.add(par.get(currentNode));
            currentNode = par.get(currentNode);
        }

        // Printing path from source to destination
        System.out.println("start\n");
        for (int i = path.size() - 1; i >= 0; i--)
            System.out.print(i + " " + path.get(i) + " ");
        System.out.println('\n');
        System.out.println("returned" + path.get(path.size()-2));
        return path.get(path.size()-2);
    }
    private void setPoints() {
        String currentDirectory = System.getProperty("user.dir") + "/src/main/resources";
        Vector<Vector<Integer>> PointsNeighbors = new Vector<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(currentDirectory + "/PointsNeighbors.txt"))) {
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
        List<List<Integer> > edges = new ArrayList<>();
        for (int i = 0; i < PointsNeighbors.size(); i++) {
                    List<Integer> neighbors = PointsNeighbors.get(i);
                    for (int j = 0; j < neighbors.size(); j++) {
                        edges.add(Arrays.asList(i, neighbors.get(j)));
                    }
                }
        int V = PointsNeighbors.size();
        List<List<Integer> > graph = new ArrayList<>(V);
        for (int i = 0; i < V; i++) {
            graph.add(new ArrayList<>());
        }
        for (List<Integer> edge : edges) {
            graph.get(edge.get(0)).add(edge.get(1));
           // graph.get(edge.get(1)).add(edge.get(0));
        }
        this.graph = graph;

        points = new Vector<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(currentDirectory + "/Points.txt"))) {
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
    public Hostage getHostage() {
        Hostage loh = clientAplication.getPlayer().getHostage();
       return clientAplication.getHostage();
    }


    public void setPlayer(Player player) {
        this.player = player;
        map.setUserPlayer(player);
        switch (player.getTeam()) {
            case CounterTerrorists:
                //nextPoint = 15;
                currentPoint = 15;
                break;
            case Terrorists:

                currentPoint = 3;
                break;
            default:
                break;
        }
    }
}

