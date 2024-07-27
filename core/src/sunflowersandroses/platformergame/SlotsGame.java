package sunflowersandroses.platformergame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;
import sunflowersandroses.platformergame.player.Player;

import java.util.HashMap;
import java.util.Random;

public class SlotsGame {
    private SpriteBatch spriteBatch;
    protected Texture texture;
    protected Texture backgroundTexture;
    protected TextureAtlas textureAtlas;
    protected Sprite sprite;
    protected Sprite backgroundSprite;
    final HashMap<String, Sprite> sprites = new HashMap<String, Sprite>();
    protected BitmapFont bitmapFont;
    Random random;
    protected OrthographicCamera camera;
    protected Viewport viewport;
    protected Stage stage;
    protected Skin skin;
    protected Table gameTable;
    protected TextButton cashInjection;
    protected TextButton spinButton;
    protected TypingLabel jackpotLabel;
    protected PlatformerGame game;
    protected Screen saveScreen;
    protected Player player;

    public SlotsGame(final PlatformerGame game, Player p)
    {
        spriteBatch = new SpriteBatch();
        texture = new Texture("debugSquare.png");
        backgroundTexture = new Texture("background.png");
        textureAtlas = new TextureAtlas("debugNumbers/debugNumbers.txt");
        sprite = new Sprite(texture);
        backgroundSprite = new Sprite(backgroundTexture);
        bitmapFont = new BitmapFont();
        random = new Random();
        this.game = game;
        saveScreen = game.getScreen();

        player = p;

        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        viewport = new FillViewport(1280,720);
        backgroundSprite.setBounds(viewport.getScreenX(), viewport.getScreenY(), viewport.getWorldWidth(), viewport.getWorldHeight());
        stage = new Stage(viewport);
        //Gdx.input.setInputProcessor(stage);

        gameTable = new Table();
        //gameTable.setFillParent(true);
        gameTable.setBounds(0,0,viewport.getWorldWidth(), viewport.getWorldHeight()/6f);
        stage.addActor(gameTable);
        stage.setDebugAll(false);

        jackpotLabel = new TypingLabel("", skin);
        jackpotLabel.setPosition(viewport.getWorldWidth() / 2f, viewport.getWorldHeight() / 3f);
        stage.addActor(jackpotLabel);

        TypingLabel titleLabel = new TypingLabel("Slots (currently in development)", skin, "title");
        gameTable.add(titleLabel).pad(10f).row();

        spinButton = new TextButton("Spin", skin);
        spinButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Put logic here
                spin();
                firstRun = false;
            }
        });
        spinButton.setSize(300f, 100f);
        gameTable.add(spinButton).pad(10f).row();

        TextButton back = new TextButton("Quit", skin);
        //back.setPosition(100,100);
        back.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Put logic here
                game.setScreen(saveScreen);
            }
        });
        back.setSize(100,50);
        stage.addActor(back);

        addSprites();

        cashInjection = new TextButton("Cash Injection", skin);
        cashInjection.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                cashInjection();
            }
        });
        cashInjection.setX(Gdx.graphics.getWidth() - cashInjection.getWidth());
        cashInjection.setVisible(false);
        stage.addActor(cashInjection);
        //gameTable.add(back).padRight(Gdx.graphics.getWidth()/2f);
    }

    private int jackpotMultiplier = 100;
    private String number0 = "Number 1";
    private String number1 = "Number 2";
    private String number2 = "Number 3";
    private int intNumber0;
    private int intNumber1;
    private int intNumber2;
    private int currentSpin = 0;
    boolean firstRun = true;

    /**
     * Logic for when the spin button is selected.
     */
    public void spin()
    {
        System.out.println(currentSpin);
        if (player.getMoney() > 0 || currentSpin <= 2) {
            if (currentSpin == 0 && firstRun)
            {
                player.setMoney(player.getMoney() - 10);
                firstRun = false;
            }
            if (currentSpin == 3) {
                if (player.getMoney() > 0) {
                    player.setMoney(player.getMoney() - 10);
                }
            }
            shuffle();
            number0 = String.valueOf(intNumber0);
            number1 = String.valueOf(intNumber1);
            number2 = String.valueOf(intNumber2);

            if (currentSpin == 2)
            {
                jackpotLabel.setAlignment(Align.center);
                jackpotLabel.setFontScale(2f * (viewport.getScreenWidth() / viewport.getWorldWidth()));
                jackpotLabel.setVisible(true);
                if ((intNumber0 == intNumber1) && (intNumber1 == intNumber2)) {
                    jackpotLabel.setText("{JUMP}{RAINBOW}JACKPOT!!!{ENDRAINBOW}");
                    jackpotLabel.setAlignment(Align.center);
                    jackpotLabel.setFontScale(5f * (viewport.getScreenWidth() / viewport.getWorldWidth()));
                    jackpotLabel.restart();
                    player.setMoney(player.getMoney() * jackpotMultiplier);
                } else if ((intNumber0 == intNumber1) || (intNumber1 == intNumber2)) {
                    jackpotLabel.setText("{WAVE}{GRADIENT=RED;BLUE;}Small Win!");
                    jackpotLabel.restart();
                    player.setMoney(player.getMoney() + 20);
                } else {
                    jackpotLabel.setText("{SICK}Awww... Try again?{ENDSICK}");
                    jackpotLabel.restart();
                }
            } else
            {
                jackpotLabel.setVisible(false);
            }

            currentSpin++;
            if (currentSpin >= 4)
            {
                currentSpin = 0;
            }
        }
    }

    public static int randomCap = 9;

    /**
     * Dedicated to choosing a number in the slot machine. randomCap is used to determine the highest number that can
     * be randomly selected.
     */
    public void shuffle()
    {
        switch (currentSpin)
        {
            case 0:
                intNumber0 = random.nextInt(randomCap) + 1;
                break;
            case 1:
                intNumber1 = random.nextInt(randomCap) + 1;
                break;
            case 2:
                intNumber2 = random.nextInt(randomCap) + 1;
                break;
            default:
                //lol
                break;
        }
    }

    /**
     * A random number generator dedicated to showing a random effect within the slot machine. This doesn't determine
     * what number is chosen.
     * @return
     */
    public int fakeShuffle()
    {
            return random.nextInt(9) + 1;
    }

    /**
     * Gives the player an extra set of cash.
     * for debugging purposes
     */
    public void cashInjection()
    {
        if (player.getMoney() <= 0)
        {
            player.setMoney(player.getMoney() + 100);
        }
    }

    /**
     * Adds sprites to a hashmap for use
     */
    private void addSprites()
    {
        Array<TextureAtlas.AtlasRegion> regions = textureAtlas.getRegions();

        for (TextureAtlas.AtlasRegion region : regions) {
            Sprite sprite = textureAtlas.createSprite(region.name);

            sprites.put(region.name, sprite);
        }
    }

    /**
     * Draws sprites on screen.
     * @param name Name for retrieval from the hashmap
     * @param x X coordinates
     * @param y Y coordinates
     * @param width Width of the sprite
     * @param height Height of the sprite
     */
    public void drawSprite(String name, float x, float y, float width, float height)
    {
        Sprite sprite = sprites.get(name);

        sprite.setBounds(x - (100f * viewport.getScreenWidth() / viewport.getWorldWidth()),
                y - (100f * viewport.getScreenHeight() / viewport.getWorldHeight()),
                width * (viewport.getScreenWidth() / viewport.getWorldWidth()),
                height * (viewport.getScreenHeight() / viewport.getWorldHeight()));

        sprite.draw(spriteBatch);
    }
    public void render(float delta)
    {
        update(delta);
        Gdx.gl.glClearColor(54/255f, 89/255f, 74/255f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (player.getMoney() <= 0 && currentSpin >= 3)
        {
            cashInjection.setVisible(true);
        }else
        {
            cashInjection.setVisible(false);
        }

        update(delta);
        stage.act(delta);
        stage.draw();
    }

    /**
     * drawNumbers uses drawSprite, and uses a switch case to display specific numbers.
     * @param number
     * @param spriteBatch
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public void drawNumbers(int number, SpriteBatch spriteBatch, float x, float y, float width, float height)
    {
        switch (number)
        {
            case 1:
                drawSprite("number1",x,y, width, height);
                break;
            case 2:
                drawSprite("number2",x,y, width, height);
                break;
            case 3:
                drawSprite("number3",x,y, width, height);
                break;
            case 4:
                drawSprite("number4",x,y, width, height);
                break;
            case 5:
                drawSprite("number5",x,y, width, height);
                break;
            case 6:
                drawSprite("number6",x,y, width, height);
                break;
            case 7:
                drawSprite("number7",x,y, width, height);
                break;
            case 8:
                drawSprite("number8",x,y, width, height);
                break;
            case 9:
                drawSprite("number9",x,y, width, height);
                break;
            default:
                drawSprite("number0",x,y, width, height);
                break;
        }
    }

    float spriteWidthAndHeight = 200;

    /**
     * Method handles what the player sees on screen.
     * @param spriteBatch
     */
    public void displaySlotNumbers(SpriteBatch spriteBatch)
    {
        switch (currentSpin) {
            case 0:
                drawNumbers(fakeShuffle(), spriteBatch, viewport.getScreenWidth() * (1 / 4f), viewport.getScreenHeight() / 2f, 200, 200);
                drawNumbers(fakeShuffle(), spriteBatch, viewport.getScreenWidth() * (1 / 2f), viewport.getScreenHeight() / 2f, 200, 200);
                drawNumbers(fakeShuffle(), spriteBatch, viewport.getScreenWidth() * (3 / 4f), viewport.getScreenHeight() / 2f, 200, 200);
                break;
            case 1:
                drawNumbers(intNumber0, spriteBatch, viewport.getScreenWidth() * (1 / 4f), viewport.getScreenHeight() / 2f, 200, 200);
                drawNumbers(fakeShuffle(), spriteBatch, viewport.getScreenWidth() * (1 / 2f), viewport.getScreenHeight() / 2f, 200, 200);
                drawNumbers(fakeShuffle(), spriteBatch, viewport.getScreenWidth() * (3 / 4f), viewport.getScreenHeight() / 2f, 200, 200);
                break;
            case 2:
                drawNumbers(intNumber0, spriteBatch, viewport.getScreenWidth() * (1 / 4f), viewport.getScreenHeight() / 2f, 200, 200);
                drawNumbers(intNumber1, spriteBatch, viewport.getScreenWidth() * (1 / 2f), viewport.getScreenHeight() / 2f, 200, 200);
                drawNumbers(fakeShuffle(), spriteBatch, viewport.getScreenWidth() * (3 / 4f), viewport.getScreenHeight() / 2f, 200, 200);
                break;
            case 3:
                drawNumbers(intNumber0, spriteBatch, viewport.getScreenWidth() * (1 / 4f), viewport.getScreenHeight() / 2f, 200, 200);
                drawNumbers(intNumber1, spriteBatch, viewport.getScreenWidth() * (1 / 2f), viewport.getScreenHeight() / 2f, 200, 200);
                drawNumbers(intNumber2, spriteBatch, viewport.getScreenWidth() * (3 / 4f), viewport.getScreenHeight() / 2f, 200, 200);
                break;
            default:
                //lol
                break;
        }
    }

    public void update(float delta)
    {
        if (currentSpin == 0)
        {
            spinButton.setText("Spin");
        } else if (currentSpin == 3)
        {
            spinButton.setText("Go Again?");
        } else
        {
            spinButton.setText("Stop");
        }
        sprite.setBounds(
                Gdx.graphics.getWidth()/2f,
                Gdx.graphics.getHeight()/2f,
                100,
                100);
        spriteBatch.begin();
        backgroundSprite.draw(spriteBatch);
        bitmapFont.draw(spriteBatch,String.valueOf(player.getMoney()),Gdx.graphics.getWidth() - 50f,Gdx.graphics.getHeight() - 50f);
        //textraLabel.draw(spriteBatch,1);
        displaySlotNumbers(spriteBatch);
        if ((intNumber0 == intNumber1) && (intNumber0 == intNumber2) && (intNumber0 != 0) && currentSpin == 3) {
            //bitmapFont.draw(spriteBatch, "JACKPOT", Gdx.graphics.getWidth() * (1 / 2f) - 25f, Gdx.graphics.getHeight() / 3f);
        }

        //sprite.draw(spriteBatch);
        //drawSprite("number1",100,100);
        spriteBatch.end();

        //bounds.setPosition(position.x, position.y);
    }

    public void dispose()
    {
        spriteBatch.dispose();
        texture.dispose();
    }

    // Getters and Setters //

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
