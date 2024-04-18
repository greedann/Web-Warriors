package com.web.warriors.game.objects;

public class Wall {
    private int start_x;
    private int start_y;
    private int end_x;
    private int end_y;

    public Wall(int start_x, int start_y, int end_x, int end_y) {
        this.start_x = start_x;
        this.start_y = start_y;
        this.end_x = end_x;
        this.end_y = end_y;
    }

    public int getStart_x() {
        return start_x;
    }

    public int getStart_y() {
        return start_y;
    }

    public int getEnd_x() {
        return end_x;
    }

    public int getEnd_y() {
        return end_y;
    }

    private double ccw(int x1, int y1, int x2, int y2, int x3, int y3) {
        return (x2 - x1) * (y3 - y1) - (y2 - y1) * (x3 - x1);
    }

    public boolean intersects(int x1, int y1, int x2, int y2) {
        return ccw(start_x, start_y, end_x, end_y, x1, y1) * ccw(start_x, start_y, end_x, end_y, x2, y2) <= 0 &&
                ccw(x1, y1, x2, y2, start_x, start_y) * ccw(x1, y1, x2, y2, end_x, end_y) <= 0;
    }

}
