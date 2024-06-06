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

    static class Point {
        int x, y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private int orientation(int x1, int y1, int x2, int y2, int x3, int y3) {
        int val = (y2 - y1) * (x3 - x2) - (x2 - x1) * (y3 - y2);
        if (val == 0)
            return 0; // collinear
        return (val > 0) ? 1 : 2; // clock or counterclock wise
    }

    // method to check if the point is on the segment
    private boolean onSegment(int x1, int y1, int x2, int y2, int x3, int y3) {
        if (x2 <= Math.max(x1, x3) && x2 >= Math.min(x1, x3) &&
                y2 <= Math.max(y1, y3) && y2 >= Math.min(y1, y3)) {
            return true;
        }
        return false;
    }

    // method to check if the two lines intersect
    public boolean intersects(int x1, int y1, int x2, int y2) {
        Point p1 = new Point(start_x, start_y);
        Point q1 = new Point(end_x, end_y);
        Point p2 = new Point(x1, y1);
        Point q2 = new Point(x2, y2);

        // Find the 4 orientations required for the general and special cases
        int o1 = orientation(p1.x, p1.y, q1.x, q1.y, p2.x, p2.y);
        int o2 = orientation(p1.x, p1.y, q1.x, q1.y, q2.x, q2.y);
        int o3 = orientation(p2.x, p2.y, q2.x, q2.y, p1.x, p1.y);
        int o4 = orientation(p2.x, p2.y, q2.x, q2.y, q1.x, q1.y);

        // General case
        if (o1 != o2 && o3 != o4)
            return true;

        // Special Cases
        // p1, q1 и p2 colinear и p2 lays on segment p1q1
        if (o1 == 0 && onSegment(p1.x, p1.y, p2.x, p2.y, q1.x, q1.y))
            return true;

        // p1, q1 и q2 colinear и q2 lays on segment p1q1
        if (o2 == 0 && onSegment(p1.x, p1.y, q2.x, q2.y, q1.x, q1.y))
            return true;

        // p2, q2 и p1 colinear и p1 lays on segment p2q2
        if (o3 == 0 && onSegment(p2.x, p2.y, p1.x, p1.y, q2.x, q2.y))
            return true;

        // p2, q2 и q1 colinear и q1 lays on segment p2q2
        if (o4 == 0 && onSegment(p2.x, p2.y, q1.x, q1.y, q2.x, q2.y))
            return true;

        // Doesn't fall in any of the above cases
        return false;
    }
}
