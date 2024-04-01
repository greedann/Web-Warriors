package com.web.warriors.game;

import java.awt.*;

public class Player {

        private int x;
        private int y;
        private Color color;

        public Player(int x, int y, Color color) {
            this.x = x;
            this.y = y;
            this.color = color;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public Color getColor() {
            return color;
        }

        public void setPosition(int x, int y) {
            this.x = x;
            this.y = y;
        }


}
