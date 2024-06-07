package com.web.warriors.game.objects;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Bullet {
    private float x;
    private float y;
    private double angle;
    private int speed = 2;
    private int id;
    private Team team;

    public Bullet(int x, int y, double angle, Team team) {
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.id = (int) (Math.random() * 1000000);
        this.team = team;
    }

    public Bullet() {
        this.x = 0;
        this.y = 0;
        this.angle = 0;
        this.id = (int) (Math.random() * 1000000);
    }

    public void move() {
        x += speed * Math.cos(angle);
        y -= speed * Math.sin(angle);
    }

    public int getX() {
        return (int) x;
    }

    public int getY() {
        return (int) y;
    }

    public double getAngle() {
        return angle;
    }

    public int getId() {
        return id;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getSpeed() {
        return speed;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    @JsonIgnore
    public boolean isOutOfBounds() {
        return x < 0 || x > 150 || y < 0 || y > 150; // TODO: change to constants
    }

    @JsonIgnore
    public boolean isColliding(Player player) {
        return Math.abs(player.getX() - x) < 3 && Math.abs(player.getY() - y) < 3;
    }

    @JsonIgnore
    public boolean isColliding(Hostage hostage) {
        return Math.abs(hostage.getX() - x) < 2 && Math.abs(hostage.getY() - y) < 2;
    }

    private Integer transform(double val) {
        if (val < 0) {
            return -1;
        } else if (val > 0) {
            return 1;
        } else {
            return 0;
        }
    }

    @JsonIgnore
    public boolean isColliding(Wall wall) {
        Integer dx = transform(Math.cos(angle));
        Integer dy = transform(Math.sin(angle));

        boolean result = wall.intersects((int) x, (int) y, (int) (x + dx), (int) (y + dy));
        return result;
    }

    @JsonIgnore
    public boolean isColliding(List<Wall> walls) {
        for (Wall wall : walls) {
            if (isColliding(wall))
                return true;
        }
        return false;
    }

}
