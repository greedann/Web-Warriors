package com.web.warriors.game.objects;

import java.io.Serializable;

public class Player implements Serializable {
    private String name;
    private int score;
    private int id;
    private int x;
    private int y;
    private Team team;
    private double angle;
    private Hostage hostage = null;

    private Integer nextPoint = null;

    public Player(Player other) {
        name = other.name;
        score = other.score;
        id = other.id;
        x = other.x;
        y = other.y;
        team = other.team;
        nextPoint = other.nextPoint;
    }

    public Player() {
        this.name = "";
        this.id = 0;
        this.score = 0;
        this.x = 75;
        this.y = 75;
        this.team = Team.NONE;
        this.angle = 0;
        this.nextPoint = 1;
    }

    public Player(String name, int id) {
        this.name = name;
        this.id = id;
        this.score = 0;
        this.x = 75;
        this.y = 75;
        this.team = Team.NONE;
        this.angle = 0;
        this.nextPoint = 1;
    }

    public Player(String name, int id, Team team) {
        this.name = name;
        this.id = id;
        this.score = 0;
        this.team = team;
        this.angle = 0;
        this.nextPoint = 1;
        switch (team) {
            case CounterTerrorists:
                x = 95;
                y = 137;
                break;
            case Terrorists:
                x = 67;
                y = 39;
            default:
                break;
        }
    }

    public int getNextPoint() {
        return nextPoint;
    }

    public void setNextPoint(int nextPoint) {
        this.nextPoint = nextPoint;
    }

    public void move(int x, int y) {
        if (x == -5 && y == -5) {
            hide();
            return;
        }
        if (x > 3 && x < 147 && y > 3 && y < 147) {
            this.angle = Math.atan2(this.y - y, x - this.x);
            this.x = x;
            this.y = y;
        }
    }

    public boolean takeHostage(Hostage hostage) {
        if (team == Team.Terrorists)
            return false;
        if (this.hostage != null)
            return false;
        this.hostage = hostage;
        hostage.take();
        return true;
    }

    public Hostage getHostage() {
        return hostage;
    }

    public void setHostage(Hostage hostage) {
        this.hostage = hostage;
    }

    public void setPosition(int x, int y, double angle) {
        this.x = x;
        this.y = y;
        this.angle = angle;
    }

    public void addScore(int score) {
        this.score += score;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public int getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public void hide() {
        this.x = -5;
        this.y = -5;
    }

    public double getAngle() {
        return angle;
    }

    public void leaveHostage() {
        this.hostage = null;
        this.score += 50;
    }

    @Override
    public String toString() {
        return "Player [name=" + name + ", score=" + score + ", id=" + id + ", x=" + x + ", y=" + y + "]";
    }
}
