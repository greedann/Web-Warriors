package com.web.warriors.game.objects;

public class Player {
    private String name;
    private int score;
    private int id;
    private int x;
    private int y;

    public Player(String name, int id) {
        this.name = name;
        this.id = id;
        this.score = 0;
        this.x = 75;
        this.y = 75;
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

    @Override
    public String toString() {
        return "Player [name=" + name + ", score=" + score + ", id=" + id + ", x=" + x + ", y=" + y + "]";
    }
}
