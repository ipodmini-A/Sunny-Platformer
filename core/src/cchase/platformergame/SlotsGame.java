package cchase.platformergame;

import cchase.platformergame.screens.GameScreen;
import cchase.platformergame.screens.MainMenuScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;

import java.util.Random;

public class SlotsGame {
    private SpriteBatch spriteBatch;
    protected Texture texture;
    protected Sprite sprite;
    protected BitmapFont bitmapFont;
    Random random;
    protected OrthographicCamera camera;
    protected Stage stage;
    protected Skin skin;
    protected Table gameTable;
    TextButton cashInjection;
    PlatformerGame game;
    Screen saveScreen;
    Player player;
    public SlotsGame(final PlatformerGame game, Player p)
    {
        spriteBatch = new SpriteBatch();
        texture = new Texture("debugSquare.png");
        sprite = new Sprite(texture);
        bitmapFont = new BitmapFont();
        random = new Random();
        this.game = game;
        saveScreen = game.getScreen();

        player = p;

        stage = new Stage();
        //Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        gameTable = new Table();
        //gameTable.setFillParent(true);
        gameTable.setBounds(0,0,Gdx.graphics.getWidth(), Gdx.graphics.getHeight()/6f);
        stage.addActor(gameTable);
        stage.setDebugAll(true);

        Label titleLabel = new Label("Slots (currently in development)", skin, "title");
        gameTable.add(titleLabel).pad(10f).row();

        TextButton playButton = new TextButton("Spin", skin);
        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Put logic here
                spin();
            }
        });
        playButton.setSize(300f, 100f);
        gameTable.add(playButton).pad(10f).row();

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

    private int jackpotMultiplier = 10;
    private String number0 = "Number 1";
    private String number1 = "Number 2";
    private String number2 = "Number 3";
    public void spin()
    {
        if (player.getMoney() > 0) {
            player.setMoney(player.getMoney() - 10);
            int intNumber0 = random.nextInt(7);
            int intNumber1 = random.nextInt(7);
            int intNumber2 = random.nextInt(10);
            number0 = String.valueOf(intNumber0);
            number1 = String.valueOf(intNumber1);
            number2 = String.valueOf(intNumber2);
            if ((intNumber0 == intNumber1) && (intNumber1 == intNumber2)) {
                player.setMoney(player.getMoney() * jackpotMultiplier);
            } else if ((intNumber0 == intNumber1) || (intNumber1 == intNumber2))
            {
                player.setMoney(player.getMoney() + 20);
            }
        }
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
    public void render(float delta)
    {
        update(delta);
        Gdx.gl.glClearColor(54/255f, 89/255f, 74/255f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (player.getMoney() <= 0)
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

    public void displaySlotNumbers(SpriteBatch spriteBatch)
    {
        bitmapFont.draw(spriteBatch,number0,Gdx.graphics.getWidth()*(1/4f),Gdx.graphics.getHeight()/2f);
        bitmapFont.draw(spriteBatch,number1,Gdx.graphics.getWidth()/2f, Gdx.graphics.getHeight()/2f);
        bitmapFont.draw(spriteBatch,number2,Gdx.graphics.getWidth()*(3/4f), Gdx.graphics.getHeight()/2f);
    }

    public void update(float delta)
    {

        sprite.setBounds(
                Gdx.graphics.getWidth()/2f,
                Gdx.graphics.getHeight()/2f,
                100,
                100);
        spriteBatch.begin();
        bitmapFont.draw(spriteBatch,String.valueOf(player.getMoney()),Gdx.graphics.getWidth() - 50f,Gdx.graphics.getHeight() - 50f);
        displaySlotNumbers(spriteBatch);
        //sprite.draw(spriteBatch);
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
