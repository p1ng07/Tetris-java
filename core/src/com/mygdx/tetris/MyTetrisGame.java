package com.mygdx.tetris;

import java.util.Vector;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
	Tetromino ghostTetromino = new Tetromino(cols / 2, rows - 1);
	Tetromino nextPiece = new Tetromino(cols / 2, rows - 1);

	Vector<Tetromino> boardTetrominos = new Vector<Tetromino>();

	// If board[col][row] is set to true, then a piece is there
	static boolean board[][] = new boolean[cols][rows + 2];
	static Color[][] boardColors = new Color[cols][rows + 2];

	ShapeRenderer shapeRenderer;
	private Sound backgroundMusic;

	private float timeElapsedSinceTouchingGround;
	private final float timeToSetAPieceAfterTouching = 0.5f;

	private float timerLeft;
	private float timerRight;
	private final float moveTimerThreshold = 0.3f;
	private final int nextPieceXOffset = 30;
	private final int nextPieceYOffset = 100;
	private boolean hardDrop = false;

	@Override
	public void create() {
		this.backgroundMusic = Gdx.audio.newSound(Gdx.files.internal("Tetris.mp3"));
		final long id = backgroundMusic.play();
		backgroundMusic.setLooping(id, true);

		this.shapeRenderer = new ShapeRenderer();

		// Reset the game board
		restartBoard();
	}

	private void restartBoard() {
		for (int i = 0; i < cols; i++)
			for (int j = 0; j < rows; j++) {
				MyTetrisGame.board[i][j] = false;
			}
	}

	// TODO: Add hard drop, refactor the way piece are set to the board using the
	// already defined boardColors ard board arrays
	@Override
	public void render() {
		ScreenUtils.clear(Color.LIGHT_GRAY);

		drawMainBoard();
		drawBoardTetrominos();
		drawNextPiece();
		if (!isGameOver) {
			updateGhostPiece();
			drawGhostPiece();
			drawCurrentTetromino();
		}

		timeElapsedSinceTouchingGround += Gdx.graphics.getDeltaTime();

		if (Gdx.input.isKeyJustPressed(Keys.SPACE)) {
			this.currentTetromino.hardDrop();
			this.hardDrop = true;
		}

		if (Gdx.input.isKeyPressed(Keys.DOWN))
			currentTetromino.moveDown(board);

		if (!isGameOver && Gdx.input.isKeyJustPressed(Keys.UP)) {
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
		if (!isGameOver && timeElapsedSinceTouchingGround >= timeToSetAPieceAfterTouching || this.hardDrop) {
			timeElapsedSinceTouchingGround = 0f;
			this.hardDrop = false;
			if (this.currentTetromino.moveDown(MyTetrisGame.board) == false) {
				setPieceIntoBoard(currentTetromino);
				// this.boardTetrominos.add(currentTetromino);

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

	private void setPieceIntoBoard(Tetromino tetromino) {
		for (Point point : tetromino.blocks) {
			MyTetrisGame.board[point.x][point.y] = true;
			boardColors[point.x][point.y] = tetromino.getColor();
		}

	}

	private void drawGhostPiece() {

		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(Color.GRAY);
		shapeRenderer.set(ShapeType.Filled);

		// Draw every Tetromino block individually
		for (final Point position : this.ghostTetromino.blocks) {
			drawSquareOnMainBoard(position);
		}

		shapeRenderer.end();
	}

	private void updateGhostPiece() {
		for (int i = 0; i < 4; i++) {
			this.ghostTetromino.blocks[i].x = this.currentTetromino.blocks[i].x;
			this.ghostTetromino.blocks[i].y = this.currentTetromino.blocks[i].y;
		}

		do {
			for (final Point point : this.ghostTetromino.blocks) {
				point.y--;
			}
		} while (MyTetrisGame.arePositionsValid(this.ghostTetromino.blocks));

		for (final Point point : this.ghostTetromino.blocks) {
			point.y++;
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
		for (int x = 0; x < cols; x++) {
			for (int y = 0; y < rows; y++) {
				if (board[x][y]) {
					shapeRenderer.setColor(isGameOver ? Color.GRAY : boardColors[x][y]);
					drawSquareOnMainBoard(new Point(x, y));
				}
			}
		}
		shapeRenderer.end();
	}

	private void drawCurrentTetromino() {

		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(this.currentTetromino.getColor());
		shapeRenderer.set(ShapeType.Filled);

		// Draw every Tetromino block individually
		for (final Point position : this.currentTetromino.blocks) {
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
