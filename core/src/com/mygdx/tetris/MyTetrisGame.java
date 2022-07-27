package com.mygdx.tetris;

import java.util.Vector;
import java.util.concurrent.TimeUnit;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.tetris.Tetromino.Square;

public class MyTetrisGame extends ApplicationAdapter {
	static int BOARD_HEIGHT = 800;
	static int BOARD_WIDTH = BOARD_HEIGHT / 2;
	static int cols = 10;
	static int rows = 20;
	static int SQUARE_SIZE = BOARD_WIDTH / cols;

	private boolean isGameOver = false;

	// New tetromino in the top left corner
	Tetromino currentTetromino = new Tetromino(cols / 2, rows - 1);

	Vector<Tetromino> boardTetrominos = new Vector<Tetromino>();

	// If board[col][row] is set to true, then a piece is there
	boolean board[][] = new boolean[cols][rows];

	ShapeRenderer shapeRenderer;
	private long milisecondsToWaitForDrop = 100;

	@Override
	public void create() {
		this.shapeRenderer = new ShapeRenderer();

		// Reset the game board
		for (int i = 0; i < cols; i++)
			for (int j = 0; j < rows; j++)
				this.board[i][j] = false;

		// Set the first tetromino bounds on the board
		for (Square square : this.currentTetromino.blocks)
			this.board[square.col][square.row] = true;

	}

	@Override
	public void render() {
		ScreenUtils.clear(Color.LIGHT_GRAY);

		// Draw main board
		drawMainBoard();
		drawBoardTetrominos();
		drawCurrentTetromino();

		try {
			Thread.sleep(milisecondsToWaitForDrop);
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
		if (!this.currentTetromino.moveDown(this.board)) {
			this.boardTetrominos.add(currentTetromino);

			this.currentTetromino = new Tetromino(cols / 2, rows - 1);
		}
	}

	private void drawBoardTetrominos() {

		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.set(ShapeType.Filled);

		// Draw every Tetromino block individually
		for (Tetromino tetromino : this.boardTetrominos) {
			shapeRenderer.setColor(tetromino.getColor());
			for (Square position : tetromino.blocks)
				drawSquareOnMainBoard(position);
		}
		shapeRenderer.end();
	}

	private void drawCurrentTetromino() {

		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(this.currentTetromino.getColor());
		shapeRenderer.set(ShapeType.Filled);

		// Draw every Tetromino block individually
		for (Square position : this.currentTetromino.blocks) {
			drawSquareOnMainBoard(position);
		}

		shapeRenderer.end();
	}

	private void drawSquareOnMainBoard(Square position) {
		shapeRenderer.rect(position.col * SQUARE_SIZE, BOARD_HEIGHT - (rows - position.row) * SQUARE_SIZE, SQUARE_SIZE,
				SQUARE_SIZE);
	}

	private void drawMainBoard() {
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(Color.BLACK);
		shapeRenderer.set(ShapeType.Filled);
		for (int i = 0; i < cols; i++) {
			for (int j = 0; j < rows; j++) {
				shapeRenderer.rect(i * SQUARE_SIZE, j * SQUARE_SIZE, 0, 0, SQUARE_SIZE - 1, SQUARE_SIZE - 1, 1, 1, 0);
			}
		}
		shapeRenderer.end();
	}
}
