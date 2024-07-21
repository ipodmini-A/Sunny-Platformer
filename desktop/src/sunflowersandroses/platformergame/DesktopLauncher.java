package sunflowersandroses.platformergame;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setResizable(true);
		//config.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
		config.setWindowedMode(1280,720);
		config.setForegroundFPS(60); // TODO: Fix bug that causes the game to run as fast as the FPS allows
		config.setTitle("platformerGame");
		config.setResizable(false);
		new Lwjgl3Application(new PlatformerGame(), config);
	}
}
