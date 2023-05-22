package cchase.platformergame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameScreen extends ScreenAdapter
{
    PlatformerGame  game;
    PlatformerInput input = new PlatformerInput();

    private SpriteBatch batch;
    private Texture image;
    World world;

    float x = 100f;
    float y = 100f;


    public GameScreen(PlatformerGame game)
    {
        this.game = game;
        batch = new SpriteBatch();
        image = new Texture(Gdx.files.internal("debugSquare.png"));
        world = new World();

    }

    @Override
    public void show()
    {
        super.show();
    }

    @Override
    public void render(float delta)
    {
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Begin the sprite batch
        batch.begin();

        if (input.isUpPressed())
        {
            y = y + 1;
        }

        if (input.isDownPressed())
        {
            y = y - 1;
        }

        if (input.isLeftPressed())
        {
            x = x - 1;
        }

        if (input.isRightPressed())
        {
            x = x + 1;
        }

        batch.draw(image, x, y);

        // End the sprite batch
        batch.end();
        world.render();

        input.update();

    }

    @Override
    public void dispose()
    {
        batch.dispose();
        image.dispose();
    }
}
