package sunflowersandroses.platformergame;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import sunflowersandroses.platformergame.console.ConsoleCommands;
import sunflowersandroses.platformergame.items.Item;
import sunflowersandroses.platformergame.player.Player;
import sunflowersandroses.platformergame.screens.EndScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
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
            newPlatformerInput = new NewPlatformerInput(LevelManager.player);
            // For some reason, when I add newPlatformerInput to the multiplexer before the console commands, it causes the
            // console commands to break. Not sure why but if you want a functional console on a specific screen, add it
            // to the multiplexer before everything else to ensure it doesn't get overridden.
            multiplexer.addProcessor(ConsoleCommands.getConsole().getInputProcessor());
            multiplexer.addProcessor(newPlatformerInput);
            Gdx.input.setInputProcessor(multiplexer);
            System.out.println("New input created");

            currentLevel = new World(LevelManager.player, LevelManager.game, levelData.mapPath);
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
        LinkedList<Enemy> enemyList = new LinkedList<>();
        try {
            JsonReader jsonReader = new JsonReader();
            JsonValue jsonData = jsonReader.parse(Gdx.files.internal(filePath));

            int mapHeight = currentLevel.getMapProperties().get("height", Integer.class) * 32;

            for (JsonValue enemyData : jsonData.get("enemies")) {
                int id = enemyData.getInt("id");
                float xPosition = enemyData.getFloat("x");
                float yPosition = enemyData.getFloat("y");

                Enemy loadingEnemy = new Enemy(xPosition, (mapHeight - yPosition));
                loadingEnemy.setId(id);
                enemyList.add(loadingEnemy);
            }
        } catch (Exception e)
        {
            System.err.println(e.getMessage());
            System.err.println("Problems reading file: " + filePath + " Check message above.");
        }
        return enemyList;
    }

    public static LinkedList<NonPlayableCharacter> loadNPCs(String filePath) {
        LinkedList<NonPlayableCharacter> NPCList = new LinkedList<>();
        try {
            JsonReader jsonReader = new JsonReader();
            JsonValue jsonData = jsonReader.parse(Gdx.files.internal(filePath));

            int mapHeight = currentLevel.getMapProperties().get("height", Integer.class) * 32;

            for (JsonValue npcData : jsonData.get("npcs")) {
                int id = npcData.getInt("id");
                float xPosition = npcData.getFloat("x");
                float yPosition = npcData.getFloat("y");

                NonPlayableCharacter loadingNPC = new NonPlayableCharacter(xPosition, (mapHeight - yPosition));
                loadingNPC.dialogue.setMessageListID(id);
                NPCList.add(loadingNPC);
            }
        } catch (Exception e)
        {
            System.err.println(e.getMessage());
            System.err.println("Problems reading file: " + filePath + " Check message above.");
        }
        return NPCList;
    }

    public static LinkedList<Item> loadItems(String filePath)
    {
        LinkedList<Item> itemList = new LinkedList<>();
        try {
            JsonReader jsonReader = new JsonReader();
            JsonValue jsonData = jsonReader.parse(Gdx.files.internal(filePath));

            int mapHeight = currentLevel.getMapProperties().get("height", Integer.class) * 32;

            for (JsonValue itemData : jsonData.get("items")) {
                int id = itemData.getInt("id");
                float xPosition = itemData.getFloat("x");
                float yPosition = itemData.getFloat("y");

                Item loadingItem = Item.itemSelector(id, xPosition, (mapHeight - yPosition));
                itemList.add(loadingItem);
            }
        } catch (Exception e)
        {
            System.err.println(e.getMessage());
            System.err.println("Problems reading file: " + filePath + " Check message above.");
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
            currentLevel.setPlayerReachedEnd(false);
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
