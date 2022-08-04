package com.mygdx.tetris;

import java.util.concurrent.ThreadLocalRandom;
import com.badlogic.gdx.graphics.Color;

public class Tetromino {
    private Color color = Color.ORANGE;

    public Point blocks[] = new Point[4];
    private Integer rotationIndex = 0;
    private PieceType type = PieceType.EVERYTHING_EXCEPT_I_O;

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
            this.color = new Color();
            this.color.set(1, 0.589f, 0.109375f, 256f);
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

    public boolean canMoveDown() {

        for (final Point square : this.blocks)
            if (square.y - 1 >= 0) {
                if (MyTetrisGame.board[square.x][square.y - 1])
                    return false;
            } else
                return false;
        return true;
    }

    // Returns false if tetromino cant move down
    public void moveDown() {
        if (!canMoveDown())
            return;
        for (final Point square : this.blocks)
            square.y--;
    }

    public void moveRight() {
        for (final Point square : this.blocks)
            if (square.x + 1 > MyTetrisGame.cols - 1 || MyTetrisGame.board[square.x + 1][square.y])
                return;

        for (final Point square : this.blocks) {
            square.x++;
        }
    }

    public void moveLeft() {
        for (final Point square : this.blocks)
            if (square.x - 1 < 0 || MyTetrisGame.board[square.x - 1][square.y])
                return;

        for (final Point square : this.blocks)
            square.x--;
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

    enum PieceType {
        I, O, EVERYTHING_EXCEPT_I_O
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
        rotationIndex = betterMod(rotationIndex, 4);

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

    public Integer betterMod(final int x, final int m) {
        return (x % m + m) % m;
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
