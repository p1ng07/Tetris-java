package com.mygdx.tetris;

import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.graphics.Color;

/**
 * Rep
 */
public class Tetromino extends Object {
    private Color color = Color.ORANGE;
    Square blocks[] = new Square[4];

    // Generates a random Tetris Piece
    // The col and line given are the top left most corner of the piece
    public Tetromino(int col, int line) {
        // In order: L, J, I, O, S, T, Z
        int randomNum = ThreadLocalRandom.current().nextInt(1, 7 + 1);
        switch (randomNum) {
        // Draw a L shape
        case 1:
            blocks[0] = new Square(-1, 0);
            blocks[1] = new Square(-1, -1);
            blocks[2] = new Square(0, -1);
            blocks[3] = new Square(1, -1);
            this.color = Color.ORANGE;
            break;
        // Draw a J shape
        case 2:
            blocks[0] = new Square(0, 0);
            blocks[1] = new Square(0, -1);
            blocks[2] = new Square(-1, -1);
            blocks[3] = new Square(-2, -1);
            this.color = Color.PINK;
            break;
        // Draw a I shape
        case 3:
            blocks[0] = new Square(1, 0);
            blocks[1] = new Square(0, 0);
            blocks[2] = new Square(-1, 0);
            blocks[3] = new Square(-2, 0);
            this.color = Color.CYAN;
            break;
        // Draw a O shape
        case 4:
            blocks[0] = new Square(0, 0);
            blocks[1] = new Square(0, -1);
            blocks[2] = new Square(-1, 0);
            blocks[3] = new Square(-1, -1);
            this.color = Color.YELLOW;
            break;
        // Draw a S shape
        case 5:
            blocks[0] = new Square(-1, 0);
            blocks[1] = new Square(-1, -1);
            blocks[2] = new Square(0, 0);
            blocks[3] = new Square(-2, -1);
            this.color = Color.GREEN;
            break;
        case 6:
            // make a T shape
            blocks[0] = new Square(-1, 0);
            blocks[1] = new Square(-1, -1);
            blocks[2] = new Square(-2, -1);
            blocks[3] = new Square(0, -1);
            this.color = Color.PURPLE;
            break;
        default:
            // Draw a z shape
            blocks[0] = new Square(-2, 0);
            blocks[1] = new Square(-1, 0);
            blocks[2] = new Square(-1, -1);
            blocks[3] = new Square(0, -1);
            this.color = Color.RED;
            break;

        }

        // Add the board coordinates to local coordinates
        for (Square position : blocks) {
            position.col += col;
            position.row += line;
        }

    }

    public Color getColor() {
        return this.color;
    }

    public class Square {
        int col, row = 0;

        public Square(int col, int row) {
            this.col = col;
            this.row = row;
        }
    }

    // Returns false if tetromino cant move down
    public boolean moveDown(boolean[][] board) {
        boolean canMoveDown = true;
        // Check if we are at the bottom of the board
        for (Square square : this.blocks) {
            if (square.row - 1 < 0)
                return false;
        }

        // Set all of the current tetromino squares to false
        for (Square square : this.blocks)
            board[square.col][square.row] = false;
        // Check if the squares bellow the current tetromino are occupied
        // If they are filled, then we cant move down
        for (Square square : this.blocks) {
            if (board[square.col][square.row - 1]) {
                canMoveDown = false;
            }
        }

        if (!canMoveDown) {
            for (Square square : this.blocks)
                board[square.col][square.row] = true;
            return false;
        }
        for (Square square : this.blocks) {
            square.row--;
            board[square.col][square.row] = true;
        }
        return true;

    }

    public void moveRight(boolean[][] board) {
        // Set all of the current tetromino squares to false
        for (Square square : this.blocks)
            board[square.col][square.row] = false;

        // Check if we are at the left most square of the board or if the left square of
        // the board is filled
        for (Square square : this.blocks) {
            if (square.col + 1 > MyTetrisGame.cols - 1 || board[square.col + 1][square.row]) {
                for (Square square2 : this.blocks)
                    board[square2.col][square2.row] = true;
                return;
            }
        }

        for (Square square : this.blocks) {
            square.col++;
            board[square.col][square.row] = true;
        }
    }

    public void moveLeft(boolean[][] board) {
        // Set all of the current tetromino squares to false
        for (Square square : this.blocks)
            board[square.col][square.row] = false;

        // Check if we are at the left most square of the board or if the left square of
        // the board is filled
        for (Square square : this.blocks) {
            if (square.col - 1 < 0 || board[square.col - 1][square.row]) {

                for (Square square2 : this.blocks)
                    board[square2.col][square2.row] = true;
                return;
            }
        }

        for (Square square : this.blocks) {
            square.col--;
            board[square.col][square.row] = true;
        }
    }
}
