package cchase.platformergame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * GameScreen is the main platformer screen of the game.
 * TODO: Rename this class.
 */
public class GameScreen extends ScreenAdapter
{
    PlatformerGame  game;
    private SpriteBatch batch;
    World world;
    Player player;

    float x = 100f;
    float y = 300f;


    /**
     * Constructor to GameScreen. Accepts a PlatformerGame and assigns game, while creating a new SpriteBatch,
     * player and world.
     * @param game
     */
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
    public void hide()
    {
        GameState.gameScreen = game.getScreen();
    }

    /**
     * render Renders the images on screen. First by clearing the screen, then running render from different classes
     * In this method you will also find game objectives, such as touching the end goal or touching an enemy.
     * @param delta The time in seconds since the last render.
     */
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

        if (world.isCollidingWithEnemy())
        {
            GameState.gameScreen = game.getScreen();
            game.setScreen(new BattleScreen(game,player,world.enemy));
        }

        if (Gdx.input.isKeyPressed(Input.Keys.I))
        {
            GameState.gameScreen = game.getScreen();
            game.setScreen(new BattleScreen(game,player,world.enemy));
        }
    }

    @Override
    public void dispose()
    {
        batch.dispose();
        player.dispose();
        world.dispose();
    }
}
