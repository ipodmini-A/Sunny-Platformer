package cchase.platformergame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
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

import java.util.LinkedList;

/**
 * BattleNew.java
 * BattleNew serves as the UI as well as controls the flow of battle.
 * Currently this is being made to replace Battle.java and allow features such as multiple characters and selecting which
 * enemy you would like to attack.
 */
public class BattleNew
{
    private Player[] player;
    private Enemy[] enemy;
    private Stage stage;
    private Skin skin;
    private Label[] playerStatusLabel;
    private Label[] enemyStatusLabel;
    private Label currentCharacterTurnLabel;
    private TextButton fightButton, fleeButton, attackButton, defendButton, magicButton, abilityButton, backButton;
    private VerticalGroup movesGroup;
    private VerticalGroup enemyGroup;
    private ScrollPane movesScrollPane;
    private ScrollPane enemyScrollPane;
    private boolean attackClicked;
    private boolean magicClicked;
    private boolean abilityClicked;
    private SpriteBatch spriteBatch;
    private OrthographicCamera camera;
    private boolean battleOccurring;
    private boolean playerTurn;
    private boolean enemyTurn;
    enum Turn
    {
        PLAYER_TURN, ENEMY_TURN
    }
    enum TypeOfAttack
    {
        ATTACK, DEFENSE, MAGIC, ABILITY
    }
    private Turn turn;
    private TypeOfAttack[] typeOfAttack;
    private TypeOfAttack[] enemyTypeOfAttack;
    private Music music;
    private boolean preActionMenu;
    private boolean actionMenu;
    private int currentCharacterTurn;
    private int currentEnemyTurn;
    private float scale;
    private LinkedList<Action> actions;

    public class Action
    {
        Player attacker;
        Player defender;
        TypeOfAttack typeOfAttack;

        public Action(Player attacker, Player defender, TypeOfAttack typeOfAttack)
        {
            this.attacker = attacker;
            this.defender = defender;
            this.typeOfAttack = typeOfAttack;
        }

        public void performMoves()
        {

            if (typeOfAttack == (BattleNew.TypeOfAttack.ATTACK)) {
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        // Code to execute after the delay
                        attacker.state = Player.State.ATTACKING;
                        System.out.println("Currently moving: " + attacker.getName());
                        BattleCalculation.damageCalculation(attacker, defender);
                        // This is here to update the players health every time this method is called.
                    }
                }, 0.1f);
            } else if (typeOfAttack == (BattleNew.TypeOfAttack.DEFENSE))
            {
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        defender.state = Player.State.DEFENDING;
                        BattleCalculation.defenseDamageCalculation(attacker,defender);
                    }
                }, 0.1f);
            }
        }
    }

    public BattleNew(final Player[] player, final Enemy[] enemy)
    {
        // Initialize all players and enemies
        this.player = player;
        this.enemy = enemy;


        // Battle occuring set to false
        battleOccurring = false;

        // New sprite batch
        spriteBatch = new SpriteBatch();

        float referenceScreenWidth = 1280;  // The reference screen width for scaling
        scale = 1.8f * (Gdx.graphics.getWidth() / referenceScreenWidth);

        // Stage creation
        stage = new Stage();
        stage.setDebugAll(true);

        // Array initialization
        playerStatusLabel = new Label[player.length];
        enemyStatusLabel = new Label[enemy.length];
        typeOfAttack = new TypeOfAttack[player.length];
        enemyTypeOfAttack = new TypeOfAttack[enemy.length];

        actions = new LinkedList<Action>();

        // Music creation

        // Setting input
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(new BattleInput());
        multiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(multiplexer);


        // Skin creation
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        // Buttons
        fightButton = new TextButton("Fight", skin);
        fleeButton = new TextButton("Flee", skin);
        attackButton = new TextButton("Attack", skin);
        defendButton = new TextButton("Defend", skin);
        magicButton = new TextButton("Magic", skin);
        abilityButton = new TextButton("Ability", skin);
        backButton = new TextButton("Back", skin);

        fightButton.setPosition(20,140);
        fleeButton.setPosition(20,110);
        attackButton.setPosition(20, 140);
        defendButton.setPosition(20, 110);
        magicButton.setPosition(20, 80);
        abilityButton.setPosition(20,50);
        backButton.setPosition(20,20);

        currentCharacterTurnLabel = new Label(currentCharacterTurnLabel + "",skin);

        // Moves list
        movesGroup = new VerticalGroup();
        movesScrollPane = new ScrollPane(movesGroup, skin);
        movesScrollPane.setSize(300,200);
        movesScrollPane.setPosition(Gdx.graphics.getWidth() - movesScrollPane.getWidth() - 20, 20);
        movesScrollPane.setVisible(false);

        // enemy List
        enemyGroup = new VerticalGroup();
        enemyScrollPane = new ScrollPane(enemyGroup, skin);
        enemyScrollPane.setSize(300,200);
        enemyScrollPane.setPosition(Gdx.graphics.getWidth() - movesScrollPane.getWidth() - 20, 20);
        enemyScrollPane.setVisible(false);

        fightButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                // TODO: Implement fight logic
                fightButton();
            }
        });

        fleeButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                // TODO: Implement flee logic
            }
        });

        // Attack logic
        attackButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // TODO: Implement attack logic
                if (turn.equals(Turn.PLAYER_TURN))
                {
                    typeOfAttack[currentCharacterTurn] = TypeOfAttack.ATTACK;
                    attackClicked = false;
                    toggleEnemyList();
                }
            }
        });

        // Defend logic
        defendButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                // TODO: Implement defend logic
            }
        });

        // Magic
        magicButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                toggleMagicMoves();
            }
        });

        abilityButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                //toggleMagicMoves();
            }
        });

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                backButton();
            }
        });

        // Adding buttons and individual labels to the stage
        stage.addActor(fightButton);
        stage.addActor(fleeButton);
        stage.addActor(attackButton);
        stage.addActor(defendButton);
        stage.addActor(magicButton);
        stage.addActor(abilityButton);
        stage.addActor(backButton);
        stage.addActor(movesScrollPane);
        stage.addActor(enemyScrollPane);
        stage.addActor(currentCharacterTurnLabel);

        //Setting camera, setting position, and setting player status label
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f, 0); // Set the camera position to the center of the screen
        camera.update();
        float playerLocationY = 2f;
        for (int i = 0; i < player.length; i++)
        {
            player[i].updateCamera(camera);
            player[i].setPositionX(Gdx.graphics.getWidth() / 60f);
            System.out.println("X Position set to: " + player[i].getPosition().x);
            player[i].setPositionY(Gdx.graphics.getHeight() / playerLocationY);
            System.out.println("Y Position set to: " + player[i].getPosition().y);

            playerStatusLabel[i] = new Label("Player HP: " + player[i].getHealth(), skin);
            playerStatusLabel[i].setPosition(player[i].getPosition().x, player[i].getPosition().y - 100);
            stage.addActor(playerStatusLabel[i]);

            playerLocationY -= 0.4f;
        }
        float enemyLocationY = 2f;
        for (int i = 0; i < enemy.length; i++)
        {
            enemy[i].updateCamera(camera);
            enemy[i].setPositionX((Gdx.graphics.getWidth() - enemy[i].getWidth()) - 250 );
            System.out.println("X Position set to: " + enemy[i].getPosition().x);
            enemy[i].setPositionY(Gdx.graphics.getHeight() / enemyLocationY);
            System.out.println("Y Position set to: " + enemy[i].getPosition().y);

            enemyStatusLabel[i] = new Label("Enemy HP: " + enemy[i].getHealth(), skin);
            enemyStatusLabel[i].setPosition(enemy[i].getPosition().x, enemy[i].getPosition().y - 100);
            stage.addActor(enemyStatusLabel[i]);

            enemyLocationY -= 0.4f;
        }

        // Turn set
        turn = Turn.PLAYER_TURN;
        preActionMenu = true;
        actionMenu = false;
    }

    public void render(float delta)
    {
        Gdx.gl.glClearColor(0, 0.1f, 0.3f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();

        menuFlow();

        for (int i = 0; i < player.length; i++)
        {
            playerSpriteUpdate(delta, i);
            player[i].renderBattle(spriteBatch,delta, scale);
        }
        for (int i = 0; i < enemy.length; i++)
        {
            enemySpriteUpdate(delta, i);
            enemy[i].renderBattle(spriteBatch,delta,scale);
        }

        currentCharacterTurnLabel.setText(currentCharacterTurn + "");
        healthBarUpdate();
        turnManager();

    }

    public void playerSpriteUpdate(float delta, int index)
    {

        for (int i = 0; i < player.length; i++)
        {
            if (!battleOccurring)
            {
                player[i].state = Player.State.STANCE;
            }
        }
    }

    public void enemySpriteUpdate(float delta, int index)
    {
        for (int i = 0; i < enemy.length; i++)
        {
            if (enemyTurn && enemyTypeOfAttack[index] == TypeOfAttack.ATTACK)
            {
                enemy[i].state = Enemy.State.ATTACKING;
            } else if (enemyTurn && enemyTypeOfAttack[index] == TypeOfAttack.DEFENSE)
            {
                enemy[i].state = Enemy.State.ATTACKING;
            } else
            {
                enemy[i].state = Enemy.State.STANCE;
            }
        }
    }

    /**
     * This might be broken. Currently, manages turns.
     */
    public void turnManager()
    {
        if (turn == BattleNew.Turn.ENEMY_TURN)
        {
            //enemyTurn();
            turn = BattleNew.Turn.PLAYER_TURN;
        }else if (turn == BattleNew.Turn.PLAYER_TURN)
        {

        }
    }

    public void menuFlow()
    {
        if (preActionMenu)
        {
            showPreActionMenu();
            hideActionMenu();
        } else if (actionMenu)
        {
            hidePreActionMenu();
            showActionMenu();
        }

        executeTurn();
    }


    private void showPreActionMenu()
    {
        fightButton.setVisible(true);
        fleeButton.setVisible(true);
        movesScrollPane.setVisible(false);
        enemyScrollPane.setVisible(false);
    }

    private void hidePreActionMenu()
    {
        fightButton.setVisible(false);
        fleeButton.setVisible(false);
    }

    private void showActionMenu()
    {
        attackButton.setVisible(true);
        defendButton.setVisible(true);
        magicButton.setVisible(true);
        abilityButton.setVisible(true);
        backButton.setVisible(true);
    }

    private void hideActionMenu()
    {
        attackButton.setVisible(false);
        defendButton.setVisible(false);
        magicButton.setVisible(false);
        abilityButton.setVisible(false);
        backButton.setVisible(false);
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

    private void showEnemyList()
    {
        enemyGroup.clear();
        enemyGroup.align(Align.left);
        enemyGroup.space(10);
        enemyGroup.padLeft(10);
        TextButton[] enemyButton = new TextButton[enemy.length];
        enemyScrollPane.setVisible(true);

        for (int i = 0; i < enemy.length; i++)
        {
            enemyButton[i] = new TextButton("AAAAA", skin);
            enemyGroup.addActor(enemyButton[i]);
            final int finalI = i;
            enemyButton[i].addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    // TODO: add selection logic
                    actions.add(new Action(player[currentCharacterTurn],enemy[finalI],TypeOfAttack.ATTACK));
                    currentCharacterTurn++;
                    hideEnemyList();
                    actionMenu = false;
                    showActionMenu();
                }
            });
        }
    }

    private void hideEnemyList()
    {
        enemyScrollPane.setVisible(false);
    }

    private void toggleEnemyList()
    {
        attackClicked = !attackClicked;

        if (attackClicked)
        {
            showEnemyList();
        } else
        {
            hideEnemyList();
        }
    }

    private void fightButton()
    {
        preActionMenu = false;
        actionMenu = true;
        currentCharacterTurn = 0;
    }

    private void backButton()
    {
        if (currentCharacterTurn == 0)
        {
            preActionMenu = true;
            actions.clear();
        } else if (currentCharacterTurn >= 1)
        {
            currentCharacterTurn--;
            actions.pop();
        }
    }

    private void attackMenu()
    {

    }

    private void healthBarUpdate()
    {
        for (int i = 0; i < player.length; i++)
        {
            playerStatusLabel[i].setText("Player HP: " + player[i].getHealth());
        }
        for (int i = 0; i < player.length; i++)
        {
            enemyStatusLabel[i].setText("Enemy HP: " + enemy[i].getHealth());
        }
    }

    private int actionsPerformed = 0;
    private void executeTurn() {
        if (actions.size() == player.length)
        {
            hideMovesList();
            hideActionMenu();
            hideEnemyList();
            battleOccurring = true;

            for (int i = 0; i < actions.size(); i++) {
                final Action action = actions.get(i);

                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        action.performMoves();
                        actionsPerformed++;
                    }
                }, i * 0.5f); // Delay each action by i * 0.5 seconds
            }

            actions.clear();
        }

        if (currentCharacterTurn == player.length && actionsPerformed == player.length)
        {
            battleOccurring = false;
            showPreActionMenu();
            actionsPerformed = 0;
            currentCharacterTurn = 0;
        }
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
