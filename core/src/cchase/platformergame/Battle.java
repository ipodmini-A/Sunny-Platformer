package cchase.platformergame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

/**
 * Battle serves as the UI as well as controls the flow of battle.
 */
public class Battle {
    private Player player;
    private Enemy enemy;
    private Stage stage;
    private Skin skin;
    private Label playerStatusLabel;
    private TextButton attackButton;
    private TextButton magicButton;
    private TextButton defendButton;
    private VerticalGroup movesGroup;
    private ScrollPane movesScrollPane;
    private boolean magicClicked;
    private SpriteBatch spriteBatch;
    private OrthographicCamera camera;

    public Battle(Player player, Enemy enemy)
    {
        this.player = player;
        this.enemy = enemy;

        player.getPosition().x = 20;
        player.getPosition().y = 300;

        spriteBatch = new SpriteBatch();

        stage = new Stage();
        stage.setDebugAll(true);
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        // Player status label
        playerStatusLabel = new Label("Player HP: " + player.getHealth(), skin);
        playerStatusLabel.setPosition(20, Gdx.graphics.getHeight() - playerStatusLabel.getHeight() - 20);
        stage.addActor(playerStatusLabel);

        // Buttons
        attackButton = new TextButton("Attack", skin);
        defendButton = new TextButton("Defend", skin);
        magicButton = new TextButton("Magic", skin);

        attackButton.setPosition(20, 140);
        defendButton.setPosition(20, 80);
        magicButton.setPosition(20, 20);

        // Moves list
        movesGroup = new VerticalGroup();
        movesScrollPane = new ScrollPane(movesGroup, skin);
        movesScrollPane.setSize(200, 300); // Background
        movesScrollPane.setPosition(Gdx.graphics.getWidth() - movesScrollPane.getWidth() - 20, 20);
        movesScrollPane.setVisible(false);

        // Button listeners
        attackButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // TODO: Implement attack logic
            }
        });

        magicButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toggleMagicMoves();
            }
        });

        defendButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // TODO: Implement defend logic
            }
        });

        stage.addActor(attackButton);
        stage.addActor(defendButton);
        stage.addActor(magicButton);
        stage.addActor(movesScrollPane);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.update();
    }

    public void render(float delta)
    {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();

        player.render(spriteBatch,delta);
    }

    public void dispose()
    {
        stage.dispose();
    }

    private void toggleMagicMoves()
    {
        magicClicked = !magicClicked;

        if (magicClicked)
        {
            showMovesList("Magic");
        } else
        {
            hideMovesList();
        }
    }

    private void showMovesList(String category)
    {
        movesGroup.clear();
        movesGroup.align(Align.left);
        movesGroup.space(10);
        movesGroup.padLeft(10);

        // Simulating moves based on category
        if (category.equals("Magic")) {
            TextButton magic1Button = new TextButton("Magic 1", skin);
            TextButton magic2Button = new TextButton("Magic 2", skin);
            TextButton magic3Button = new TextButton("Magic 3", skin);

            movesGroup.addActor(magic1Button);
            movesGroup.addActor(magic2Button);
            movesGroup.addActor(magic3Button);

            magic1Button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    // TODO: Implement magic 1 logic
                }
            });

            magic2Button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    // TODO: Implement magic 2 logic
                }
            });

            magic3Button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    // TODO: Implement magic 3 logic
                }
            });
        }

        movesScrollPane.setVisible(true);
    }

    private void hideMovesList() {
        movesScrollPane.setVisible(false);
    }
}









