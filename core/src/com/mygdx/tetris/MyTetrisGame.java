package com.mygdx.tetris;

import java.util.Vector;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.ScreenUtils;

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
	static boolean board[][] = new boolean[cols][rows + 2];

	ShapeRenderer shapeRenderer;

	private float timeElapsedSinceTouchingGround;
	private float timeToSetAPieceAfterTouching = 0.5f;

	private float timerLeft;
	private float timerRight;
	private float moveTimerThreshold = 0.3f;

	@Override
	public void create() {
		this.shapeRenderer = new ShapeRenderer();

		// Reset the game board
		for (int i = 0; i < cols; i++)
			for (int j = 0; j < rows; j++)
				this.board[i][j] = false;
	}

	// TODO: Add rotations, ghost piece and hard drop
	@Override
	public void render() {
		ScreenUtils.clear(Color.LIGHT_GRAY);

		drawMainBoard();
		drawBoardTetrominos();
		if (!isGameOver)
			drawCurrentTetromino();

		timeElapsedSinceTouchingGround += Gdx.graphics.getDeltaTime();

		if (Gdx.input.isKeyPressed(Keys.DOWN))
			currentTetromino.moveDown(board);

		if (Gdx.input.isKeyJustPressed(Keys.UP)) {
			for (Point point : this.currentTetromino.blocks)
				this.board[point.x][point.y] = false;

			currentTetromino.rotate(board, true, true);
			for (Point point : this.currentTetromino.blocks)
				this.board[point.x][point.y] = false;
		}

		if (Gdx.input.isKeyJustPressed(Keys.LEFT)) {
			currentTetromino.moveLeft(board);
			timerLeft = 0.0f;
		}
		if (Gdx.input.isKeyPressed(Keys.LEFT)) {
			timerLeft += Gdx.graphics.getDeltaTime();
			if (timerLeft > moveTimerThreshold)
				currentTetromino.moveLeft(board);
		}

		if (Gdx.input.isKeyJustPressed(Keys.RIGHT)) {
			currentTetromino.moveRight(board);
			timerRight = 0.0f;
		}
		if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
			timerRight += Gdx.graphics.getDeltaTime();
			if (timerRight > moveTimerThreshold)
				currentTetromino.moveRight(board);
		}

		// Set the piece in the board definitely
		if (!isGameOver && timeElapsedSinceTouchingGround >= timeToSetAPieceAfterTouching) {
			timeElapsedSinceTouchingGround = 0f;
			if (this.currentTetromino.moveDown(this.board) == false) {
				this.boardTetrominos.add(currentTetromino);

				for (Point point : this.currentTetromino.blocks)
					board[point.x][point.y] = true;

				Tetromino newTetromino = new Tetromino(cols / 2, rows - 1);
				if (!arePositionsValid(newTetromino.blocks)) {
					isGameOver = true;
				} else {
					this.currentTetromino = newTetromino;
				}
			}
		}
	}

	private void drawBoardTetrominos() {

		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.set(ShapeType.Filled);
		// Draw every Tetromino block individually
		for (Tetromino tetromino : this.boardTetrominos) {
			shapeRenderer.setColor(tetromino.getColor());
			for (Point position : tetromino.blocks)
				drawSquareOnMainBoard(position);
		}
		shapeRenderer.end();
	}

	private void drawCurrentTetromino() {

		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(this.currentTetromino.getColor());
		shapeRenderer.set(ShapeType.Filled);
		for (int i = 0; i < 4; i++) {
			if (i == 0) {
				shapeRenderer.setColor(Color.WHITE);
			} else {
				shapeRenderer.setColor(this.currentTetromino.getColor());
			}
			drawSquareOnMainBoard(this.currentTetromino.blocks[i]);
		}

		// Draw every Tetromino block individually
		// for (Point position : this.currentTetromino.blocks) {
		// drawSquareOnMainBoard(position);
		// }

		shapeRenderer.end();
	}

	private void drawSquareOnMainBoard(Point position) {
		shapeRenderer.rect(position.x * SQUARE_SIZE, BOARD_HEIGHT - (rows - position.y) * SQUARE_SIZE, SQUARE_SIZE,
				SQUARE_SIZE);
	}

	// Checks if every point is within board bounds and if every point isn't set in
	// the board
	public static boolean arePositionsValid(Point[] points) {
		for (Point square : points) {
			if (square.x < 0 || square.x > cols - 1 || square.y < 0 || square.y > rows)
				return false;
			if (MyTetrisGame.board[square.x][square.y])
				return false;
		}
		return true;

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
