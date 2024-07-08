package cchase.platformergame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;


/**
 * Player.java
 * Contains code that controls various aspects of the player. This class is intended to extend from, making various
 * characters from it. Currently, Enemy.java and NonPlayableCharacter extends it.
 * Objects include the characters stats, their sprites, their moves, and their bounds.
 * TODO: As time goes on, Player may not be the best name for this class.
 * TODO: Clean up this class.
 */
public class Player
{
    private static final float GRAVITY = -1000f; // Adjust the gravity value as needed -1000f
    private static final float JUMP_VELOCITY = 450f; // Adjust the jump velocity as needed
    protected float HEIGHT = 60f;
    protected static final float WIDTH = 30f;
    protected static final float SPRITE_HEIGHT = 60f + 10f;
    protected static final float SPRITE_WIDTH = 60f + 10f; // I don't know why adding 10 makes the sprite the proper size.
    protected static float MAX_VELOCITY = 500f;
    private static float SCALE = 1f;
    protected String name;
    // Player stats
    protected float health;
    protected float attackPoints;
    protected float defensePoints;
    protected float magic;
    protected float wisdom;
    protected float speed;
    protected int money;
    final HashMap<String, Sprite> sprites = new HashMap<String, Sprite>();

    protected Texture texture;
    protected Sprite sprite;
    protected float spriteXPositionOffset = 7f;
    protected float spriteYPosition = 7f;
    protected Vector2 position;
    protected Vector2 velocity;
    protected Rectangle bounds;
    protected Rectangle attackHitbox;
    protected boolean attackedAlready;
    private PlatformerInput platformerInput;
    protected boolean grounded;
    private boolean touchingLeftWall;
    private boolean touchingRightWall;
    private boolean touchingWall;
    private boolean touchingCeiling;
    private boolean flying;
    protected OrthographicCamera camera;
    protected SpriteBatch spriteBatch;
    protected TextureAtlas textureAtlas;
    //protected TextureAtlas textureAtlasRunning;
    private boolean disableControls;
    private boolean leftMove;
    private boolean rightMove;
    private boolean downMove;
    private boolean jump;
    private boolean attack;
    private boolean displayMessage;
    private boolean nextMessage;
    private boolean npcInteraction;
    private boolean itemInteraction;

    enum State
    {
        STANDING, WALKING, JUMPING, FALLING, WALL_RIDING, LOOKING_DOWN, LOOKING_UP,TOUCHING_WALL,
        ATTACKING, DEFENDING, PUNCHING, STANCE
    }
    enum Status
    {
        DEAD
    }
    protected State state;
    protected Status status;
    protected boolean facingRight = false;
    protected boolean doubleJumped = false;
    protected boolean wallRiding = false;
    protected boolean lookingUp;
    protected boolean lookingDown;
    protected boolean spriteAttacking;
    protected boolean defending;
    protected boolean allowedToDash;
    protected boolean dashing;
    protected LinkedList<Item> collectedItems;
    SpriteBatch batch;
    protected Animation<TextureRegion> runningAnimation;
    protected Animation<TextureRegion> standingAnimation;
    protected float runningElapsedTime;
    protected float standingElapsedTime;
    protected float baseFrameDuration = 1/4f;
    protected float velocitySensitivity = 0.002f;
    protected float runningFrameDuration;
    protected float standingFrameDuration = 1/6f;
    protected TextureRegion runningFlippedFrame;
    protected TextureRegion standingFlippedFrame;
    protected boolean invincible;
    protected Random random;

    /**
     * Default constructor. The location of the player is set to 0,0
     * Health is set to 100f
     * Attack is set to 10f
     * Defense is set to 10f
     */
    public Player()
    {
        position = new Vector2(0,0);
        velocity = new Vector2();
        grounded = false;
        name = "CHARACTER";
        health = 100f;
        attackPoints = 10f;
        defensePoints = 10f;
        money = 100;
        touchingCeiling = false;
        touchingLeftWall = false;
        touchingRightWall = false;
        touchingWall = false;
        doubleJumped = false;
        lookingUp = false;
        lookingDown = false;
        spriteAttacking = false;
        attackedAlready = false;
        defending = false;
        flying = false;
        dashing = false;
        invincible = false;
        allowedToDash = true;
        npcInteraction = false;
        itemInteraction = false;
        displayMessage = false;
        nextMessage = false;
        disableControls = false;
        bounds = new Rectangle(position.x, position.y, WIDTH, HEIGHT);
        bounds.setSize(WIDTH, HEIGHT); // Update the bounds size
        state = State.STANDING;

        collectedItems = new LinkedList<Item>();

        textureAtlas = new TextureAtlas("sprites.txt");
        spriteBatch = new SpriteBatch();
        texture = new Texture("debugSquare.png");
        sprite = new Sprite(texture);

        addSprites();
        sprite.setSize(WIDTH,HEIGHT);

        runningAnimation = new Animation<TextureRegion>(runningFrameDuration, textureAtlas.findRegions("running")); // 4 frames per second
        runningElapsedTime = 0f;
        runningFlippedFrame = new TextureRegion(runningAnimation.getKeyFrame(runningElapsedTime,true));

        standingAnimation = new Animation<TextureRegion>(1/6f, textureAtlas.findRegions("standing"));
        standingElapsedTime = 0f;
        standingFlippedFrame = new TextureRegion(standingAnimation.getKeyFrame(standingElapsedTime,true));

        platformerInput = new PlatformerInput(this);
        Gdx.input.setInputProcessor(platformerInput);

        random = new Random();

        System.out.println("Width: " + sprite.getWidth() + " Height: " + sprite.getHeight());
    }

    /**
     * This constructor accepts a x and y value, which determines where the player is placed.
     * Health is set to 100f
     * Attack is set to 10f
     * Defense is set to 10f
     * @param x x coordinate
     * @param y y coordinate
     */
    public Player(float x, float y)
    {
        position = new Vector2(x, y);
        velocity = new Vector2();
        grounded = false;
        name = "CHARACTER";
        health = 100f;
        attackPoints = 10f;
        defensePoints = 10f;
        money = 100;
        touchingCeiling = false;
        touchingLeftWall = false;
        touchingRightWall = false;
        touchingWall = false;
        doubleJumped = false;
        lookingUp = false;
        lookingDown = false;
        spriteAttacking = false;
        attackedAlready = false;
        defending = false;
        flying = false;
        dashing = false;
        invincible = false;
        allowedToDash = true;
        npcInteraction = false;
        itemInteraction = false;
        displayMessage = false;
        nextMessage = false;
        disableControls = false;
        bounds = new Rectangle(position.x, position.y, WIDTH, HEIGHT);
        bounds.setSize(WIDTH, HEIGHT); // Update the bounds size
        state = State.STANDING;

        collectedItems = new LinkedList<Item>();

        textureAtlas = new TextureAtlas("sprites.txt");
        spriteBatch = new SpriteBatch();
        texture = new Texture("debugSquare.png");
        sprite = new Sprite(texture);

        addSprites();
        sprite.setSize(WIDTH,HEIGHT);

        runningAnimation = new Animation<TextureRegion>(runningFrameDuration, textureAtlas.findRegions("running")); // 4 frames per second
        runningElapsedTime = 0f;
        runningFlippedFrame = new TextureRegion(runningAnimation.getKeyFrame(runningElapsedTime,true));

        standingAnimation = new Animation<TextureRegion>(1/6f, textureAtlas.findRegions("standing"));
        standingElapsedTime = 0f;
        standingFlippedFrame = new TextureRegion(standingAnimation.getKeyFrame(standingElapsedTime,true));

        platformerInput = new PlatformerInput(this);
        Gdx.input.setInputProcessor(platformerInput);

        random = new Random();

        System.out.println("Width: " + sprite.getWidth() + " Height: " + sprite.getHeight());
    }

    /**
     * Controls the basic movement of the player. The original input() has been taken over by this method.
     * How fast the player can move is controlled by MAX_VELOCITY
     *
     * Currently messing around with the method.
     *
     * Update 6/25/2024:
     * Moved some of the logic from World.java to Player.java. More specifically, the way the players position is handled.
     * Update 6/26/2024:
     * For now I should just cap the FPS to 60. It runs great but for some reason, moving left and right with
     * how velocity is currently handled doesn't work.
     */
    float dt = Math.min(Gdx.graphics.getDeltaTime(), 1 / 60f);
    public void input()
    {
        dt = Math.min(Gdx.graphics.getDeltaTime(), 1/ 60f);
        if (!rightMove && !leftMove &&
                (velocity.x <= 5 && velocity.x > 0)) {
            velocity.x = 0;
        }
        position.add(velocity.x * dt, velocity.y * dt);
        if (!disableControls) {
            if (leftMove && (velocity.x >= -1 * MAX_VELOCITY)) {
                if (lookingDown)
                {
                    velocity.x -= 400f * dt * 0.85f;
                }else {
                    velocity.x -= 700f * dt * 0.85f;
                    //velocity.x = -150f;
                }
            }
            if (rightMove && (velocity.x <= MAX_VELOCITY)) {
                if (lookingDown)
                {
                    velocity.x += 400f * dt * 0.85f;
                } else {
                    velocity.x += 700f * dt * 0.85f;
                    //velocity.x = 150f;
                }
            }

            jump();
            if (jump)
            {
                superJump();
            }

            superJumpCharge();
            jump();

            doubleJumpCheck();
            wallRideCheck();
        }
    }

    /**
     * render is called every frame. Render should be used while the player is in a level.
     *
     * @param spriteBatch
     * @param delta
     */
    public void render(SpriteBatch spriteBatch,float delta)
    {
        this.spriteBatch = spriteBatch;
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        input();
        changeAnimationSpeed(delta);
        update(delta);
        renderMovement(spriteBatch);
        //drawSprite("standing", position.x, position.y);
        spriteBatch.end();
        //System.out.println("Sprite X:" + sprite.getX() + " Sprite Y:" + sprite.getY());
        //System.out.println("Bounding X:" + bounds.getX() + " Bounding Y:" + bounds.getY());
    }

    protected float xOffset = 5f;
    protected float yOffset = 7f;
    /**
     * renderMovement() controls movement and will display the correct sprite depending on what action is being performed
     * The method uses drawSprite(), and changes by using the enum State.
     *
     * TODO: Implement a new variable to position.x and position.y. I'd like to offset the sprite if it is necessary
     */
    public void renderMovement(SpriteBatch spriteBatch)
    {
        runningElapsedTime += Gdx.graphics.getDeltaTime();
        standingElapsedTime += Gdx.graphics.getDeltaTime();
        // If elapsedTime is left uncapped, it causes the current implementation of animation to continuously go faster
        // as long as the game is active. Until the animation implementation changes, the elapsed time is to remain capped.
        // A cap of two to four seems to work fine.
        // Update: There was a looping error, causing the animation to abruptly cut in the middle of it and reset.
        // Setting the cap to be the frameDuration * the amount of frames (in this case, 4) seems to fix the looping error.
        if (runningElapsedTime >= (runningFrameDuration * 4f))
        {
            runningElapsedTime = 0;
        }
        if (standingElapsedTime >= (standingFrameDuration * 6f))
        {
            standingElapsedTime = 0;
        }
        //System.out.println();
        if (velocity.x < 0)
        {
            facingRight = false;
        } else if (velocity.x > 0)
        {
            facingRight = true;
        }

        if (lookingDown && grounded)
        {
            HEIGHT = 30f;
        } else
        {
            HEIGHT = 60f;
        }

        switch (state)
        {
            case STANDING:
                //drawSprite("standing", position.x, position.y);
                if (facingRight && !sprite.isFlipX()) {
                    // Flip the sprite horizontally
                    standingFlippedFrame.setRegion(standingAnimation.getKeyFrame(standingElapsedTime,true));
                    //flippedFrame = new TextureRegion(animation.getKeyFrame(elapsedTime,true));
                    standingFlippedFrame.flip(true, false);
                    spriteBatch.draw(standingFlippedFrame, position.x - (WIDTH / 2f) - xOffset, position.y - yOffset, SPRITE_WIDTH, SPRITE_HEIGHT);
                } else
                {
                    standingFlippedFrame.setRegion(standingAnimation.getKeyFrame(standingElapsedTime,true));
                    //flippedFrame = new TextureRegion(animation.getKeyFrame(elapsedTime,true));
                    standingFlippedFrame.flip(false, false);
                    spriteBatch.draw(standingFlippedFrame, position.x - (WIDTH / 2f) - xOffset, position.y - yOffset, SPRITE_WIDTH, SPRITE_HEIGHT);
                }
                break;
            case WALKING:
                // I don't know why this works but... for know it works fine.
                // This is very flawed, as its using "sprite" even though this block of code doesn't rely on sprite at all.
                // That being said, it's a great way to check the direction of the player.
                if (facingRight && !sprite.isFlipX()) {
                    // Flip the sprite horizontally
                    runningFlippedFrame.setRegion(runningAnimation.getKeyFrame(runningElapsedTime,true));
                    //flippedFrame = new TextureRegion(animation.getKeyFrame(elapsedTime,true));
                    runningFlippedFrame.flip(true, false);
                    spriteBatch.draw(runningFlippedFrame, position.x - (WIDTH / 2f) - 5f, position.y - spriteYPosition, SPRITE_WIDTH, SPRITE_HEIGHT);
                } else
                {
                    runningFlippedFrame.setRegion(runningAnimation.getKeyFrame(runningElapsedTime,true));
                    //flippedFrame = new TextureRegion(animation.getKeyFrame(elapsedTime,true));
                    runningFlippedFrame.flip(false, false);
                    spriteBatch.draw(runningFlippedFrame, position.x - (WIDTH / 2f) - 5f, position.y - spriteYPosition, SPRITE_WIDTH, SPRITE_HEIGHT);
                }
                break;
            case JUMPING:
                drawSprite("jumping", position.x, position.y);
                break;
            case FALLING:
                drawSprite("falling", position.x, position.y);
                break;
            case WALL_RIDING:
                drawSprite("wallriding", position.x, position.y);
                break;
            case LOOKING_UP:
                drawSprite("lookingUp", position.x, position.y - spriteYPosition);
                break;
            case LOOKING_DOWN:
                drawSprite("lookingDown", position.x, position.y - spriteYPosition);
                break;
            case TOUCHING_WALL:
                drawSprite("touchingWall", position.x, position.y);
                break;
            case ATTACKING:
                drawSprite("attacking", position.x, position.y);
                break;
            case DEFENDING:
                drawSprite("defending", position.x, position.y);
                break;
            case PUNCHING:
                drawSprite("punching", position.x, position.y);
                break;
            case STANCE:
                drawSprite("stance", position.x, position.y);
                break;
        }
    }

    public void changeAnimationSpeed(float delta)
    {
        runningFrameDuration = baseFrameDuration / (1 + velocitySensitivity * Math.abs(velocity.x));
        //System.out.println(frameDuration);
        runningAnimation.setFrameDuration(runningFrameDuration);
        standingAnimation.setFrameDuration(standingFrameDuration);
    }

    /**
     * renderBattle is a render screen that is used for the battle screen.
     * When in battle the player cannot move and they are locked into the standing animation (for now... until I
     * figure out what animation is)
     *
     * @param spriteBatch SpriteBatch
     * @param delta float
     * @param scale float, how large the sprites will be rendered.
     */
    public void renderBattle(SpriteBatch spriteBatch,float delta, float scale)
    {
        this.spriteBatch = spriteBatch;
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        //input();
        updateBattle(delta, scale);
        //drawSpriteBattle("stance", position.x, position.y, scale);
        switch(state)
        {
            case ATTACKING:
                drawSpriteBattle("attacking", position.x, position.y,scale);
                break;
            case DEFENDING:
                drawSpriteBattle("defending", position.x, position.y,scale);
                break;
            case STANCE:
                drawSpriteBattle("stance", position.x, position.y,scale);
                break;
        }
        spriteBatch.end();
    }

    /**
     * Allows the player to jump. If they are grounded, first the player is moved up one pixel, then jump velocity is applied
     */
    public float jumpTime = 0;
    public boolean jumpHeld = false;
    public boolean jumpReleased = false;
    public boolean jumpPerformed = false;

    /**
     * jump() replace the older jump method. This method is supposed to implement a smoother jump movement,
     * being dependent on how long the player presses the jump button. The method is similar to the original jump method,
     * except in this case when the button is released it cuts the velocity in half immediately.
     * Works with NewPlatformerInput to function properly.
     */
    public void jump()
    {
        if (jumpHeld)
        {
            jump = true;
            if (grounded && !lookingDown) {
                //position.y += 1;
                if (jumpPerformed) {
                    velocity.y = 450f;
                    jumpPerformed = false;
                }
            }
            jumpTime += Gdx.graphics.getDeltaTime();
        } else if(jumpReleased)
        {
            //position.y += 1;
            if (velocity.y >= 0)
            {
                velocity.y = velocity.y / 2f;
            }
            jumpTime = 0;
            jumpReleased = false;
            jump = false;
        }
    }



    /**
     * Allows the player to super jump. Similar to the jump() method
     * TODO: Player has to hold down for at least 2 seconds before a super jump is granted. In its current state it can be spammed
     */
    protected boolean superJumpReady = false;
    protected float superJumpCharge = 0f;
    protected float superJumpChargeTime = 1f;
    public void superJump()
    {
        if (grounded && jump && isLookingDown() && superJumpReady)
        {
            System.out.println("Super Jump");
            position.y +=1;
            velocity.y = JUMP_VELOCITY * 1.3f;
            superJumpReady = false;
            jump = false;
        }
    }

    public void superJumpCharge()
    {
        if (lookingDown && grounded && velocity.x == 0) {
            if (lookingDown && !superJumpReady) {
                superJumpCharge += Gdx.graphics.getDeltaTime();
                System.out.println(superJumpCharge);
                if (superJumpCharge >= superJumpChargeTime) {
                    System.out.println("Super jump ready");
                    superJumpCharge = 0;
                    superJumpReady = true;
                }
            }
        } else
        {
            superJumpCharge = 0;
        }
    }

    /**
     * Allows the player to wall jump.
     * Current bugs:
     * - When the player attempts to wall jump, It seems to only work when the velocity is going down, or when the player
     * is holding jump and running at a wall.
     * - The player can scale the wall
     *
     * TODO: Bug fix
     */
    public void wallJump()
    {
        if (!grounded && touchingWall)
        {
            if (touchingLeftWall)
            {
                position.y += 1;
                velocity.x = -JUMP_VELOCITY / 2f;
                velocity.y = JUMP_VELOCITY;
            } else if (touchingRightWall)
            {
                position.y -= 1;
                velocity.x = JUMP_VELOCITY / 2f;
                velocity.y = JUMP_VELOCITY;
            }
            //grounded = false;
        }
    }

    /**
     * Allows the player to double jump.
     */
    public void doubleJump()
    {
        if (!grounded)
        {
            System.out.println("Double Jump");
            if (rightMove && !doubleJumped) // If the player is holding left they will go forwards diagonally.
            {
                velocity.y = JUMP_VELOCITY;
                velocity.x = JUMP_VELOCITY / 2f;
                doubleJumped = true;
            }else if (leftMove && !doubleJumped) // If the player is holding left they will go backwards diagonally
            {
                velocity.y = JUMP_VELOCITY;
                velocity.x = -JUMP_VELOCITY / 2f;
                doubleJumped = true;
            }
            if (!doubleJumped)
            {
                velocity.y = JUMP_VELOCITY;
                doubleJumped = true;
            }

        }
    }

    /**
     * Checks to see if the player already double jumped.
     * Used within newInput()
     */
    public void doubleJumpCheck()
    {
        if (grounded && doubleJumped)
        {
            doubleJumped = false;
        }
    }

    /**
     * dash allows the player to dash forward.
     * dash works together with dashing() to handle timing.
     *
     * TODO: Possibly fuse the two methods to make it less messy
     *
     */
    public void dash()
    {
        if(allowedToDash) {

            if (rightMove) {
                dashing();
                velocity.x += 300f;

            } else if (leftMove) {
                dashing();
                velocity.x -= 300f;
            }
        }
    }

    /**
     * dashing is intended to work together with dash(). Dashing handles the timer based actions.
     *
     * The method marks the player that they have dashed, and doesn't allow them to do the action again for a specified
     * amount of time that can be found under "dashTimer"
     */
    protected float dashTimer = 1f;
    public void dashing()
    {
        dashing = true;
        allowedToDash = false;
        System.out.println("Dash");
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                // Code to execute after the delay
                dashing = false;
            }
        },0.2f);

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                // Code to execute after the delay
                allowedToDash = true;
            }
        },dashTimer);

    }

    /**
     * Allows the player to hang onto the wall, a.k.a. wall-ride
     *
     * This method has a few things making it work. If the player is touching a wall and either right or left is being held,
     * wallRiding is set to true, which is handled by wallRideCheck().
     * Within the input class, key down and key up help control when wallRiding is set to true or false.
     *
     * TODO: Fix bug that allows the player to continue to wallride despite not touching a wall
     *
     */
    public void wallRide()
    {
        if ((touchingLeftWall && rightMove) || (touchingRightWall && leftMove))
        {
            System.out.println("wallride");
            wallRiding = true;
        } else
        {
            wallRiding = false;
        }
    }

    /**
     * Checks to see if wallRiding is set to true or false. Sets the player's velocity to half of the max velocity
     * if wallRiding is true.
     */
    public void wallRideCheck()
    {
        if (wallRiding)
        {
            velocity.y = -150f;
            // TODO: Create a wall ride variable
            // When modifying the maximum velocity, this variable would be affected. For example, changing it to
            // 500f would cause the wall ride variable to change to 250f instead of 150f which is annoying.
        }
    }

    private float attackInterval = 0.2f;
    boolean attackUp = false;
    boolean attackDown = false;
    /**
     * Allows the player to attack. When the attack button is pressed, a box is placed in front of the player briefly.
     *
     * This method works with attackRender()
     * TODO: Have the animation play separate from the attack
     * TODO: Think of better names then "attacking"
     */
    public void attack()
    {
        attack = true;
        spriteAttacking = true;
        //attack = false;
        if (lookingUp)
        {
            attackUp = true;
        } else if (lookingDown)
        {
            attackDown = true;
        }
        attackLogic();
        // Jump to attackRender() //
        System.out.println("Hitbox present");
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                // Code to execute after the delay
                attackHitbox = null;
                spriteAttacking = false;
                System.out.println("HitBox removed");
                attack = false;
                attackUp = false;
                attackDown = false;
            }
        }, attackInterval);
    }

    /**
     * deployAttack deals damage to the enemy and then sets attack to "false" to stop the player from dealing damage
     * @param e Enemy
     */
    public void deployAttack(Enemy e)
    {
        e.setHealth(e.getHealth() - (5f * random.nextInt(1,3)));
        if (facingRight) {
            e.setVelocity(e.velocity.x + 150f, e.velocity.y + 150f);
        } else
        {
            e.setVelocity(e.velocity.x - 150f, e.velocity.y + 150f);
        }
        attack = false;
    }

    /**
     * Renders the hitbox for attack.
     * This is more for debugging purposes and this method will soon include something such as an attack image
     */
    public void attackRender()
    {
        try {
            if (attack) {
                attackLogic();
            } else
            {
                attackHitbox = null;
            }
        } catch (Exception e)
        {
            // what
        }
    }

    /**
     * AttackLogic is a method that condenses the code within attack() and attackRender()
     * Both methods had the same exact code. This is to simplify the code.
     * attack and attackRender cannot be combined, as they both serve different functions. attack() is linked to the
     * actual key press, which can only be done once. attackRender is linked to the render method. This is so that it
     * constantly shows where the hitbox is as long as the hitbox is active.
     * I suppose there could be a way to fuse the methods depending on what is passed into the method, but for now
     * this works.
     */
    public void attackLogic()
    {
        if (attackUp)
        {
            attackHitbox = new Rectangle(position.x + (WIDTH / 5f), position.y + HEIGHT, 25f, 25f);
        } else if (attackDown)
        {
            attackHitbox = new Rectangle(position.x + (WIDTH / 5f),position.y - 25f, 25f, 25f);
        } else
        {
            if (facingRight) {
                attackHitbox = new Rectangle(position.x + WIDTH, position.y + (HEIGHT / 3f), 25f, 25f);
            } else {
                attackHitbox = new Rectangle(position.x - WIDTH, position.y + (HEIGHT / 3f), 25f, 25f);
            }
        }
    }

    /**
     * Handles how the player character responds to getting hurt.
     *
     * Currently, when the player is hurt they will bounce back. The direction of how far they get thrown back is hard coded,
     * and is dependent on which direction the player is facing
     * When the player is hit,
     * @param e
     */
    float invincibleTimer = 3f;
    public void hurt(Enemy e)
    {
        if (!invincible)
        {
            invincible = true;
            health = health - e.attackPoints;
            if (facingRight) {
                velocity.set(-200, 200);
            } else {
                velocity.set(200, 200);
            }
            //Allows the player to be invincible
            // TODO: Is Timer the best thing to use?
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    // Code to execute after the delay
                    invincible = false;
                }
            },invincibleTimer);
        }

    }

    public void itemCollected(Item item)
    {
        collectedItems.add(item);
    }

    public void updateCamera(OrthographicCamera camera)
    {
        this.camera = camera;
    }

    /**
     * Spaghetti code :^)
     *
     * Updates the player each frame, which in this case is delta.
     *
     * First thing that is checked is input, next bounding box and sprite is edited.
     * UPDATE: All code relating to collision was moved to World.java.
     * TODO: Refactor sprite so that it doesn't look horrible
     * TODO: Convert to switch block. This is gross
     *
     * Afterwards, collision code is checked.
     *
     * @param delta
     */
    public void update(float delta)
    {

        sprite.setBounds(
                position.x,
                position.y,
                WIDTH,
                HEIGHT);
        // Update position based on velocity
        bounds.setPosition(position.x, position.y); // Update the bounds with the new position
        //position.add(velocity.x * delta, velocity.y * delta);
        if (grounded) {
            if (velocity.x == 0 && !lookingUp && !lookingDown) {
                state = State.STANDING;
            } else if (lookingUp && velocity.x == 0)
            {
                state = State.LOOKING_UP;
            } else if (lookingDown && velocity.x == 0)
            {
                state = State.LOOKING_DOWN;
            } else
            {
                state = State.WALKING;
            }
        } else if (!wallRiding)
        {
            if (velocity.y > 0) {
                state = State.JUMPING;
            } else if (velocity.y < 0) {
                state = State.FALLING;
            }
        }

        if (touchingWall && wallRiding) {
            state = State.WALL_RIDING;
        }

        if (spriteAttacking)
        {
            state = State.PUNCHING;
        }

        if (dashing)
        {
            state = State.ATTACKING;
        }

        //hitbox render test
        attackRender();

    }

    public void updateBattle(float delta, float scale)
    {
        sprite.setBounds(
                position.x,
                position.y,
                WIDTH * scale,
                HEIGHT * scale);
        // Update position based on velocity
        bounds.setPosition(position.x, position.y); // Update the bounds with the new position
        facingRight = false;
        //state = State.STANDING;
    }

    /**
     * Adds sprites to the hashmap located within player.
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
     * Draws the sprite on screen.
     * TODO: Currently messing around with bounds and sprite bounds. Create a variable dedicated to the sprite bounds.
     * @param name The name is the hash map key.
     * @param x X position of the sprite
     * @param y Y position of the sprite
     */
    protected void drawSprite(String name, float x, float y)
    {
        Sprite sprite = sprites.get(name);

        sprite.setBounds(x - (WIDTH / 2f) - 5f,y,SPRITE_WIDTH,SPRITE_HEIGHT);

        if (facingRight && !sprite.isFlipX())
        {
            sprite.flip(true,false);
        } else if (!facingRight && sprite.isFlipX())
        {
            sprite.flip(true, false);
        }
        sprite.draw(spriteBatch);
    }

    /**
     * drawSpriteBattle draws the sprite on screen inside a battle screen.
     * Going to be honest... I made this method so that I can scale up the sprite, and in hindsight I could have just
     * overloaded drawSprite.
     * @param name name of the sprite to be drawn
     * @param x x coordinate
     * @param y y coordinate
     * @param scale scale to draw the sprite at
     */
    protected void drawSpriteBattle(String name, float x, float y, float scale)
    {
        Sprite sprite = sprites.get(name);

        sprite.setBounds(x,y,(SPRITE_WIDTH) * scale,(SPRITE_HEIGHT) * scale);

        if (facingRight && sprite.isFlipX())
        {
            sprite.flip(true,false);
        } else if (!facingRight && !sprite.isFlipX())
        {
            sprite.flip(true, false);
        }
        sprite.draw(spriteBatch);
    }

    private boolean menuPressed = false;
    public boolean menuPressed()
    {
        return menuPressed;
    }

    //                      //
    //Setters and Getters   //
    //                      //

    public Vector2 getPosition()
    {
        return position;
    }

    public void setVelocity(float x, float y)
    {
        velocity.set(x, y);
    }

    public Vector2 getVelocity()
    {
        return velocity;
    }

    public Rectangle getBounds()
    {
        return bounds;
    }

    public boolean isGrounded()
    {
        return grounded;
    }

    public void setGrounded(boolean grounded)
    {
        this.grounded = grounded;
    }

    public boolean isTouchingWall()
    {
        return touchingWall;
    }

    public void setTouchingWall(boolean touchingWall)
    {
        this.touchingWall = touchingWall;
    }

    public boolean isTouchingLeftWall()
    {
        return touchingLeftWall;
    }

    public void setTouchingLeftWall(boolean touchingLeftWall)
    {
        this.touchingLeftWall = touchingLeftWall;
    }

    public boolean isTouchingRightWall()
    {
        return touchingRightWall;
    }

    public void setTouchingRightWall(boolean touchingRightWall)
    {
        this.touchingRightWall = touchingRightWall;
    }

    public boolean isTouchingCeiling()
    {
        return touchingCeiling;
    }

    public void setTouchingCeiling(boolean touchingCeiling)
    {
        this.touchingCeiling = touchingCeiling;
    }

    public void setSCALE(float f)
    {
        SCALE = f;
    }

    public float getHeight()
    {
        return HEIGHT;
    }

    public float getWidth()
    {
        return WIDTH;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public void setPositionX(float x)
    {
        position.x = x;
    }

    public void setPositionY(float y)
    {
        position.y = y;
    }

    public PlatformerInput getPlatformerInput()
    {
        return platformerInput;
    }

    public void setPlatformerInput(PlatformerInput platformerInput)
    {
        this.platformerInput = platformerInput;
    }

    public float getHealth()
    {
        return health;
    }

    public void setHealth(float health)
    {
        this.health = health;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public boolean isDisableControls()
    {
        return disableControls;
    }

    public void setDisableControls(boolean disableControls)
    {
        this.disableControls = disableControls;
    }

    public static float getMaxVelocity()
    {
        return MAX_VELOCITY;
    }

    public void setBounds(Rectangle bounds)
    {
        this.bounds = bounds;
    }

    public static void setMaxVelocity(float maxVelocity)
    {
        MAX_VELOCITY = maxVelocity;
    }

    public void setLeftMove(boolean t)
    {
        if(rightMove && t) rightMove = false;
        leftMove = t;
    }
    public void setRightMove(boolean t)
    {
        if(leftMove && t) leftMove = false;
        rightMove = t;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getAttackPoints()
    {
        return attackPoints;
    }

    public void setAttackPoints(float attackPoints)
    {
        this.attackPoints = attackPoints;
    }

    public float getDefensePoints()
    {
        return defensePoints;
    }

    public void setDefensePoints(float defensePoints)
    {
        this.defensePoints = defensePoints;
    }

    public float getMagic()
    {
        return magic;
    }

    public void setMagic(float magic)
    {
        this.magic = magic;
    }

    public float getWisdom()
    {
        return wisdom;
    }

    public void setWisdom(float wisdom)
    {
        this.wisdom = wisdom;
    }

    public float getSpeed()
    {
        return speed;
    }

    public void setSpeed(float speed)
    {
        this.speed = speed;
    }

    public boolean isNpcInteraction() {
        return npcInteraction;
    }

    public void setNpcInteraction(boolean npcInteraction) {
        this.npcInteraction = npcInteraction;
    }

    public boolean isItemInteraction () {
        return itemInteraction;
    }

    public void setItemInteraction (boolean itemInteraction)
    {
        this.itemInteraction = itemInteraction;
    }

    public boolean isDownMove()
    {
        return downMove;
    }

    public void setDownMove(boolean downMove)
    {
        this.downMove = downMove;
    }

    public boolean isNextMessage() {
        return nextMessage;
    }

    public void setNextMessage(boolean nextMessage) {
        this.nextMessage = nextMessage;
    }

    public boolean isDisplayMessage() {
        return displayMessage;
    }

    public void setDisplayMessage(boolean displayMessage) {
        this.displayMessage = displayMessage;
    }

    public boolean isLookingUp() {
        return lookingUp;
    }

    public void setLookingUp(boolean lookingUp) {
        this.lookingUp = lookingUp;
    }

    public boolean isLookingDown() {
        return lookingDown;
    }

    public void setLookingDown(boolean lookingDown) {
        this.lookingDown = lookingDown;
    }

    public boolean isLeftMove() {
        return leftMove;
    }

    public boolean isRightMove() {
        return rightMove;
    }

    public boolean isSpriteAttacking() {
        return spriteAttacking;
    }

    public void setSpriteAttacking(boolean spriteAttacking) {
        this.spriteAttacking = spriteAttacking;
    }

    public boolean isDefending() {
        return defending;
    }

    public void setDefending(boolean defending) {
        this.defending = defending;
    }

    public boolean isJump() {
        return jump;
    }

    public void setJump(boolean jump) {
        this.jump = jump;
    }

    public State getState()
    {
        return state;
    }

    public void setState(State state)
    {
        this.state = state;
    }

    public Status getStatus()
    {
        return status;
    }

    public void setStatus(Status status)
    {
        this.status = status;
    }

    public LinkedList<Item> getCollectedItems() {
        return collectedItems;
    }

    public void setCollectedItems(LinkedList<Item> collectedItems) {
        this.collectedItems = collectedItems;
    }

    public Rectangle getAttackHitbox() {
        return attackHitbox;
    }

    public void setAttackHitbox(Rectangle attackHitbox) {
        this.attackHitbox = attackHitbox;
    }

    public boolean isAttack() {
        return attack;
    }

    public void setAttack(boolean attack) {
        this.attack = attack;
    }

    public float getRunningFrameDuration() {
        return runningFrameDuration;
    }

    public void setRunningFrameDuration(float runningFrameDuration) {
        this.runningFrameDuration = runningFrameDuration;
    }

    public boolean isMenuPressed() {
        return menuPressed;
    }

    public void setMenuPressed(boolean menuPressed) {
        this.menuPressed = menuPressed;
    }

    public void dispose()
    {
        texture.dispose();
        textureAtlas.dispose();
        //spriteBatch.dispose();
    }
}

