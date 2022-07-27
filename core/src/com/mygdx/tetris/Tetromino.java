package com.mygdx.tetris;

import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.graphics.Color;

/**
 * Rep
 */
public class Tetromino {
    private Color color = Color.ORANGE;
    Square blocks[] = new Square[4];

    // Generates a random Tetris Piece
    // The col and line given are the top left most corner of the piece
    public Tetromino(int col, int line) {
        // In order: L, J, I, O, S, T, Z
        int randomNum = ThreadLocalRandom.current().nextInt(1, 7 + 1);
        randomNum = 7;
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
            this.color = Color.BLUE;
            break;
        // Draw a I shape
        case 3:
            blocks[0] = new Square(1, 0);
            blocks[1] = new Square(0, 0);
            blocks[2] = new Square(-1, 0);
            blocks[3] = new Square(-2, 0);
            this.color.r = 0;
            this.color.g = 1;
            this.color.b = 1;
            this.color.a = 1;
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

    public void moveDown(boolean[][] board) {
        boolean canMoveDown = true;
        for (Square square : this.blocks) {
            if (square.row - 1 < 0)
                return;
            if (board[square.col][square.row - 1] == true) {
                canMoveDown = false;
            }
        }
        if (canMoveDown) {
            for (Square square : this.blocks) {
                square.row--;
            }
        }

    }
}
