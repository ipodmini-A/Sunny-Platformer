package cchase.platformergame;

import cchase.platformergame.console.ConsoleCommands;
import cchase.platformergame.screens.EndScreen;
import cchase.platformergame.screens.GameScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
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
        LinkedList<Enemy> level1Enemies = loadEnemies("enemiesData/enemiesLevel1.csv");
        currentLevel.loadEnemies(level1Enemies);

        LinkedList<NonPlayableCharacter> level1NPCs = loadNPCs("nonPlayableCharactersData/nonPlayableCharactersLevel1.csv");
        currentLevel.loadNPCs(level1NPCs);
    }

    public LinkedList<Enemy> loadEnemies(String filePath)
    {
        LinkedList<Enemy> enemiesList = new LinkedList<>();
        try (
                FileReader reader = new FileReader(filePath);
                CSVParser csvParser = CSVFormat.Builder.create()
                        .setHeader("x", "y")
                        .setSkipHeaderRecord(true)
                        .build()
                        .parse(reader)
        ) {
            for (CSVRecord csvRecord : csvParser) {
                float xPosition = Integer.parseInt(csvRecord.get("x"));
                float yPosition = Integer.parseInt(csvRecord.get("y"));

                // Height yeilds the actual tiles in the level, i.e. 50, and it is multiplied by 32 due to how wide each
                // tile is
                int mapHeight = currentLevel.getMapProperties().get("height",Integer.class) * 32;
                //System.out.println(mapHeight);

                enemiesList.add(new Enemy(xPosition, (mapHeight - yPosition)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return enemiesList;
    }
    public LinkedList<NonPlayableCharacter> loadNPCs(String filePath)
    {
        LinkedList<NonPlayableCharacter> NPCList = new LinkedList<>();
        try (
                FileReader reader = new FileReader(filePath);
                CSVParser csvParser = CSVFormat.Builder.create()
                        .setHeader("id","x", "y")
                        .setSkipHeaderRecord(true)
                        .build()
                        .parse(reader)
        ) {
            for (CSVRecord csvRecord : csvParser) {
                int id = Integer.parseInt(csvRecord.get("id"));
                float xPosition = Integer.parseInt(csvRecord.get("x"));
                float yPosition = Integer.parseInt(csvRecord.get("y"));

                // Height yeilds the actual tiles in the level, i.e. 50, and it is multiplied by 32 due to how wide each
                // tile is
                int mapHeight = currentLevel.getMapProperties().get("height",Integer.class) * 32;
                //System.out.println(mapHeight);

                NonPlayableCharacter loadingNPC = new NonPlayableCharacter(xPosition, (mapHeight - yPosition));
                loadingNPC.setMessageList(id);
                NPCList.add(loadingNPC);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return NPCList;
    }

    private static final float TIME_STEP = 1 / 60f;
    private float accumulator = 0f;

    /**
     * Renders the level
     * Currently is being slowly worked on. The idea is to have one consistant number accross the whole game,
     * being the TIME_STEP (1/60f). The game works as intended around 60fps.
     *
     * Current bugs known: When setting the FPS to 120, it causes strange behavior. Changing 1/30f to something slightly
     * higher resolves it, but it comes at the cost of less smooth animation
     * @param delta
     */
    public void render(float delta) {
        currentLevel.worldUpdate(player);// Attempts to remove worldUpdate causes issues. I'm not really sure why.
        if (currentLevel != null) {

            float delta2 = Math.min(delta, 1/30f);
            accumulator += delta2;

            while (accumulator >= TIME_STEP) {
                currentLevel.render(TIME_STEP);
                accumulator -= TIME_STEP;
            }

            //currentLevel.render(delta);
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
