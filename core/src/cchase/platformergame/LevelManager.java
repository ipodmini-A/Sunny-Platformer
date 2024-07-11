package cchase.platformergame;

import cchase.platformergame.console.ConsoleCommands;
import cchase.platformergame.screens.EndScreen;
import cchase.platformergame.screens.GameScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;

import java.util.LinkedList;

public class LevelManager {
    private World currentLevel;
    private Player player;
    public static InputMultiplexer multiplexer = new InputMultiplexer();
    private NewPlatformerInput newPlatformerInput;
    PlatformerGame game;

    public void loadLevel(Player player, PlatformerGame game, String mapPath) {
        if (currentLevel != null) {
            currentLevel.dispose();
        }

        multiplexer = new InputMultiplexer();
        newPlatformerInput = new NewPlatformerInput(player);
        // For some reason, when I add newPlatformerInput to the multiplexer before the console commands, it causes the
        // console commands to break. Not sure why but if you want a functional console on a specific screen, add it
        // to the multiplexer before everything else to ensure it doesn't get overridden.
        multiplexer.addProcessor(ConsoleCommands.getConsole().getInputProcessor());
        multiplexer.addProcessor(newPlatformerInput);
        Gdx.input.setInputProcessor(multiplexer);

        this.game = game;
        this.player = player;

        currentLevel = new World(this.player, this.game, mapPath);
        // Time for a CSV file... :(
        LinkedList<Enemy> level1Enemies;
        level1Enemies = new LinkedList<>();
        level1Enemies.add(new Enemy(player.getPosition().x + 300, player.getPosition().y));
        level1Enemies.add(new Enemy(player.getPosition().x + 400, player.getPosition().y));
        currentLevel.loadEnemies(level1Enemies);

        LinkedList<NonPlayableCharacter> level1NPCs = new LinkedList<>();
        level1NPCs.add(new NonPlayableCharacter(player.getPosition().x + 500, player.getPosition().y));
        level1NPCs.add(new NonPlayableCharacter(player.getPosition().x + 50, player.getPosition().y));
        level1NPCs.get(1).setMessageList(1);
        level1NPCs.add(new NonPlayableCharacter(player.getPosition().x - 200, player.getPosition().y));
        level1NPCs.get(2).setMessageList(2);
        level1NPCs.add(new NonPlayableCharacter(player.getPosition().x - 200, player.getPosition().y - 300));
        level1NPCs.get(3).setMessageList(3);
        currentLevel.loadNPCs(level1NPCs);
    }

    public void render(float delta) {
        currentLevel.worldUpdate(player);// Attempts to remove worldUpdate causes issues. I'm not really sure why.
        if (currentLevel != null) {
            currentLevel.render(delta);
            if (currentLevel.isPlayerReachedEnd())
            {

                currentLevel = new World(player,game,"test2.tmx");
                Gdx.input.setInputProcessor(multiplexer);
                //game.setScreen(new EndScreen(game, true));
            }

        }
    }

    public void show()
    {
        Gdx.input.setInputProcessor(multiplexer); // Dont do this... Currently this is just here because there is no (show)

    }

    public World getCurrentLevel() {
        return currentLevel;
    }
}
