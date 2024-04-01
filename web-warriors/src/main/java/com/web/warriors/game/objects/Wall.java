package com.web.warriors.game.objects;

public class Wall {
    private float start_x;
    private float start_y;
    private float end_x;
    private float end_y;

    public Wall(float start_x, float start_y, float end_x, float end_y) {
        this.start_x = start_x;
        this.start_y = start_y;
        this.end_x = end_x;
        this.end_y = end_y;
    }

    public float getStart_x() {
        return start_x;
    }

    public float getStart_y() {
        return start_y;
    }

    public float getEnd_x() {
        return end_x;
    }

    public float getEnd_y() {
        return end_y;
    }

}
