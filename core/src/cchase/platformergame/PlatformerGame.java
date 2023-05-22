package cchase.platformergame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

public class PlatformerGame extends Game
{
	SpriteBatch batch;
	ShapeRenderer shapeRenderer;
	BitmapFont font;
	OrthographicCamera camera;


	@Override
	public void create()
	{
		// create() makes a new SpriteBatch, ShapeRenderer and BitmapFont. Once these objects are created,
		// the screen is then passed to the TitleScreen.
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		font = new BitmapFont();
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getWidth());
		setScreen(new TitleScreen(this));
	}

	@Override
	public void dispose ()
	{
		batch.dispose();
		shapeRenderer.dispose();
		font.dispose();
	}

}
