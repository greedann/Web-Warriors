package com.web.warriors.game.objects;

public class Player {
    private String name;
    private int score;
    private int id;
    private float x;
    private float y;

    public Player(String name, int id) {
        this.name = name;
        this.id = id;
        this.score = 0;
    }

    public void move(float x, float y) {
        this.x = x;
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

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    @Override
    public String toString() {
        return "Player [name=" + name + ", score=" + score + ", id=" + id + ", x=" + x + ", y=" + y + "]";
    }
}
