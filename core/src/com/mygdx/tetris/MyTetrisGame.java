package com.mygdx.tetris;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.tetris.Tetromino.Square;

import java.util.concurrent.ThreadLocalRandom;

public class MyTetrisGame extends ApplicationAdapter {
	static int BOARD_HEIGHT = 800;
	static int BOARD_WIDTH = BOARD_HEIGHT / 2;
	static int cols = 10;
	static int rows = 20;
	static int SQUARE_SIZE = BOARD_WIDTH / cols;

	// New tetromino in the top left corner
	Tetromino currentTetromino = new Tetromino(cols / 2, rows - 1);
	boolean board[][] = new boolean[cols][rows];

	ShapeRenderer shapeRenderer;

	@Override
	public void create() {
		this.shapeRenderer = new ShapeRenderer();
	}

	@Override
	public void render() {
		ScreenUtils.clear(Color.LIGHT_GRAY);

		// Draw main board
		drawMainBoard();
		drawCurrentTetromino();

		try {

			Thread.sleep(500);
		} catch (Throwable e) {
			System.out.println("Error " + e.getMessage());
			e.printStackTrace();
		}
		this.currentTetromino.moveDown(this.board);
	}

	@Override
	public void dispose() {
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
