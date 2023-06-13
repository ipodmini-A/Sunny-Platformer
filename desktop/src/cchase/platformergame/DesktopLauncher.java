package cchase.platformergame;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import cchase.platformergame.PlatformerGame;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setWindowedMode(1290,720);
		config.setForegroundFPS(60);
		config.setTitle("platformerGame");
		config.setResizable(false);
		new Lwjgl3Application(new PlatformerGame(), config);
	}
}
