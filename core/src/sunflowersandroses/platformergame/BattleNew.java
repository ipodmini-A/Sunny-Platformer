package sunflowersandroses.platformergame;

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
import sunflowersandroses.platformergame.player.Player;

import java.util.LinkedList;
import java.util.Random;

/**
 * BattleNew.java
 * BattleNew serves as the UI as well as controls the flow of battle.
 * Currently, this is being made to replace Battle.java and allow features such as multiple characters and selecting which
 * enemy you would like to attack.
 * TODO: Implement Magic and Abilities,
 *      Implement a flag for when all enemies are defeated.
 *      Put code in place to test for status (Maybe have a enum called status, with things like dead?)
 *      With above, when the player is dead, they shouldn't be able to do anything, or be attacked... It would be funny though
 *
 * Current Bugs:
 * Null pointer exception is caused when the player selects Defend on all characters. Code that is causing the bug is currently
 * commented out
 */
public class BattleNew
{
    private Player[] player;
    private Enemy[] enemy;
    private Stage stage;
    private Skin skin;
    private Label[] playerStatusLabel;
    private ProgressBar[] playerHealthBar;
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
    private Random rand;
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
    private float spriteScale;
    private LinkedList<Action> actions;
    private boolean allEnemiesDefeated;
    private boolean playerFled = false;

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

        public Action(Player defender, TypeOfAttack typeOfAttack)
        {
            this.defender = defender;
            this.typeOfAttack = typeOfAttack;
        }

        public void performMoves()
        {
            try
            {
                if (attacker.getStatus() != Player.Status.DEAD)
                {
                    if (typeOfAttack == (BattleNew.TypeOfAttack.ATTACK))
                    {
                        if (defender.isDefending())
                        {
                            Timer.schedule(new Timer.Task()
                            {
                                @Override
                                public void run()
                                {
                                    // Code to execute after the delay
                                    attacker.setState(Player.State.ATTACKING);
                                    defender.setState(Player.State.DEFENDING);
                                    System.out.println("Currently moving: " + attacker.getName());
                                    BattleCalculation.defenseDamageCalculation(attacker, defender);
                                    defender.setDefending(false);
                                    // This is here to update the players health every time this method is called.
                                }
                            }, 0.5f);

                        } else
                        {
                            Timer.schedule(new Timer.Task()
                            {
                                @Override
                                public void run()
                                {
                                    // Code to execute after the delay
                                    attacker.setState(Player.State.ATTACKING);
                                    System.out.println("Currently moving: " + attacker.getName());
                                    if (defender.getStatus() == Player.Status.DEAD)
                                    {
                                        int indexOfNextEnemy = 0;
                                        for (int i = 0; i < enemy.length; i++)
                                        {
                                            if (enemy[i].getStatus() != Player.Status.DEAD)
                                            {
                                                indexOfNextEnemy = i;
                                            }
                                        }
                                        BattleCalculation.damageCalculation(attacker, enemy[indexOfNextEnemy]);
                                    } else
                                    {
                                        BattleCalculation.damageCalculation(attacker, defender);
                                    }
                                    // This is here to update the players health every time this method is called.
                                }
                            }, 0.5f);
                        }
                    } else if (typeOfAttack == (BattleNew.TypeOfAttack.DEFENSE))
                    {
                        Timer.schedule(new Timer.Task()
                        {
                            @Override
                            public void run()
                            {
                                defender.setState(Player.State.DEFENDING);
                                //attacker.state = Player.State.ATTACKING;
                                System.out.println("Currently moving: " + defender.getName());
                                //BattleCalculation.defenseDamageCalculation(attacker,defender);
                            }
                        }, 0.5f);
                    }
                }
            } catch (Exception e)
            {
                // The assumption is that if there is no attacker, then this player must be defending.
                Timer.schedule(new Timer.Task()
                {
                    @Override
                    public void run()
                    {
                        defender.setState(Player.State.DEFENDING);
                        //attacker.state = Player.State.ATTACKING;
                        System.out.println("Currently moving: " + defender.getName());
                        //BattleCalculation.defenseDamageCalculation(attacker,defender);
                    }
                }, 0.5f);
            }
        }
    }

    public BattleNew(final Player[] player, final Enemy[] enemy)
    {
        // Initialize all players and enemies
        this.player = player;
        this.enemy = enemy;

        // Random initializer
        rand = new Random();

        // Battle occuring set to false
        battleOccurring = false;

        // New sprite batch
        spriteBatch = new SpriteBatch();

        float referenceScreenWidth = 1280f;  // The reference screen width for scaling
        spriteScale = 1.8f * (Gdx.graphics.getWidth() / referenceScreenWidth);

        // Stage creation
        stage = new Stage();
        stage.setDebugAll(true);

        // Array initialization
        playerStatusLabel = new Label[player.length];
        playerHealthBar = new ProgressBar[player.length];
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

        currentCharacterTurnLabel = new Label(String.valueOf(currentCharacterTurnLabel),skin);

        // Moves list
        movesGroup = new VerticalGroup();
        movesScrollPane = new ScrollPane(movesGroup, skin);
        movesScrollPane.setSize(300,Gdx.graphics.getHeight() / 5f);
        movesScrollPane.setPosition(fightButton.getX() + 100, 20);
        movesScrollPane.setVisible(false);

        // enemy List
        enemyGroup = new VerticalGroup();
        enemyScrollPane = new ScrollPane(enemyGroup, skin);
        enemyScrollPane.setSize(300,Gdx.graphics.getHeight() / 5f);
        enemyScrollPane.setPosition(fightButton.getX() + 100, 20);
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
                playerFled = true;

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
                if (turn.equals(Turn.PLAYER_TURN))
                {
                    typeOfAttack[currentCharacterTurn] = TypeOfAttack.DEFENSE;
                    actions.add(new Action(player[currentCharacterTurn],TypeOfAttack.DEFENSE));
                    player[currentCharacterTurn].setDefending(true);
                    currentCharacterTurn++;
                }
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

        //TODO: Fix scaling issue when playing at higher resolutions
        float playerLocationY = 2f;
        for (int i = 0; i < player.length; i++)
        {
            player[i].updateCamera(camera);
            player[i].setPositionX(Gdx.graphics.getWidth() / 60f);
            System.out.println("X Position set to: " + player[i].getPosition().x);
            player[i].setPositionY(((Gdx.graphics.getHeight() / 720f) / -16f) + (playerLocationY * (Gdx.graphics.getHeight() / 720f) * 240f));
            System.out.println("Y Position set to: " + player[i].getPosition().y);

            playerStatusLabel[i] = new Label(player[i].getName() + " HP: " + player[i].getHealth(), skin);
            playerStatusLabel[i].setPosition(Gdx.graphics.getWidth() / 2f, (Gdx.graphics.getHeight() / 4f) - (playerLocationY * 80f));
            //playerHealthBar[i] = new ProgressBar(0f, player[i].health, 1f, false, skin);
            //playerHealthBar[i].setPosition(movesScrollPane.getX() + movesGroup.getMaxWidth(), movesGroup.getY() * playerLocationY);
            stage.addActor(playerStatusLabel[i]);

            playerLocationY -= 0.5f;
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
            playerSpriteUpdate();
            player[i].renderBattle(spriteBatch,delta, spriteScale);
        }
        for (int i = 0; i < enemy.length; i++)
        {
            {
                if (enemy[i].getHealth() >= 0)
                {
                    enemySpriteUpdate();
                    enemy[i].renderBattle(spriteBatch, delta, spriteScale);
                }
            }
        }

        currentCharacterTurnLabel.setText(currentCharacterTurn + "");
        //enemyKilled();
        healthBarUpdate();
        turnManager();

        setEnemyStatus();

    }

    /**
     * Updates the players sprites. Uses a for loop and as long as a battle isn't occuring, their state is set to stance.
     */
    public void playerSpriteUpdate()
    {
        for (int i = 0; i < player.length; i++)
        {
            if (!battleOccurring)
            {
                player[i].setState(Player.State.STANCE);
            }
        }
    }

    /**
     * Updates the enemies sprites. Uses a for loop and as long as a battle isn't occurring, their state is set to stance
     */
    public void enemySpriteUpdate()
    {
        for (int i = 0; i < enemy.length; i++)
        {
            if (!battleOccurring)
            {
                enemy[i].setState(Enemy.State.STANCE);
            }
        }
    }

    /**
     * This might be broken. Currently, manages turns.
     *
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

    /**
     * Assists with how the menus are handled within battle. Does not account for everything, such as the magic or
     * abilities list.
     */
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

        if (battleOccurring)
        {
            hideActionMenu();
            hidePreActionMenu();
            hideMovesList();
            hideEnemyList();
        }

        executeTurn();
    }

    /**
     * Lines up the enemies moves to be added to the action linked list. If the enemy isn't dead, they attack a random
     * player.
     * TODO: The enemy can attack from the dead.
     */
    public void enemyTurnExecution()
    {
        for (int i = 0; i < enemy.length; i++)
        {
            if (enemy[i].getStatus() != Enemy.Status.DEAD)
            {
                int choice = rand.nextInt(2);
                actions.add(new Action(enemy[i], player[choice], TypeOfAttack.ATTACK));
                System.out.println("Added enemy");
            }
        }

    }

    /**
     * Shows the pre action menu. Sets the fight and flee button to true. In the event of where the back button is pressed
     * to go back to the pre action menu, the moves and enemy scroll pane are set to false as well.
     */
    private void showPreActionMenu()
    {
        fightButton.setVisible(true);
        fleeButton.setVisible(true);
        movesScrollPane.setVisible(false);
        enemyScrollPane.setVisible(false);
    }

    /**
     * Hides the pre action menu. Sets the fight and flee button to false
     */
    private void hidePreActionMenu()
    {
        fightButton.setVisible(false);
        fleeButton.setVisible(false);
    }

    /**
     * Shows the action menu. Sets the attack, defend, magic, ability, and back button to true
     */
    private void showActionMenu()
    {
        attackButton.setVisible(true);
        defendButton.setVisible(true);
        magicButton.setVisible(true);
        abilityButton.setVisible(true);
        backButton.setVisible(true);
    }

    /**
     * Hides the action menu. Sets the attack, defend, magic, ability, and back button to false
     */
    private void hideActionMenu()
    {
        attackButton.setVisible(false);
        defendButton.setVisible(false);
        magicButton.setVisible(false);
        abilityButton.setVisible(false);
        backButton.setVisible(false);
    }

    /**
     * Toggles the magic moves
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

    /**
     * Displays a move list depending on the category.
     * @param category "Magic" for the magic list. "Abilities" for the ability list.
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

    /**
     * Hides the moves list.
     */
    private void hideMovesList() {
        movesScrollPane.setVisible(false);
    }

    /**
     * Shows the enemy list.
     * Uses a for loop to generate buttons to correspond with the enemies.
     */
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
            if (!(enemy[i].getHealth() <= 0))
            {
                enemyButton[i] = new TextButton(enemy[i].getName(), skin);
                enemyGroup.addActor(enemyButton[i]);
                final int finalI = i;
                enemyButton[i].addListener(new ClickListener()
                {
                    @Override
                    public void clicked(InputEvent event, float x, float y)
                    {
                        // TODO: add selection logic
                        actions.add(new Action(player[currentCharacterTurn], enemy[finalI], TypeOfAttack.ATTACK));
                        currentCharacterTurn++;
                        hideEnemyList();
                        actionMenu = false;
                        showActionMenu();
                    }
                });
            }
        }
    }

    /**
     * Hides the enemy list.
     */
    private void hideEnemyList()
    {
        enemyScrollPane.setVisible(false);
    }

    /**
     * Toggles the enemy list.
     */
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

    /**
     * Controls the logic of the fight button. If its clicked the pre action menu is set to false and hidden, while the
     * action menu is set to true and shown
     */
    private void fightButton()
    {
        preActionMenu = false;
        actionMenu = true;
        currentCharacterTurn = 0;
    }

    /**
     * Controls the logic of the back button. If the current character turn is 0, pre actions menu is shown.
     * if the current character turn equals or is over 1, the current character turn goes back one, and that action is removed
     * from the linked list.
     */
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

    /**
     * Not in use.
     */
    private void attackMenu()
    {

    }

    /**
     * Updates the health bar. Health bar won't show if the enemy status is set to dead.
     */
    private void healthBarUpdate()
    {
        for (int i = 0; i < player.length; i++)
        {
            playerStatusLabel[i].setText(player[i].getName() + " HP: " + player[i].getHealth());
        }
        for (int i = 0; i < enemy.length; i++)
        {
            if (!(enemy[i].getStatus() == Player.Status.DEAD))
            {
                enemyStatusLabel[i].setText("Enemy HP: " + enemy[i].getHealth());
            }else
            {
                enemyStatusLabel[i].setVisible(false);
            }
        }
    }

    private int actionsPerformed = 0;
    private int actionListSize = 0;

    /**
     * Method that executes once all players have selected an action. It checks to see if the currentCharacterTurn is
     * equal to the player length, which then executes the turn.
     * Once actionsPerformed equals the size of the Linked List and the battleOccuring boolean is set to false, the
     * pre action menu is called to be shown.
     * TODO: See if it is possible to refactor
     *  Currently if a players status is set to dead, they can still move.
     */
    private void executeTurn()
    {

        if (currentCharacterTurn == player.length)
        {
            System.out.println("Hiding menu");
            actionListSize = actions.size();
            currentCharacterTurn = 0;
            enemyTurnExecution();
            battleOccurring = true;
            int i = 0;

            for (; i < actions.size(); i++) {
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

        if (actionsPerformed == actionListSize && battleOccurring)
        {
            System.out.println("Starting reset timer");
            actionsPerformed = 0;
            Timer.schedule(new Timer.Task()
            {
                @Override
                public void run() {
                    battleOccurring = false;
                    actionMenu = false;
                    showPreActionMenu();
                    //actionsPerformed = 0;
                    actionListSize = 0;
                    currentCharacterTurn = 0;
                    typeOfAttack = new TypeOfAttack[player.length];
                }
            }, 1f * actionListSize); // Delay each action by i * 0.5 seconds
        }
    }

    public void enemyKilled()
    {
        for (int i = 0; i < enemy.length; i++)
        {
            
            if (enemy[i].getHealth() <= 0)
            {
                enemy[i].dispose();
                enemy[i] = null;
            }
        }
    }

    /**
     * Sets the enemy status to dead if their health is at or below 0.
     * To be placed within a render method so that health status is constantly updated.
     */
    public void setEnemyStatus()
    {
        for (int i = 0; i < enemy.length; i++)
        {
            if (enemy[i].getHealth() <= 0)
            {
                enemy[i].setStatus(Player.Status.DEAD);
            }
        }
    }


    /**
     * Returns true if the player has won yet.
     * This method runs through each enemy to see if all of them are alive.
     * @return true if all enemy objects status is reported dead, false if otherwise
     */
    public boolean playerWon()
    {
        boolean allEnemiesKilled = true;
        for (int i = 0; i < enemy.length; i++)
        {
            if (enemy[i].getStatus() != Player.Status.DEAD)
            {
                allEnemiesKilled = false;
            }
        }
        return allEnemiesKilled;
    }

    public boolean playerFlee()
    {
        return playerFled;
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
