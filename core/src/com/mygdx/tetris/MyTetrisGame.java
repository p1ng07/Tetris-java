package com.mygdx.tetris;

import java.util.Vector;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.ScreenUtils;

public class MyTetrisGame extends ApplicationAdapter {
	static int BOARD_HEIGHT = 800;
	static int BOARD_WIDTH = BOARD_HEIGHT / 2;
	static int cols = 10;
	static int rows = 22;
	static int SQUARE_SIZE = BOARD_WIDTH / cols;

	private boolean isGameOver = false;

	// New tetromino in the top left corner
	Tetromino currentTetromino = newBoardTetromino();

	private Tetromino newBoardTetromino() {
		return new Tetromino(cols / 2, rows - 3);
	}

	Tetromino ghostTetromino = newBoardTetromino();
	Tetromino nextPiece = newBoardTetromino();

	Vector<Tetromino> boardTetrominos = new Vector<Tetromino>();

	// If board[col][row] is set to true, then a piece is there
	static boolean board[][] = new boolean[cols][rows];
	static Color[][] boardColors = new Color[cols][rows];

	ShapeRenderer shapeRenderer;
	private Sound backgroundMusic;

	private float timeElapsedSinceTouchingGround;
	private final float timeToSetAPieceAfterTouching = 0.5f;
	private Float timeSinceMovingDown = 0f;
	private Float timeLimitToMoveDown = 1.0f;

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

		// Re-initialize the game board
		restartBoard();
	}

	private void restartBoard() {
		for (int i = 0; i < cols; i++)
			for (int j = 0; j < rows; j++) {
				MyTetrisGame.board[i][j] = false;
			}
		this.currentTetromino = newBoardTetromino();
		this.nextPiece = newBoardTetromino();
	}

	// TODO: increase speed at which pieces fall
	// incrementally, pretty game, sound effect when line clear
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
		} else {
			restartBoard();
			isGameOver = false;
		}

		// Increment Timers
		timeElapsedSinceTouchingGround += Gdx.graphics.getDeltaTime();
		timeSinceMovingDown += Gdx.graphics.getDeltaTime();

		if (Gdx.input.isKeyJustPressed(Keys.SPACE)) {
			this.currentTetromino.hardDrop();
			this.hardDrop = true;
		}

		if (Gdx.input.isKeyPressed(Keys.S) || Gdx.input.isKeyPressed(Keys.O))
			currentTetromino.moveDown();

		// Rotate clock wise
		if (!isGameOver && (Gdx.input.isKeyJustPressed(Keys.UP) || Gdx.input.isKeyJustPressed(Keys.W)
				|| Gdx.input.isKeyJustPressed(Keys.COLON) || Gdx.input.isKeyJustPressed(Keys.T)
				|| Gdx.input.isKeyJustPressed(Keys.K))) {
			currentTetromino.rotate(true, true);
		}

		// Rotate counter clockwise
		if (!isGameOver && (Gdx.input.isKeyJustPressed(Keys.H) || Gdx.input.isKeyJustPressed(Keys.J))) {
			currentTetromino.rotate(false, true);
		}

		if (Gdx.input.isKeyJustPressed(Keys.LEFT) || Gdx.input.isKeyJustPressed(Keys.A)) {
			currentTetromino.moveLeft();
			timeElapsedSinceTouchingGround = 0.0f;
			timerLeft = 0.0f;
		}

		if (Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.A)) {
			timerLeft += Gdx.graphics.getDeltaTime();
			timeElapsedSinceTouchingGround = 0.0f;
			if (timerLeft > moveTimerThreshold)
				currentTetromino.moveLeft();
		}

		if (Gdx.input.isKeyJustPressed(Keys.RIGHT) || Gdx.input.isKeyJustPressed(Keys.E)
				|| Gdx.input.isKeyJustPressed(Keys.D)) {
			currentTetromino.moveRight();
			timeElapsedSinceTouchingGround = 0.0f;
			timerRight = 0.0f;
		}
		if (Gdx.input.isKeyPressed(Keys.RIGHT) || Gdx.input.isKeyPressed(Keys.E) || Gdx.input.isKeyPressed(Keys.D)) {
			timerRight += Gdx.graphics.getDeltaTime();
			timeElapsedSinceTouchingGround = 0.0f;
			if (timerRight > moveTimerThreshold)
				currentTetromino.moveRight();
		}

		if (timeSinceMovingDown >= timeLimitToMoveDown) {
			this.currentTetromino.moveDown();
			timeSinceMovingDown = 0.0f;
		}

		// Set the piece in the board definitely
		if (!isGameOver && timeElapsedSinceTouchingGround >= timeToSetAPieceAfterTouching || this.hardDrop) {
			timeElapsedSinceTouchingGround = 0f;
			this.hardDrop = false;
			if (!this.currentTetromino.canMoveDown()) {
				setPieceIntoBoard(currentTetromino);
				// this.boardTetrominos.add(currentTetromino);

				final Tetromino newTetromino = newBoardTetromino();
				if (!arePositionsValid(newTetromino.blocks)) {
					isGameOver = true;
				} else {
					this.currentTetromino = this.nextPiece;
					nextPiece = newBoardTetromino();
				}
			}
			if (checkForLinesToClear()) {
				clearLines();
				timeLimitToMoveDown -= 0.03f;
			}
		}
	}

	private void clearLines() {

		for (int y = rows - 1; y > -1; y--) {
			Integer numberOfFilledSquares = 0;
			for (int x = 0; x < cols; x++) {
				if (board[x][y])
					numberOfFilledSquares++;
			}
			if (numberOfFilledSquares == cols) {
				// Render white squares on every block that is going to be cleared
				shapeRenderer.begin(ShapeType.Filled);
				shapeRenderer.setColor(Color.WHITE);
				for (int x = 0; x < cols; x++) {
					drawSquareOnMainBoard(new Point(x, y));
				}
				shapeRenderer.end();

				// Shift all of the lines above it down
				for (int row = y; row < rows - 1; row++) {
					for (int x = 0; x < cols; x++) {
						board[x][row] = board[x][row + 1];
						boardColors[x][row] = boardColors[x][row + 1];
					}
				}
				// manually set the last line to nothing
				for (int x = 0; x < cols; x++) {
					board[x][rows - 1] = false;
				}
			}

		}
	}

	// Returns true if there are line to clear
	private Boolean checkForLinesToClear() {
		for (int y = 0; y < rows; y++) {
			Integer numberOfFilledSquares = 0;
			for (int x = 0; x < cols; x++) {
				if (board[x][y])
					numberOfFilledSquares++;
			}
			if (numberOfFilledSquares == cols) {
				return true;
			}
		}
		return false;
	}

	private void setPieceIntoBoard(final Tetromino tetromino) {
		for (final Point point : tetromino.blocks) {
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
					BOARD_HEIGHT - nextPieceYOffset - (rows - 2 - position.y) * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
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
				// if (x == 0 && y == 0) {
				// shapeRenderer.setColor(Color.PINK);
				// drawSquareOnMainBoard(new Point(x, y));
				// }
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
		shapeRenderer.rect(position.x * SQUARE_SIZE, BOARD_HEIGHT - (rows - 2 - position.y) * SQUARE_SIZE, SQUARE_SIZE,
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
				shapeRenderer.rect(i * SQUARE_SIZE, j * SQUARE_SIZE, 0, 0, SQUARE_SIZE, SQUARE_SIZE, 1, 1, 0);
			}
		}
		shapeRenderer.end();
	}

}
