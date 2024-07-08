package cchase.platformergame.screens;

import cchase.platformergame.*;
import cchase.platformergame.console.ConsoleCommands;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;

/**
 * GameScreen is the main platformer screen of the game.
 * TODO: Rename this class.
 */
public class GameScreen extends ScreenAdapter
{
    protected PlatformerGame game;
    private Stage stage;
    private SpriteBatch batch;
    protected World world;
    protected Player player;

    float x = 400f;
    float y = 1300f;
    private boolean firstSpawnCheck = false;
    public static InputMultiplexer multiplexer = new InputMultiplexer();
    private final NewPlatformerInput newPlatformerInput;

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
        stage = new Stage();
        batch = new SpriteBatch();
        player = new Player();

        multiplexer = new InputMultiplexer();
        newPlatformerInput = new NewPlatformerInput(player);
        // For some reason, when I add newPlatformerInput to the multiplexer before the console commands, it causes the
        // console commands to break. Not sure why but if you want a functional console on a specific screen, add it
        // to the multiplexer before everything else to ensure it doesn't get overridden.
        multiplexer.addProcessor(ConsoleCommands.getConsole().getInputProcessor());
        multiplexer.addProcessor(newPlatformerInput);
        Gdx.input.setInputProcessor(multiplexer);
        world = new World(player,game);
    }

    @Override
    public void show()
    {
        Gdx.input.setInputProcessor(multiplexer);
        player.setDisableControls(false);
        if (firstSpawnCheck)
        {
            player.setPositionX(GameState.lastRecordedPlayerX);
            player.setPositionY(GameState.lastRecordedPlayerY);
        }

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
        GameState.lastRecordedPlayerX = player.getPosition().x;
        System.out.println(GameState.lastRecordedPlayerX);
        GameState.lastRecordedPlayerY = player.getPosition().y;
        System.out.println(GameState.lastRecordedPlayerY);
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

        ConsoleCommands.draw(); // For some reason enter doesn't work.
    }

    @Override
    public void dispose()
    {
        batch.dispose();
        player.dispose();
        world.dispose();
    }
}
