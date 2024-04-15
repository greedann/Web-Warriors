package com.web.warriors.game.objects;

import java.io.Serializable;

public class Player implements Serializable {
    private String name;
    private int score;
    private int id;
    private int x;
    private int y;
    private Team team;

    public Player() {
        this.name = "";
        this.id = 0;
        this.score = 0;
        this.x = 75;
        this.y = 75;
        this.team = Team.NONE;
    }

    public Player(String name, int id) {
        this.name = name;
        this.id = id;
        this.score = 0;
        this.x = 75;
        this.y = 75;
        this.team = Team.NONE;
    }

    public Player(String name, int id, Team team) {
        this.name = name;
        this.id = id;
        this.score = 0;
        this.x = 75;
        this.y = 75;
        this.team = team;
    }


    public void move(int x, int y) {
        if (x > 3 && x < 147)
            this.x = x;
        if (y > 3 && y < 147)
            this.y = y;
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

    @Override
    public String toString() {
        return "Player [name=" + name + ", score=" + score + ", id=" + id + ", x=" + x + ", y=" + y + "]";
    }
}
