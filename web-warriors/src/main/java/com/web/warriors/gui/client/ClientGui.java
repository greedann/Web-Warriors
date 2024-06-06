package com.web.warriors.gui.client;

import javax.swing.JFrame;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.warriors.game.ClientApplication;
import com.web.warriors.game.GameEngine;
import com.web.warriors.game.objects.Hostage;
import com.web.warriors.game.objects.Message;
import com.web.warriors.game.objects.Player;
import com.web.warriors.game.objects.Team;
import com.web.warriors.gui.common.Map;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.*;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClientGui {
    private final int MOVES_PER_SECONDS = 20;
    private final int MILLISECONDS_PER_TICK = 1000 / MOVES_PER_SECONDS;
    private ClientApplication clientApplication;
    private Map map;
    private Player player = null;
    ObjectMapper mapper = new ObjectMapper();
    String currentDirectory = System.getProperty("user.dir") + "/web-warriors/src/main/resources";

    private boolean isMovingLeft = false;
    private boolean isMovingRight = false;
    private boolean isMovingUp = false;
    private boolean isMovingDown = false;
    private Timer respawnTimer;
    Vector<Vector<Integer>> PointsNeighbors;
    List<List<Integer>> graph;
    Vector<Point> points;

    private Integer nextPoint;
    private Integer aimPoint;
    private Integer currentPoint;
    private boolean seeOpponent = false;

    private int aimHostage = 1;
    private Timer autoMoveTimer;
    private double angle;

    public ClientGui(ClientApplication clientApplication, GameEngine gameEngine) {
        this.clientApplication = clientApplication;
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
        respawnTimer = new Timer();
        autoMoveTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                makeMovePlayer();
                // autoMovePlayer();
            }
        }, 1000, MILLISECONDS_PER_TICK);
        
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
                clientApplication.exit();
                frame.dispose();
            }
        });
    }

    private void logicMovement() {
        if (player == null || player.getTeam() == null) {
            return;
        }
        Random random = new Random();
        if (player.getTeam() == Team.CounterTerrorists) {

            Hostage hostage = getHostage();

            if(hostage != null){
                aimPoint = 15;
                sendToTeam(new Message("hostage_aim", String.valueOf(aimPoint),"-" ));
                System.out.println("Take hostage");
                return;
            }

            if ((player.getX() == points.get(1).x && player.getY() == points.get(1).y)
                    || (player.getX() == points.get(4).x && player.getY() == points.get(4).y) && hostage == null) {
                if (player.getX() == points.get(1).x) {
                    aimPoint = 4;
                } else {
                    aimPoint = 1;
                }
                sendToTeam(new Message("hostage_aim", String.valueOf(aimPoint),"-" ));
            } else if (hostage == null) {
                // make random between 2 numbers (1 or 4)
                aimPoint = random.nextInt(2) == 0 ? 1 : 4;

            } else if (hostage != null) {
                aimPoint = 15;
                sendToTeam(new Message("hostage_aim", String.valueOf(aimPoint),"-" ));
            } else { // TODO fix unreached code
                aimPoint = random.nextInt(15) + 1;
            }
        } else if (player.getTeam() == Team.Terrorists) {
            // Collection<Hostage> hostages = getHostages();
            // Collection<Hostage> hostages2 = clientApplication.getHostagesFromGE();
            // if (hostages.size() == 0 && hostages2.size() != 0) {
            if (!checkIfHostagePicked()) {
                if (player.getX() != points.get(1).x && player.getY() != points.get(1).y) {
                    aimPoint = 1;
                } else if (player.getX() != points.get(4).x && player.getY() != points.get(4).y) {
                    aimPoint = 4;
                } else {
                    aimPoint = random.nextInt(2) == 0 ? 1 : 4;

                }

            } else {
                aimPoint = 15;
            }
            // if (randomHostage.isTaken()) {
            // aimPoint = 15;
            // } else if (player.getX() != points.get(1).x && player.getY() !=
            // points.get(1).y
            // && randomHostage.getY() == 30) {
            // aimPoint = 1;
            // } else if (player.getX() != points.get(4).x && player.getY() !=
            // points.get(4).y
            // && randomHostage.getY() == 10)
            // aimPoint = 4;
            // } else {
            // aimPoint = random.nextInt(15) + 1;
            // }
        }
    }

    private void sendToTeam(Message message) {
        for (Player player : clientApplication.getPlayers() ){
            if (player.getTeam() == this.player.getTeam() && player.getId() != this.player.getId()){
                message.addReceive(player.getId());
            }
        }
        clientApplication.notifyTeam(message);
    }

    private void makeMovePlayer() {
        if (clientApplication.isPaused()) {
            return;
        }
        if (player == null || player.getTeam() == null) {
            player = clientApplication.getPlayer();
            return;
        }

        if (ifSeeOpponent()) {
            player.move(player.getX(), player.getY() , angle);
            //System.out.println("See opponent");
            if(seeOpponent == false){
                if(nextPoint != null && getDistance(player.getX(), player.getY(), points.get(nextPoint).x, points.get(nextPoint).y) < getDistance(player.getX(), player.getY(), points.get(currentPoint).x, points.get(currentPoint).y)){
                    sendToTeam(new Message("help", nextPoint.toString(),"-" ));
                    System.out.println("Need help on " + nextPoint);
                }
                else{
                    sendToTeam(new Message("help", currentPoint.toString(),"-" ));
                    System.out.println("Need help on " + currentPoint);
                }

                seeOpponent = true;
            }

            // printMyPosition();
            // printPositionOthers();
            if (!actionOnSeenOpponent()) {
                //System.out.println("Stay");
                return;
            }
            //System.out.println("Go");
        } else {
            seeOpponent = false;
            //System.out.println("Doesn't need help");
        }
        if (aimPoint == null) {
            logicMovement();
        }
        if (nextPoint == null) {
            nextPoint = findShortestWay(graph, currentPoint, aimPoint, PointsNeighbors.size());
            player.setNextPoint(nextPoint);
        }
        Point currentPosition = new Point(player.getX(), player.getY());
        if (currentPosition.equals(points.get(aimPoint))) {
            currentPoint = aimPoint;
            player.setCurrentPoint(currentPoint);
            nextPoint = null;

            aimPoint = null;
            return;
        }
        if(nextPoint == -1){
            return;
        }
        if (currentPosition.equals(points.get(nextPoint))) {
            currentPoint = nextPoint;
            player.setCurrentPoint(currentPoint);
            nextPoint = null;
            printPositionOthers();
            return;
        }

        int deltaX = 0, deltaY = 0;
        deltaX = points.get(nextPoint).x - currentPosition.x;
        deltaY = points.get(nextPoint).y - currentPosition.y;
        deltaX = deltaX == 0 ? 0 : deltaX / Math.abs(deltaX);
        deltaY = deltaY == 0 ? 0 : deltaY / Math.abs(deltaY);
        if (ifSeeOpponent()) {

        }
        else {
            player.move(player.getX() + deltaX, player.getY() + deltaY);
        }
    }

    static void bfs(List<List<Integer>> graph, int S,
            List<Integer> par, List<Integer> dist) {
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
                if (dist.get(neighbor) == Integer.MAX_VALUE) {
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

    static Integer findShortestWay(List<List<Integer>> graph, int S,
            int D, int V) {
        // par[] array stores the parent of nodes
        List<Integer> par = new ArrayList<>(Collections.nCopies(V, -1));

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

        if (path.size() < 2)
            return 15;

        return path.get(path.size() - 2);
    }

    private void setPoints() {
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
        List<List<Integer>> edges = new ArrayList<>();
        for (int i = 0; i < PointsNeighbors.size(); i++) {
            List<Integer> neighbors = PointsNeighbors.get(i);
            for (int j = 0; j < neighbors.size(); j++) {
                edges.add(Arrays.asList(i, neighbors.get(j)));
            }
        }
        int V = PointsNeighbors.size();
        List<List<Integer>> graph = new ArrayList<>(V);
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
        return clientApplication.getHostage();
    }

    public Collection<Hostage> getHostages() {

        Vector<Hostage> hostages = new Vector<>();
        for (Player p : this.clientApplication.getPlayers()) {
            if (p.getHostage() != null) {
                hostages.add(p.getHostage());
            }
        }
        return hostages;

    }

    public void setPlayer(Player player) {
        this.player = player;
        map.setUserPlayer(player);
        switch (player.getTeam()) {
            case CounterTerrorists:
                currentPoint = 15;
                aimPoint = 1;
                break;
            case Terrorists:
                currentPoint = 3;
                break;
            default:
                break;
        }
    }

    public Boolean ifSeeOpponent() {
        Collection<Player> allPlayers = clientApplication.getPlayers();
        for (Player player1 : allPlayers) {
            if (player.getTeam() != player1.getTeam()) {
                if (player1.getY() != -5) {
                    angle = Math.atan2(player.getX() - player1.getX(), player.getY() - player1.getY()) + (Math.PI / 2);
                    return true;
                }
            }
        }
        return false;
    }

    public void printPositionOthers() {
        Collection<Player> allPlayers = clientApplication.getPlayers();
        for (Player player1 : allPlayers) {
            if (!player1.equals(player)) {
                //System.out.println(player1.getX() + " " + player1.getY());
            }
        }
    }

    public Boolean actionOnSeenOpponent() {
        // generate random number
        Random random = new Random();
        int chanceToBeKilled = random.nextInt(1000);
        if (chanceToBeKilled > 990) {
            player.setPosition(-5, -5, 0);
            player.move(-5, -5);
            nextPoint = null;
            scheduleRespawn();
            System.out.println("Killed");
            return false;
        }
        if (player.getTeam() == Team.CounterTerrorists) {
            if (player.getHostage() == null) {
                return true;
            } else {
                return false;
            }
        } else {
            if (checkIfHostagePicked()) {
                return true;
            } else {
                return false;
            }
        }
    }

    public void printMyPosition() {
        System.out.println("MY: " + player.getX() + " " + player.getY());
    }

    public Boolean checkIfHostagePicked() {
        Collection<Hostage> hostages = getHostages();
        Collection<Hostage> hostages2 = clientApplication.getHostagesFromGE();
        if (hostages.size() == 0 && hostages2.size() != 0) {
            return false;
        }
        return true;
    }

    public void scheduleRespawn() {
        respawnTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                respawn();
            }
        }, 5000);
    }

    public void respawn() {
        // add time check 5 seconds
        Random random = new Random();
        if (player.getTeam() == Team.CounterTerrorists) {

            currentPoint = 17+random.nextInt(3);
            player.setNextPoint(currentPoint);
            System.out.println("Respawned on " +(int) points.get(currentPoint).getX() + " " + (int) points.get(currentPoint).getY());
            player.move((int) points.get(currentPoint).getX(), (int) points.get(currentPoint).getY());

            nextPoint = null;
            aimPoint = null;
            sendToTeam(new Message("whereWeGo", null,"-" ));
        } else {
            player.move(67, 39);
            currentPoint = 20+random.nextInt(3);

            player.setNextPoint(currentPoint);
            System.out.println("Respawned on " +(int) points.get(currentPoint).getX() + " " + (int) points.get(currentPoint).getY());
            player.move((int) points.get(currentPoint).getX(), (int) points.get(currentPoint).getY());
            nextPoint = null;
            aimPoint = null;
        }
    }



    public void processTeamMessage(Message message) {
        System.out.println("Message from team: " + message.getMessage());
        if (message.getType().equals("help")) {
            aimPoint = Integer.parseInt(message.getMessage());

            System.out.println("Go to help on" + points.get(aimPoint).x + " " + points.get(aimPoint).y + " from " + player.getX() + " " + player.getY());
        }
        if(message.getType().equals("hostage_aim")){
            aimPoint = Integer.parseInt(message.getMessage());

            System.out.println("Go to aim point on" + points.get(aimPoint).x + " " + points.get(aimPoint).y + " from " + player.getX() + " " + player.getY());
        }
        if(message.getType().equals("whereWeGo")){
            sendToTeam(new Message("hostage_aim", aimPoint.toString(),"-" ));
        }
    }


    public int getDistance(int x1, int y1, int x2, int y2){
        return (int) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }
}
