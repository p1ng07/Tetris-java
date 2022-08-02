package com.mygdx.tetris;

import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.graphics.Color;

public class Tetromino {
    private Color color = Color.ORANGE;
    Point blocks[] = new Point[4];
    Integer rotationIndex = 0;
    PieceType type = PieceType.EVERYTHING_EXCEPT_I_O;

    // Generates a random Tetris Piece
    // The col and line given are the reference square for the piece
    public Tetromino(final int col, final int line) {
        final int randomNum = ThreadLocalRandom.current().nextInt(1, 7 + 1);
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
            this.type = PieceType.I;
            break;
        case 4:
            // Draw a O shape
            blocks[0] = new Point(-1, -1);
            blocks[1] = new Point(0, -1);
            blocks[2] = new Point(-1, 0);
            blocks[3] = new Point(0, 0);
            this.color = Color.YELLOW;
            this.type = PieceType.O;
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
        for (final Point position : blocks) {
            position.x += col;
            position.y += line;
        }
    }

    public Color getColor() {
        return this.color;
    }

    // Returns false if tetromino cant move down
    public boolean moveDown(final boolean[][] board) {
        boolean canMoveDown = true;
        // Check if we are at the bottom of the board
        for (final Point square : this.blocks)
            if (square.y - 1 < 0)
                return false;

        // Check if the squares bellow the current tetromino are occupied
        // If they are filled, then we cant move down
        for (final Point square : this.blocks)
            if (board[square.x][square.y - 1])
                canMoveDown = false;

        if (!canMoveDown) {
            return false;
        }
        for (final Point square : this.blocks) {
            square.y--;
        }
        return true;
    }

    public void moveRight(final boolean[][] board) {
        // Check if we are at the left most square of the board or if the left square of
        // the board is filled
        for (final Point square : this.blocks)
            if (square.x + 1 > MyTetrisGame.cols - 1 || board[square.x + 1][square.y])
                return;

        for (final Point square : this.blocks) {
            square.x++;
        }
    }

    public void moveLeft(final boolean[][] board) {
        // Check if we are at the left most square of the board or if the left square of
        // the board is filled
        for (final Point square : this.blocks)
            if (square.x - 1 < 0 || board[square.x - 1][square.y])
                return;

        for (final Point square : this.blocks)
            square.x--;
    }

    public void rotate(final boolean clockwise, final Boolean offset) {

        // Apply true rotation to every block
        for (final Point point : this.blocks) {
            point.rotate(this.blocks[0], clockwise);
        }

        if (!offset) {
            return;
        }
        final Point newBlocks[] = new Point[] { new Point(), new Point(), new Point(), new Point() };

        boolean valid = false;

        final int oldRotationIndex = this.rotationIndex;

        rotationIndex = clockwise ? rotationIndex + 1 : rotationIndex - 1;
        rotationIndex = mod(rotationIndex, 4);

        Point[][] offsetToUse = DEFAULT_OFFSET;
        if (type == PieceType.I) {
            offsetToUse = I_OFFSET;
        } else if (type == PieceType.O) {
            offsetToUse = O_OFFSET;
        }

        for (int j = 0; j < offsetToUse[rotationIndex].length; j++) {
            for (int i = 0; i < 4; i++) {
                newBlocks[i].x = this.blocks[i].x
                        + (offsetToUse[oldRotationIndex][j].x - offsetToUse[rotationIndex][j].x);
                newBlocks[i].y = this.blocks[i].y
                        + (offsetToUse[oldRotationIndex][j].y - offsetToUse[rotationIndex][j].y);
            }
            if (MyTetrisGame.arePositionsValid(newBlocks)) {
                this.blocks = newBlocks;
                valid = true;
                break;
            }
        }

        if (!valid) {
            rotationIndex = oldRotationIndex;
            this.rotate(!clockwise, false);
        }

    }

    static Point[][] O_OFFSET = { { new Point(0, 0) }, { new Point(0, -1) }, { new Point(-1, -1) },
            { new Point(-1, 0) } };

    static Point[][] I_OFFSET = {
            { new Point(0, 0), new Point(-1, 0), new Point(2, 0), new Point(-1, 0), new Point(2, 0) },
            { new Point(-1, 0), new Point(0, 0), new Point(0, 0), new Point(0, 1), new Point(0, -2) },
            { new Point(-1, 1), new Point(1, 1), new Point(-2, 1), new Point(1, 0), new Point(-2, 0) },
            { new Point(0, 1), new Point(0, 1), new Point(0, 1), new Point(0, -1), new Point(0, 2) } };

    static Point[][] DEFAULT_OFFSET = {
            { new Point(0, 0), new Point(0, 0), new Point(+0, -0), new Point(0, +0), new Point(+0, +0) },
            { new Point(0, 0), new Point(1, 0), new Point(+1, -1), new Point(0, +2), new Point(+1, +2) },
            { new Point(0, 0), new Point(0, 0), new Point(0, 0), new Point(0, 0), new Point(0, 0) },
            { new Point(0, 0), new Point(-1, 0), new Point(-1, -1), new Point(0, +2), new Point(-1, +2) } };

    public Integer mod(final int x, final int m) {
        return (x % m + m) % m;
    }

    enum PieceType {
        I, O, EVERYTHING_EXCEPT_I_O
    }

    public void hardDrop() {
        do {
            for (Point point : this.blocks) {
                point.y--;
            }
        } while (MyTetrisGame.arePositionsValid(blocks));
        for (Point point : this.blocks) {
            point.y++;
        }
    }
}
