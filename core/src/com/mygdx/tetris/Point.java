package com.mygdx.tetris;

public class Point {
    int x, y = 0;

    public Point() {
        this.x = 0;
        this.y = 0;
    }

    public Point(final int col, final int row) {
        this.x = col;
        this.y = row;
    }

    public void rotate(final Point origin, final boolean clockwise) {
        final Point relativePosition = new Point(this.x - origin.x, this.y - origin.y);
        final Point rotationMatrix[] = new Point[2];
        if (clockwise) {
            rotationMatrix[0] = new Point(0, 1);
            rotationMatrix[1] = new Point(-1, 0);
        } else {
            rotationMatrix[0] = new Point(0, -1);
            rotationMatrix[1] = new Point(1, 0);
        }

        // Dot product of the rotationMatrix . relativePosition
        final int newX = (rotationMatrix[0].x * relativePosition.x) + (rotationMatrix[0].y * relativePosition.y);
        final int newY = (rotationMatrix[1].x * relativePosition.x) + (rotationMatrix[1].y * relativePosition.y);
        this.x = newX + origin.x;
        this.y = newY + origin.y;
    }
}
