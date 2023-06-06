package cchase.platformergame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class GameScreen extends ScreenAdapter
{
    PlatformerGame  game;
    private SpriteBatch batch;
    World world;
    Player player;

    float x = 100f;
    float y = 300f;



    public GameScreen(PlatformerGame game)
    {
        this.game = game;
        batch = new SpriteBatch();
        player = new Player(x,y);

        world = new World(player);
    }

    @Override
    public void show()
    {
        super.show();
    }

    @Override
    public void render(float delta)
    {
        Gdx.gl.glClearColor(0.0f, 0.1f, 0.2f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        world.WorldUpdate(player); // Removing this results in a null crash. idk
        world.render(delta);
        if (world.isTouchingEndGoal())
        {
            game.setScreen(new EndScreen(game));
        }

        //player.render(batch,delta); // Render the player

    }

    @Override
    public void dispose()
    {
        batch.dispose();
        player.dispose();
        world.dispose();
    }
}
