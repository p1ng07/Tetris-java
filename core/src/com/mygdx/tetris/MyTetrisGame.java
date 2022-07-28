package com.mygdx.tetris;

import java.util.Vector;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.tetris.Tetromino.Square;

public class MyTetrisGame extends ApplicationAdapter {
	static int BOARD_HEIGHT = 800;
	static int BOARD_WIDTH = BOARD_HEIGHT / 2;
	static int cols = 10;
	static int rows = 20;
	static int SQUARE_SIZE = BOARD_WIDTH / cols;

	private boolean isGameOver = false;

	private Stage stage;

	// New tetromino in the top left corner
	Tetromino currentTetromino = new Tetromino(cols / 2, rows - 1);

	Vector<Tetromino> boardTetrominos = new Vector<Tetromino>();

	// If board[col][row] is set to true, then a piece is there
	boolean board[][] = new boolean[cols][rows];

	ShapeRenderer shapeRenderer;

	private float timeElapsedSinceTouchingGround;
	private float maximumTimeToSetAPieceAfterTouching = 1.0f;

	private float timeElapsedSinceRight = 0.0f;

	@Override
	public void create() {
		this.shapeRenderer = new ShapeRenderer();
		this.stage = new Stage();

		this.stage.addListener(new InputListener() {
			public boolean keyDown(InputEvent event, int keycode) {
				if (keycode == Input.Keys.DOWN) {
					currentTetromino.moveDown(board);
					return true;
				}
				return false;
			}
		});

		// Reset the game board
		for (int i = 0; i < cols; i++)
			for (int j = 0; j < rows; j++)
				this.board[i][j] = false;

	}

	// TODO: Add rotations, and timings for move right and left
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
		if (Gdx.input.isKeyPressed(Keys.LEFT))
			currentTetromino.moveLeft(board);
		if (Gdx.input.isKeyPressed(Keys.RIGHT))
			currentTetromino.moveRight(board);

		// Set the piece in the board definitely
		if (!isGameOver && timeElapsedSinceTouchingGround >= maximumTimeToSetAPieceAfterTouching) {
			timeElapsedSinceTouchingGround = 0f;
			if (this.currentTetromino.moveDown(this.board) == false) {
				this.boardTetrominos.add(currentTetromino);

				Tetromino newTetromino = new Tetromino(cols / 2, rows - 1);
				if (isTetrominoColliding(newTetromino)) {
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

	private boolean isTetrominoColliding(Tetromino tetromino) {
		for (Square square : tetromino.blocks) {
			if (this.board[square.col][square.row]) {
				return true;
			}
		}
		return false;

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
