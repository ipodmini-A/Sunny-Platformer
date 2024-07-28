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
		config.setTitle("Sunflowers and Roses Game");
		config.setResizable(false);
		config.useVsync(false); // Disabling VSync fixed an issue where the game would lag if music wasn't present
		// Don't ask me what that means please.
		new Lwjgl3Application(new PlatformerGame(), config);
	}
}
