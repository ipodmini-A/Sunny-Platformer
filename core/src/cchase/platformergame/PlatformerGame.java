package cchase.platformergame;

import cchase.platformergame.screens.TitleScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;

public class PlatformerGame extends Game
{
	public SpriteBatch batch;
	protected ShapeRenderer shapeRenderer;
	public BitmapFont font;
	public OrthographicCamera camera;
	protected ScalingViewport viewport;

	@Override
	public void create()
	{
		// create() makes a new SpriteBatch, ShapeRenderer and BitmapFont. Once these objects are created,
		// the screen is then passed to the TitleScreen.
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		font = new BitmapFont();

		//TODO: Work on a solution for the window size.
		float screenWidth = Gdx.graphics.getWidth();
		float screenHeight = Gdx.graphics.getHeight();

		float targetWidth = 1280.0f; // Your desired game width
		float targetHeight = 720.0f; // Your desired game height

		camera = new OrthographicCamera(targetWidth, targetHeight);
		viewport = new ScalingViewport(Scaling.fit, targetWidth, targetHeight, camera);
		viewport.apply(true);

		setScreen(new TitleScreen(this));
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, true);
	}



	@Override
	public void dispose ()
	{
		batch.dispose();
		shapeRenderer.dispose();
		font.dispose();
	}

}
