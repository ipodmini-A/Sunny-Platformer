package cchase.platformergame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;

/**
 * Battle serves as the UI as well as controls the flow of battle.
 */
public class Battle {
    private Player player;
    private Enemy enemy;
    private Stage stage;
    private Skin skin;
    private Label playerStatusLabel;
    private Label enemyStatusLabel;
    private TextButton attackButton;
    private TextButton magicButton;
    private TextButton defendButton;
    private VerticalGroup movesGroup;
    private ScrollPane movesScrollPane;
    private boolean magicClicked;
    private SpriteBatch spriteBatch;
    private OrthographicCamera camera;
    enum Turn
    {
        PLAYER_TURN, ENEMY_TURN
    }
    private Turn turn;

    public Battle(final Player player, final Enemy enemy)
    {
        this.player = player;
        this.enemy = enemy;

        spriteBatch = new SpriteBatch();

        stage = new Stage();
        stage.setDebugAll(true);
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        // Player status label
        playerStatusLabel = new Label("Player HP: " + player.getHealth(), skin);
        playerStatusLabel.setPosition(20, Gdx.graphics.getHeight() - playerStatusLabel.getHeight() - 20);
        stage.addActor(playerStatusLabel);

        // Enemy status label
        enemyStatusLabel = new Label("Enemy HP: " + player.getHealth(), skin);
        enemyStatusLabel.setPosition((Gdx.graphics.getWidth() - enemy.getWidth()) - 250
                , Gdx.graphics.getHeight() - enemyStatusLabel.getHeight() - 20);
        stage.addActor(enemyStatusLabel);

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
        movesScrollPane.setSize(300, 200); // Background
        movesScrollPane.setPosition(Gdx.graphics.getWidth() - movesScrollPane.getWidth() - 20, 20);
        movesScrollPane.setVisible(false);

        // Button listeners
        // Attack logic
        attackButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // TODO: Implement attack logic
                if (turn.equals(Turn.PLAYER_TURN))
                {
                    schedulePlayerTurn(2); // Delay in seconds for player's attack (adjust as needed)
                }
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
        stage.addActor(defendButton );
        stage.addActor(magicButton);
        stage.addActor(movesScrollPane);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f, 0); // Set the camera position to the center of the screen
        camera.update();
        player.updateCamera(camera);
        enemy.updateCamera(camera);

        /*
        Setting players position
         */
        player.setPositionX(Gdx.graphics.getWidth() / 60f);
        System.out.println("X Position set to: " + player.getPosition().x);
        player.setPositionY(Gdx.graphics.getHeight() / 2f);
        System.out.println("Y Position set to: " + player.getPosition().y);

        enemy.setPositionX((Gdx.graphics.getWidth() - enemy.getWidth()) - 250 );
        System.out.println("X Position set to: " + enemy.getPosition().x);
        enemy.setPositionY(Gdx.graphics.getHeight() / 2f);
        System.out.println("Y Position set to: " + enemy.getPosition().y);

        //Turn set
        turn = Turn.PLAYER_TURN;
    }

    float baseWidth = 1280f;
    float baseHeight = 720f;
    public void render(float delta)
    {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();

        float referenceScreenWidth = 1280;  // The reference screen width for scaling
        float scale = 3 * (Gdx.graphics.getWidth() / referenceScreenWidth);

        player.renderBattle(spriteBatch, delta,scale);
        enemy.renderBattle(spriteBatch, delta, scale);
        turnManager();
    }

    public void turnManager()
    {
        if (turn == Turn.ENEMY_TURN)
        {
            enemyTurn();
            turn = Turn.PLAYER_TURN;
        }else if (turn == Turn.PLAYER_TURN)
        {

        }
    }

    public void dispose()
    {
        stage.dispose();
    }

    /**
     * Allows the magic moves to be toggled.
     */
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

    public void playerTurn()
    {
        playerAttackOccurred();
    }

    public void enemyTurn()
    {
        enemyAttackOccurred();
    }

    /**
     * scedulePlayerTurn starts the players turn with a delay so that it's not done instantly
     * @param delay how long to delay the turn
     */
    private void schedulePlayerTurn(float delay) {
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                // Code to execute after the delay
                playerAttackOccurred();
                scheduleEnemyTurn(2); // Delay in seconds for enemy's turn (adjust as needed)
            }
        }, delay);
    }

    /**
     * scheduleEnemyTurn starts the enemy turn with a delay so that it's not done instantly
     * @param delay how long to delay the turn
     */
    private void scheduleEnemyTurn(float delay) {
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                // Code to execute after the delay
                turn = Turn.ENEMY_TURN;
                enemyAttackOccurred();
            }
        }, delay);
    }

    public void playerAttackOccurred()
    {

        BattleCalculation.damageCalculation(player,enemy);
        // This is here to update the players health every time this method is called.
        playerStatusLabel.setText("Player HP: " + player.getHealth());
        enemyStatusLabel.setText("Enemy HP: " + enemy.getHealth());
    }

    public void enemyAttackOccurred()
    {
        BattleCalculation.damageCalculation(enemy, player);
        playerStatusLabel.setText("Player HP: " + player.getHealth());
        enemyStatusLabel.setText("Enemy HP: " + enemy.getHealth());
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









