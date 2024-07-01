package cchase.platformergame.screens;

import cchase.platformergame.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * GameScreen is the main platformer screen of the game.
 * TODO: Rename this class.
 */
public class GameScreen extends ScreenAdapter
{
    protected PlatformerGame game;
    private SpriteBatch batch;
    protected World world;
    protected Player player;

    float x = 400f;
    float y = 1300f;
    private boolean firstSpawnCheck = false;
    public boolean playerWon = false;


    /**
     * Constructor to GameScreen. Accepts a PlatformerGame and assigns game, while creating a new SpriteBatch,
     * player and world.
     * @param game
     */
    public GameScreen(PlatformerGame game)
    {
        System.out.println("GameScreen created");
        this.game = game;
        batch = new SpriteBatch();
        player = new Player();
        //Gdx.input.setInputProcessor(inputProcessor);
        world = new World(player);
    }

    @Override
    public void show()
    {
        player.setDisableControls(false);
        if (firstSpawnCheck)
        {
            player.setPositionX(GameState.lastRecordedPlayerX);
            player.setPositionY(GameState.lastRecordedPlayerY);
        }
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(new NewPlatformerInput(player));
        Gdx.input.setInputProcessor(multiplexer);

        // Resetting players movement
        player.setDownMove(false);
        player.setLeftMove(false);
        player.setRightMove(false);

        // Resetting players velocity
        player.setVelocity(0,0);

        world.music.play();

        firstSpawnCheck = true;
        super.show();
    }

    @Override
    public void hide()
    {
        GameState.gameScreen = game.getScreen();
        System.out.println("Hidden");
    }

    /**
     * render Renders the images on screen. First by clearing the screen, then running render from different classes
     * In this method you will also find game objectives, such as touching the end goal or touching an enemy.
     * @param delta The time in seconds since the last render.
     */
    @Override
    public void render(float delta)
    {

        world.WorldUpdate(player); // Removing this results in a null crash. idk
        world.render(delta);
        if (world.isTouchingEndGoal())
        {
            playerWon = true;
            game.setScreen(new EndScreen(game, playerWon));
        }

        if (player.menuPressed())
        {
            game.setScreen(new TitleScreen(game));
        }

        /*
        Following code stores the state of the game screen and the player.
        TODO: Put into a method
         */
        // The code below is commented out for now. I am testing out normal platform attacks instead of RPG battle mechanics
        /*
        if (world.isCollidingWithEnemy())
        {
            GameState.gameScreen = game.getScreen();
            player.setDisableControls(true);
            GameState.lastRecordedPlayerX = player.getPosition().x;
            System.out.println(GameState.lastRecordedPlayerX);
            GameState.lastRecordedPlayerY = player.getPosition().y;
            System.out.println(GameState.lastRecordedPlayerY);
            world.music.pause();
            game.setScreen(new BattleScreen(game,player,world.enemy));
        }
         */

        if (Gdx.input.isKeyPressed(Input.Keys.I))
        {
            GameState.gameScreen = game.getScreen();
            player.setDisableControls(true);
            GameState.lastRecordedPlayerX = player.getPosition().x;
            System.out.println(GameState.lastRecordedPlayerX);
            GameState.lastRecordedPlayerY = player.getPosition().y;
            System.out.println(GameState.lastRecordedPlayerY);
            world.music.pause();
            //game.setScreen(new BattleScreen(game,player,world.enemy));
        }

        if (player.getHealth() <= 0)
        {

            game.setScreen(new EndScreen(game, playerWon));
        }

        //System.out.println(player.isTouchingWall());
        //System.out.println(player.isTouchingWall());
    }

    @Override
    public void dispose()
    {
        batch.dispose();
        player.dispose();
        world.dispose();
    }
}
