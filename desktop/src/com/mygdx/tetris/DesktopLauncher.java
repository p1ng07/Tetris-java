package com.mygdx.tetris;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.mygdx.tetris.MyTetrisGame;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	static int WINDOW_HEIGHT = 800;
	static int WINDOW_WIDTH = 800;

	public static void main(String[] arg) {
		MyTetrisGame myGame = new MyTetrisGame();
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(60);
		config.setResizable(false);
		config.setWindowedMode(WINDOW_WIDTH, WINDOW_HEIGHT);
		config.setTitle("Tetris");
		new Lwjgl3Application(myGame, config);
	}
}
