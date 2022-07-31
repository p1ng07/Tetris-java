package com.mygdx.tetris;

import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.graphics.Color;

public class Tetromino {
    private Color color = Color.ORANGE;
    Point blocks[] = new Point[4];

    // Generates a random Tetris Piece
    // The col and line given are the reference square for the piece
    public Tetromino(int col, int line) {
        int randomNum = ThreadLocalRandom.current().nextInt(1, 7 + 1);
        switch (randomNum) {
        case 1:
            // Draw a L shape
            blocks[0] = new Point(-1, -1);
            blocks[1] = new Point(0, -1);
            blocks[2] = new Point(0, 0);
            blocks[3] = new Point(-2, -1);
            this.color = Color.ORANGE;
            break;
        case 2:
            // Draw a J shape
            blocks[0] = new Point(0, -1);
            blocks[1] = new Point(-1, -1);
            blocks[2] = new Point(-1, 0);
            blocks[3] = new Point(1, -1);
            this.color = Color.BLUE;
            break;
        case 3:
            // Draw a I shape
            blocks[0] = new Point(-1, 0);
            blocks[1] = new Point(0, 0);
            blocks[2] = new Point(1, 0);
            blocks[3] = new Point(-2, 0);
            this.color = Color.CYAN;
            break;
        case 4:
            // Draw a O shape
            blocks[0] = new Point(-1, -1);
            blocks[1] = new Point(0, -1);
            blocks[2] = new Point(-1, 0);
            blocks[3] = new Point(0, 0);
            this.color = Color.YELLOW;
            break;
        case 5:
            // Draw a S shape
            blocks[0] = new Point(-1, -1);
            blocks[1] = new Point(-1, 0);
            blocks[2] = new Point(0, 0);
            blocks[3] = new Point(-2, -1);
            this.color = Color.GREEN;
            break;
        case 6:
            // make a T shape
            blocks[0] = new Point(-1, -1);
            blocks[1] = new Point(-1, 0);
            blocks[2] = new Point(-2, -1);
            blocks[3] = new Point(0, -1);
            this.color = Color.PURPLE;
            break;
        default:
            // Draw a z shape
            blocks[0] = new Point(-1, -1);
            blocks[1] = new Point(-1, 0);
            blocks[2] = new Point(-2, 0);
            blocks[3] = new Point(0, -1);
            this.color = Color.RED;
            break;

        }

        // Add the board coordinates to local coordinates
        for (Point position : blocks) {
            position.x += col;
            position.y += line;
        }
    }

    public Color getColor() {
        return this.color;
    }

    // Returns false if tetromino cant move down
    public boolean moveDown(boolean[][] board) {
        boolean canMoveDown = true;
        // Check if we are at the bottom of the board
        for (Point square : this.blocks)
            if (square.y - 1 < 0)
                return false;

        // Check if the squares bellow the current tetromino are occupied
        // If they are filled, then we cant move down
        for (Point square : this.blocks)
            if (board[square.x][square.y - 1])
                canMoveDown = false;

        if (!canMoveDown) {
            return false;
        }
        for (Point square : this.blocks) {
            square.y--;
        }
        return true;
    }

    public void moveRight(boolean[][] board) {
        // Check if we are at the left most square of the board or if the left square of
        // the board is filled
        for (Point square : this.blocks)
            if (square.x + 1 > MyTetrisGame.cols - 1 || board[square.x + 1][square.y])
                return;

        for (Point square : this.blocks) {
            square.x++;
        }
    }

    public void moveLeft(boolean[][] board) {
        // Check if we are at the left most square of the board or if the left square of
        // the board is filled
        for (Point square : this.blocks)
            if (square.x - 1 < 0 || board[square.x - 1][square.y])
                return;

        for (Point square : this.blocks)
            square.x--;
    }

    public void rotate(boolean[][] board, boolean clockwise, Boolean offset) {
        for (Point point : this.blocks) {
            point.rotate(this.blocks[0], clockwise);
        }

    }
}
