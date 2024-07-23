package sunflowersandroses.platformergame;

import sunflowersandroses.platformergame.console.ConsoleCommands;
import sunflowersandroses.platformergame.screens.EndScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

public class LevelManager {
    private static World currentLevel;
    private static Player player;
    public static InputMultiplexer multiplexer;
    private static NewPlatformerInput newPlatformerInput;
    private static PlatformerGame game;
    private static int currentLevelNumber;
    public int amountOfLevels;
    private static HashMap<Integer, LevelData> levelDataMap;

    private static class LevelDataList {
        public Array<LevelData> levels;
    }

    private static class LevelData {
        public int number;
        public String mapPath;
        public String enemyDataPath;
        public String npcDataPath;
        public String itemDataPath;
        public int nextLevel;
    }

    public LevelManager() {
        loadLevelData();
    }

    private void loadLevelData() {
        Json json = new Json();
        LevelDataList levelDataList = json.fromJson(LevelDataList.class, Gdx.files.internal("levels/levels.json"));
        amountOfLevels = levelDataList.levels.size;
        levelDataMap = new HashMap<>();
        for (LevelData levelData : levelDataList.levels) {
            levelDataMap.put(levelData.number, levelData);
        }
    }

    public void loadLevel(Player player, PlatformerGame game, int levelNumber) {
        if (currentLevel != null) {
            currentLevel.dispose();
        }

        LevelManager.game = game;
        LevelManager.player = player;
        currentLevelNumber = levelNumber;

        LevelData levelData = levelDataMap.get(levelNumber);
        if (levelData != null) {
            multiplexer = new InputMultiplexer();
            newPlatformerInput = new NewPlatformerInput(this.player);
            // For some reason, when I add newPlatformerInput to the multiplexer before the console commands, it causes the
            // console commands to break. Not sure why but if you want a functional console on a specific screen, add it
            // to the multiplexer before everything else to ensure it doesn't get overridden.
            multiplexer.addProcessor(ConsoleCommands.getConsole().getInputProcessor());
            multiplexer.addProcessor(newPlatformerInput);
            Gdx.input.setInputProcessor(multiplexer);
            System.out.println("New input created");

            currentLevel = new World(this.player, this.game, levelData.mapPath);
            currentLevel.loadEnemies(loadEnemies(levelData.enemyDataPath));
            currentLevel.loadNPCs(loadNPCs(levelData.npcDataPath));
            currentLevel.loadItems(loadItems(levelData.itemDataPath));
            show();
        } else {
            throw new IllegalArgumentException("Invalid level number: " + levelNumber);
        }
    }

    public static void loadLevel(int levelNumber) {
        if (currentLevel != null) {
            currentLevel.dispose();
        }

        currentLevelNumber = levelNumber;

        LevelData levelData = levelDataMap.get(levelNumber);
        if (levelData != null) {
            multiplexer = new InputMultiplexer();
            newPlatformerInput = new NewPlatformerInput(player);
            // For some reason, when I add newPlatformerInput to the multiplexer before the console commands, it causes the
            // console commands to break. Not sure why but if you want a functional console on a specific screen, add it
            // to the multiplexer before everything else to ensure it doesn't get overridden.
            multiplexer.addProcessor(ConsoleCommands.getConsole().getInputProcessor());
            multiplexer.addProcessor(newPlatformerInput);
            Gdx.input.setInputProcessor(multiplexer);
            System.out.println("New input created");

            currentLevel = new World(player, game, levelData.mapPath);
            currentLevel.loadEnemies(loadEnemies(levelData.enemyDataPath));
            currentLevel.loadNPCs(loadNPCs(levelData.npcDataPath));
            currentLevel.loadItems(loadItems(levelData.itemDataPath));
            show();
        } else {
            throw new IllegalArgumentException("Invalid level number: " + levelNumber);
        }
    }

    public static LinkedList<Enemy> loadEnemies(String filePath)
    {
        LinkedList<Enemy> enemiesList = new LinkedList<>();
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

                Enemy loadingEnemy = new Enemy(xPosition, (mapHeight - yPosition));
                loadingEnemy.setId(id);
                enemiesList.add(loadingEnemy);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return enemiesList;
    }

    public static LinkedList<NonPlayableCharacter> loadNPCs(String filePath)
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

    public static LinkedList<Item> loadItems(String filePath)
    {
        LinkedList<Item> itemList = new LinkedList<>();
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

                Item loadingItem = Item.itemSelector(id,xPosition,(mapHeight - yPosition));
                itemList.add(loadingItem);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return itemList;
    }

    private void transitionToNextLevel() {
        LevelData currentLevelData = levelDataMap.get(currentLevelNumber);
        if (currentLevelData != null && currentLevelData.nextLevel != -1 && (currentLevelNumber < amountOfLevels)) {
            loadLevel(player, game, currentLevelData.nextLevel);
        } else {
            // Handle end of game or loop back to the first level
            System.out.println("End of levels or undefined next level.");
        }
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
            float delta2 = Math.min(delta, 1 / 30f);
            accumulator += delta2;

            while (accumulator >= TIME_STEP) {
                currentLevel.render(TIME_STEP);
                accumulator -= TIME_STEP;
            }

            if (currentLevel.isPlayerReachedEnd()) {
                transitionToNextLevel();
            }
        }
        gameOverCheck();
    }

    public void gameOverCheck()
    {
        if (player.getHealth() <= 0 || player.getPosition().y <= -500f)
        {
            game.setScreen(new EndScreen(game, false));
        }
    }

    public static void show()
    {
        Gdx.input.setInputProcessor(multiplexer); // Dont do this... Currently this is just here because there is no (show)

    }

    public World getCurrentLevel() {
        return currentLevel;
    }
}
