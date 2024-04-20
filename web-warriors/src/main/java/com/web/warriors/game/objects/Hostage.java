package com.web.warriors.game.objects;

import java.io.Serializable;

public class Hostage implements Serializable {
    private int id;
    private int x;
    private int y;

    public Hostage() {
        id = -1;
        x = -5;
        y = -5;
    }

    public Hostage(int id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
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

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
