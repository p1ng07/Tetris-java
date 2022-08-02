package com.mygdx.tetris;

import java.util.Vector;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.tetris.Tetromino.PieceType;

public class MyTetrisGame extends ApplicationAdapter {
	static int BOARD_HEIGHT = 800;
	static int BOARD_WIDTH = BOARD_HEIGHT / 2;
	static int cols = 10;
	static int rows = 20;
	static int SQUARE_SIZE = BOARD_WIDTH / cols;

	private boolean isGameOver = false;

	// New tetromino in the top left corner
	Tetromino currentTetromino = new Tetromino(cols / 2, rows - 1);
	Tetromino nextPiece = new Tetromino(cols / 2, rows - 1);

	Vector<Tetromino> boardTetrominos = new Vector<Tetromino>();

	// If board[col][row] is set to true, then a piece is there
	static boolean board[][] = new boolean[cols][rows + 2];

	ShapeRenderer shapeRenderer;

	private float timeElapsedSinceTouchingGround;
	private float timeToSetAPieceAfterTouching = 0.5f;

	private float timerLeft;
	private float timerRight;
	private final float moveTimerThreshold = 0.3f;
	private int nextPieceXOffset = 30;
	private int nextPieceYOffset = 100;

	private SpriteBatch batch;
	private BitmapFont font;

	@Override
	public void create() {
		this.font = new BitmapFont();
		this.batch = new SpriteBatch();
		this.shapeRenderer = new ShapeRenderer();

		// Reset the game board
		for (int i = 0; i < cols; i++)
			for (int j = 0; j < rows; j++)
				this.board[i][j] = false;
	}

	// TODO: ghost piece and hard drop
	@Override
	public void render() {
		ScreenUtils.clear(Color.LIGHT_GRAY);

		drawMainBoard();
		drawBoardTetrominos();
		drawNextPiece();
		if (!isGameOver)
			drawCurrentTetromino();

		timeElapsedSinceTouchingGround += Gdx.graphics.getDeltaTime();

		if (Gdx.input.isKeyPressed(Keys.DOWN))
			currentTetromino.moveDown(board);

		if (Gdx.input.isKeyJustPressed(Keys.UP)) {
			currentTetromino.rotate(true, true);
			System.out.println("The new rotation index is " + this.currentTetromino.rotationIndex);
		}

		if (Gdx.input.isKeyJustPressed(Keys.LEFT)) {
			currentTetromino.moveLeft(board);
			timeElapsedSinceTouchingGround = 0.0f;
			timerLeft = 0.0f;
		}

		if (Gdx.input.isKeyPressed(Keys.LEFT)) {
			timerLeft += Gdx.graphics.getDeltaTime();
			timeElapsedSinceTouchingGround = 0.0f;
			if (timerLeft > moveTimerThreshold)
				currentTetromino.moveLeft(board);
		}

		if (Gdx.input.isKeyJustPressed(Keys.RIGHT)) {
			currentTetromino.moveRight(board);
			timeElapsedSinceTouchingGround = 0.0f;
			timerRight = 0.0f;
		}
		if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
			timerRight += Gdx.graphics.getDeltaTime();
			timeElapsedSinceTouchingGround = 0.0f;
			if (timerRight > moveTimerThreshold)
				currentTetromino.moveRight(board);
		}

		// Set the piece in the board definitely
		if (!isGameOver && timeElapsedSinceTouchingGround >= timeToSetAPieceAfterTouching) {
			timeElapsedSinceTouchingGround = 0f;
			if (this.currentTetromino.moveDown(MyTetrisGame.board) == false) {
				this.boardTetrominos.add(currentTetromino);
				for (final Point point : this.currentTetromino.blocks) {
					board[point.x][point.y] = true;
				}

				final Tetromino newTetromino = new Tetromino(cols / 2, rows - 1);
				if (!arePositionsValid(newTetromino.blocks)) {
					isGameOver = true;
				} else {
					this.currentTetromino = this.nextPiece;
					nextPiece = new Tetromino(cols / 2, rows - 1);
				}
			}
		}
	}

	private void drawNextPiece() {

		shapeRenderer.begin(ShapeType.Filled);
		// Draw every Tetromino block individually
		shapeRenderer.setColor(this.nextPiece.getColor());
		for (final Point position : this.nextPiece.blocks) {
			shapeRenderer.rect((BOARD_WIDTH + nextPieceXOffset) + position.x * SQUARE_SIZE,
					BOARD_HEIGHT - nextPieceYOffset - (rows - position.y) * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
		}

		shapeRenderer.setColor(Color.WHITE);

		// Upper line
		shapeRenderer.rectLine(BOARD_WIDTH + nextPieceXOffset + SQUARE_SIZE * 2.5f, BOARD_HEIGHT - nextPieceYOffset / 2,
				BOARD_WIDTH + nextPieceXOffset + MyTetrisGame.SQUARE_SIZE * 7.2f, BOARD_HEIGHT - nextPieceYOffset / 2,
				4);
		// Downer ? line
		shapeRenderer.rectLine(BOARD_WIDTH + nextPieceXOffset + SQUARE_SIZE * 2.5f, BOARD_HEIGHT - nextPieceYOffset * 2,
				BOARD_WIDTH + nextPieceXOffset + MyTetrisGame.SQUARE_SIZE * 7.2f, BOARD_HEIGHT - nextPieceYOffset * 2,
				4);

		// Correct line
		shapeRenderer.rectLine(BOARD_WIDTH + nextPieceXOffset + SQUARE_SIZE * 2.5f, BOARD_HEIGHT - nextPieceYOffset * 2,
				BOARD_WIDTH + nextPieceXOffset + SQUARE_SIZE * 2.5f, BOARD_HEIGHT - nextPieceYOffset / 2, 4);

		// Wroing line
		shapeRenderer.rectLine(BOARD_WIDTH + nextPieceXOffset + MyTetrisGame.SQUARE_SIZE * 7.2f,
				BOARD_HEIGHT - nextPieceYOffset * 2, BOARD_WIDTH + nextPieceXOffset + MyTetrisGame.SQUARE_SIZE * 7.2f,
				BOARD_HEIGHT - nextPieceYOffset / 2, 4);
		shapeRenderer.end();

	}

	private void drawBoardTetrominos() {

		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.set(ShapeType.Filled);
		// Draw every Tetromino block individually
		for (final Tetromino tetromino : this.boardTetrominos) {
			shapeRenderer.setColor(tetromino.getColor());
			for (final Point position : tetromino.blocks)
				drawSquareOnMainBoard(position);
		}
		shapeRenderer.end();
	}

	private void drawCurrentTetromino() {

		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(this.currentTetromino.getColor());
		shapeRenderer.set(ShapeType.Filled);

		// Draw every Tetromino block individually
		for (Point position : this.currentTetromino.blocks) {
			drawSquareOnMainBoard(position);
		}

		shapeRenderer.end();
	}

	private void drawSquareOnMainBoard(final Point position) {
		shapeRenderer.rect(position.x * SQUARE_SIZE, BOARD_HEIGHT - (rows - position.y) * SQUARE_SIZE, SQUARE_SIZE,
				SQUARE_SIZE);
	}

	// Checks if every point is within board bounds and if every point isn't set in
	// the board
	public static boolean arePositionsValid(final Point[] points) {
		for (final Point square : points) {
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
