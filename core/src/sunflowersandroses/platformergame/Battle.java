package sunflowersandroses.platformergame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
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
 * Battle.java
 * Battle serves as the UI as well as controls the flow of battle
 * The goal is to have something that looks similar to a traditional RPG battle system, such as Dragon Quest.
 * Current Bugs: Pressing attack multiple times causes a overload which causes the game to think you won? This bug is crazy
 * - Possible fix: Remove the menu when attack, defend or magic attack is clicked.
 * TODO: Implement Magic system (requires some work)
 * @deprecated
 */
public class Battle {
    private Player player;
    private Enemy enemy;
    private Stage stage;
    private Stage actorsHealth;
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
    private boolean battleOccuring;
    private boolean playerTurn;
    private boolean enemyTurn;
    enum Turn
    {
        PLAYER_TURN, ENEMY_TURN
    }
    enum TypeOfAttack
    {
        ATTACK, DEFENSE, MAGIC
    }
    private Turn turn;
    private TypeOfAttack typeOfAttack;
    public Music music;

    /**
     * Constructor to Battle. A new stage is created with labels and buttons.
     * Listeners are declared here.
     * @param player the player character
     * @param enemy the enemy
     */
    public Battle(final Player player, final Enemy enemy)
    {
        this.player = player;
        this.enemy = enemy;

        battleOccuring = false;

        // New Sprite Batch so that the players and the enemies are fixed on screen and not using the worlds sprite batch.
        spriteBatch = new SpriteBatch();

        // Stage creation
        stage = new Stage();
        actorsHealth = new Stage();

        // Music creation
        music = Gdx.audio.newMusic(Gdx.files.internal("sound/alt_version_I_guess.mp3"));
        music.play();
        music.setVolume(1.0f);

        stage.setDebugAll(true);
        actorsHealth.setDebugAll(true);
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

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
                    typeOfAttack = TypeOfAttack.ATTACK;
                    schedulePlayerTurn(0.3f,typeOfAttack); // Delay in seconds for player's attack (adjust as needed)
                }
            }
        });

        // Magic logic
        magicButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toggleMagicMoves();
            }
        });

        // Defend logic
        defendButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // TODO: Implement defend logic
                if (turn.equals(Turn.PLAYER_TURN))
                {
                    typeOfAttack = TypeOfAttack.DEFENSE;
                    schedulePlayerTurn(0.5f,typeOfAttack);
                }
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

        // Player status label
        playerStatusLabel = new Label("Player HP: " + player.getHealth(), skin);
        playerStatusLabel.setPosition(player.getPosition().x, player.getPosition().y - 100);
        actorsHealth.addActor(playerStatusLabel);

        // Enemy status label
        enemyStatusLabel = new Label("Enemy HP: " + player.getHealth(), skin);
        enemyStatusLabel.setPosition(enemy.getPosition().x, enemy.getPosition().y - 100);
        actorsHealth.addActor(enemyStatusLabel);

        //Turn set
        turn = Turn.PLAYER_TURN;
    }

    /**
     * Renders the stage and the players on screen.
     * @param delta float
     */
    public void render(float delta)
    {
        Gdx.gl.glClearColor(0, 0.1f, 0.3f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        displayMoves();
        actorsHealth.act();
        actorsHealth.draw();

        float referenceScreenWidth = 1280;  // The reference screen width for scaling
        float scale = 3 * (Gdx.graphics.getWidth() / referenceScreenWidth);

        update(delta);
        player.renderBattle(spriteBatch, delta, scale);
        enemy.renderBattle(spriteBatch, delta, scale);
        turnManager();
    }

    public void update(float delta)
    {
        if (playerTurn && typeOfAttack == TypeOfAttack.ATTACK)
        {
            player.state = Player.State.ATTACKING;
        } else if ((playerTurn || enemyTurn) && typeOfAttack == TypeOfAttack.DEFENSE)
        {
            player.state = Player.State.DEFENDING;
        }else
        {
            player.state = Player.State.STANCE;
        }

        if (enemyTurn && typeOfAttack == TypeOfAttack.ATTACK)
        {
            enemy.state = Enemy.State.ATTACKING;
        } else if (enemyTurn && typeOfAttack == TypeOfAttack.DEFENSE)
        {
            enemy.state = Enemy.State.ATTACKING;
        } else
        {
            enemy.state = Enemy.State.STANCE;
        }
    }

    /**
     * Manages the players and enemies turns. Currently incomplete
     * TODO: Incomplete. implement logic for player turn
     */
    public void turnManager()
    {
        if (turn == Turn.ENEMY_TURN)
        {
            //enemyTurn();
            turn = Turn.PLAYER_TURN;
        }else if (turn == Turn.PLAYER_TURN)
        {

        }
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

    private void displayMoves()
    {
        if (!battleOccuring)
        {
            stage.act();
            stage.draw();
        }
    }

    /**
     * activates the method playerAttackOccurred. Not in use.
     */
    public void playerTurn()
    {
        playerAttackOccurred();
    }

    /**
     * Activates the method enemyAttackOccurred.
     */
    public void enemyTurn()
    {
        enemyAttackOccurred();
    }

    /**
     * scedulePlayerTurn starts the players turn with a delay so that it's not done instantly
     * @param delay how long to delay the turn
     */
    private void schedulePlayerTurn(final float delay, final TypeOfAttack typeOfAttack)
    {
        battleOccuring = true;
        playerTurn = true;
        if (typeOfAttack == (TypeOfAttack.ATTACK))
        {
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    // Code to execute after the delay
                    playerAttackOccurred();
                    scheduleEnemyTurn(delay,typeOfAttack); // Delay in seconds for enemy's turn (adjust as needed)
                    playerTurn = false;
                }
            }, delay);
        } else if (typeOfAttack == (TypeOfAttack.DEFENSE))
        {
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    // Code to execute after the delay
                    scheduleEnemyTurn(delay,typeOfAttack); // Delay in seconds for enemy's turn (adjust as needed)
                    playerTurn = false;
                }
            }, 0.1f);
        }
    }

    /**
     * scheduleEnemyTurn starts the enemy turn with a delay so that it's not done instantly
     * Depends on schedulePlayerTurn()
     * @param delay how long to delay the turn
     */
    private void scheduleEnemyTurn(float delay, TypeOfAttack typeOfAttack)
    {
        enemyTurn = true;
        if (typeOfAttack == (TypeOfAttack.ATTACK))
        {
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    // Code to execute after the delay
                    turn = Turn.ENEMY_TURN;
                    enemyAttackOccurred();
                    enemyTurn = false;
                    battleOccuring = false;
                }
            }, delay);
        } else if (typeOfAttack == (TypeOfAttack.DEFENSE))
        {
            Timer.schedule(new Timer.Task()
            {
                @Override
                public void run()
                {
                    // Code to execute after the delay
                    turn = Turn.ENEMY_TURN;
                    enemyAttackOccurred();
                    enemyTurn = false;
                    battleOccuring = false;
                }
            }, delay);
        }
    }

    /**
     * Calculates damage against the enemy, and updates both the player's and enemy's health
     * This uses damageCalculation which can be found within BattleCalculation
     */
    public void playerAttackOccurred()
    {

        BattleCalculation.damageCalculation(player,enemy);
        // This is here to update the players health every time this method is called.
        playerStatusLabel.setText("Player HP: " + player.getHealth());
        enemyStatusLabel.setText("Enemy HP: " + enemy.getHealth());
    }

    /**
     * Calculates damage against the player and updates both the player's and enemy's health
     * This uses damageCalculation which can be found within BattleCalculation
     */
    public void enemyAttackOccurred()
    {
        if (typeOfAttack == TypeOfAttack.ATTACK) {
            BattleCalculation.damageCalculation(enemy, player);
        } else if (typeOfAttack == TypeOfAttack.DEFENSE)
        {
            BattleCalculation.defenseDamageCalculation(enemy, player);
        }
        playerStatusLabel.setText("Player HP: " + player.getHealth());
        enemyStatusLabel.setText("Enemy HP: " + enemy.getHealth());
    }

    /**
     * Displays a move list depending on the category. Currently only used for the magic list.
     * @param category
     */
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

    /**
     * Disposes the stage when the battle is done
     * TODO: Probably needs to be reworked.
     */
    public void dispose()
    {
        stage.dispose();
    }
}









