package cchase.platformergame;

import cchase.platformergame.screens.TitleScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import java.util.List;

import static cchase.platformergame.Dialogue.loadDialogue;


public class PlatformerGame extends Game {
	public SpriteBatch batch;
	protected ShapeRenderer shapeRenderer;
	public BitmapFont font;
	public OrthographicCamera camera;
	protected ScalingViewport viewport;
	static List<DialogueLine> dialogueLines;

	@Override
	public void create() {
		// Create a new SpriteBatch, ShapeRenderer, and BitmapFont.
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		font = new BitmapFont();

		// Define your target width and height for the game's internal resolution.
		float targetWidth = 1280.0f; // Your desired game width
		float targetHeight = 720.0f; // Your desired game height

		// Set up the OrthographicCamera with the target width and height.
		camera = new OrthographicCamera(targetWidth, targetHeight);

		// Set up the ScalingViewport to maintain the fixed resolution and scale the rendering.
		//viewport = new ScalingViewport(Scaling.fit, targetWidth, targetHeight, camera);
		viewport = new FitViewport(targetWidth, targetHeight, camera);
		//viewport = new StretchViewport(1280,720, camera);
		viewport.apply(true);

		// Set the initial screen to the TitleScreen.
		setScreen(new TitleScreen(this));

		String filePath = "dialogue/dialogue.csv";
		dialogueLines = loadDialogue(filePath);

		for (DialogueLine line : dialogueLines) {
			System.out.println(line);
		}
	}

	@Override
	public void resize(int width, int height) {
		// Update the viewport when the window is resized.
		Gdx.graphics.setWindowedMode(width, height);
		viewport.update(width, height);
		camera.setToOrtho(false, width, height);
	}


	@Override
	public void render() {
		// Apply the viewport before rendering.
		viewport.apply();
		batch.setProjectionMatrix(camera.combined);
		camera.update();

		// Render the current screen.
		super.render();
	}

	@Override
	public void dispose() {
		// Dispose of the SpriteBatch, ShapeRenderer, and BitmapFont.
		batch.dispose();
		shapeRenderer.dispose();
		font.dispose();
	}
}
