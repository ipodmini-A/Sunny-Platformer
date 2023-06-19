package cchase.platformergame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;


/**
 * Player.java
 * Contains code that controls various aspects of the player. This class is intended to extend from, making various
 * characters from it. Currently, Enemy.java extends it.
 * Objects include the characters stats, their sprites, their moves, and their bounds.
 * TODO: As time goes on, Player may not be the best name for this class.
 */
public class Player
{
    private static final float GRAVITY = -1000f; // Adjust the gravity value as needed -1000f
    private static final float JUMP_VELOCITY = 450f; // Adjust the jump velocity as needed
    protected static final float HEIGHT = 60f;
    protected static final float WIDTH = 30f;
    protected static final float SPRITE_HEIGHT = 60f + 10f;
    protected static final float SPRITE_WIDTH = 60f + 10f; // I don't know why adding 10 makes the sprite the proper size.
    protected static float MAX_VELOCITY = 200f;
    private static float SCALE = 1f;
    protected String name;
    // Player stats
    protected float health;
    protected float attack;
    protected float defense;
    protected float magic;
    protected float wisdom;
    protected float speed;
    final HashMap<String, Sprite> sprites = new HashMap<String, Sprite>();

    private Texture texture;
    protected Sprite sprite;
    protected Vector2 position;
    protected Vector2 velocity;
    protected Rectangle bounds;
    private PlatformerInput platformerInput;
    private boolean grounded;
    private boolean touchingLeftWall;
    private boolean touchingRightWall;
    private boolean touchingWall;
    private boolean touchingCeiling;
    private boolean flying;
    protected OrthographicCamera camera;
    protected SpriteBatch spriteBatch;
    private TextureAtlas textureAtlas;
    private boolean disableControls;
    private boolean leftMove;
    private boolean rightMove;
    private boolean downMove;
    private boolean jump;
    private boolean displayMessage;
    private boolean nextMessage;
    private boolean npcInteraction;

    enum State
    {
        STANDING, WALKING, JUMPING, FALLING, WALL_RIDING, LOOKING_DOWN, LOOKING_UP,TOUCHING_WALL, ATTACKING, DEFENDING, STANCE
    }
    protected State state;
    protected boolean facingRight = false;
    protected boolean doubleJumped = false;
    protected boolean wallRiding = false;
    protected boolean lookingUp;
    protected boolean lookingDown;
    protected boolean attacking;
    protected boolean defending;

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
        attack = 10f;
        defense = 10f;
        touchingCeiling = false;
        touchingLeftWall = false;
        touchingRightWall = false;
        touchingWall = false;
        doubleJumped = false;
        lookingUp = false;
        lookingDown = false;
        attacking = false;
        defending = false;
        flying = false;
        npcInteraction = false;
        displayMessage = false;
        nextMessage = false;
        disableControls = false;
        bounds = new Rectangle(position.x, position.y, WIDTH, HEIGHT);
        bounds.setSize(WIDTH, HEIGHT); // Update the bounds size
        state = State.STANDING;

        textureAtlas = new TextureAtlas("sprites.txt");
        spriteBatch = new SpriteBatch();
        texture = new Texture("debugSquare.png");
        sprite = new Sprite(texture);

        addSprites();
        sprite.setSize(WIDTH,HEIGHT);

        platformerInput = new PlatformerInput(this);
        Gdx.input.setInputProcessor(platformerInput);

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
        attack = 10f;
        defense = 10f;
        touchingCeiling = false;
        touchingLeftWall = false;
        touchingRightWall = false;
        touchingWall = false;
        flying = false;
        bounds = new Rectangle(position.x, position.y, WIDTH, HEIGHT);
        bounds.setSize(WIDTH, HEIGHT); // Update the bounds size
        state = State.STANDING;

        textureAtlas = new TextureAtlas("sprites.txt");
        spriteBatch = new SpriteBatch();
        texture = new Texture("debugSquare.png");
        sprite = new Sprite(texture);

        addSprites();
        sprite.setSize(WIDTH,HEIGHT);

        platformerInput = new PlatformerInput(this);
        Gdx.input.setInputProcessor(platformerInput);

        System.out.println("Width: " + sprite.getWidth() + " Height: " + sprite.getHeight());
    }

    /**
     * input() controls the input for the player.
     *
     * @Depricated
     */
    public void input()
    {
        //platformerInput.update();

        if (!disableControls)
        {
            if (platformerInput.isLeftPressed())
            {
                if (velocity.x >= -1 * MAX_VELOCITY)
                {
                    velocity.x -= 5;
                } else
                {
                    velocity.x = -150;
                }
            }

            if (platformerInput.isRightPressed())
            {
                if (velocity.x <= MAX_VELOCITY)
                {
                    velocity.x += 5;
                } else
                {
                    velocity.x = 150;
                }
            }

            if (platformerInput.isUpPressed() && !flying)
            {
                jump();
            }

            if (platformerInput.isDownPressed())
            {
                velocity.y -= 5;
            }
            //System.out.println(touchingWall);
            //System.out.println(grounded);
            if (platformerInput.isUpPressed() && !grounded && touchingWall)
            {
                System.out.println("Walljump");
                wallJump();
            }

            if (platformerInput.isUpPressed() && !grounded)
            {
                System.out.println("Double jump");
                //TODO: When input is entered, it should only allow one input, as currently it is letting many through
                // Causing wacky behavior.
                //doubleJump();
            }
            if (grounded)
            {
                doubleJumped = false;
            }
            flying = platformerInput.isDebugPressed();
        }
        //System.out.println(flying);
    }
    public void newInput()
    {
        if (!disableControls) {
            if (leftMove) {
                if (velocity.x >= -1 * MAX_VELOCITY) {
                    velocity.x -= 10;
                } else {
                    velocity.x = -MAX_VELOCITY;
                }
            }
            if (rightMove) {
                if (velocity.x <= MAX_VELOCITY) {
                    velocity.x += 10;
                } else {
                    velocity.x = MAX_VELOCITY;
                }
            }
            if (jump)
            {
                jump();
            }
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
        newInput();
        //input();
        update(delta);
        renderMovement();
        //drawSprite("standing", position.x, position.y);
        spriteBatch.end();

        //System.out.println("Sprite X:" + sprite.getX() + " Sprite Y:" + sprite.getY());
        //System.out.println("Bounding X:" + bounds.getX() + " Bounding Y:" + bounds.getY());
    }

    /**
     * renderMovement() controls movement and will display the correct sprite depending on what action is being performed
     * The method uses drawSprite(), and changes by using the enum State.
     */
    public void renderMovement()
    {
        if (velocity.x < 0)
        {
            facingRight = true;
        } else if (velocity.x > 0)
        {
            facingRight = false;
        }

        switch (state)
        {
            case STANDING:
                drawSprite("standing", position.x, position.y);
                break;
            case WALKING:
                drawSprite("running", position.x, position.y);
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
                drawSprite("lookingUp", position.x, position.y);
                break;
            case LOOKING_DOWN:
                drawSprite("lookingDown", position.x, position.y);
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
            case STANCE:
                drawSprite("stance", position.x, position.y);
                break;
        }
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
    public void jump()
    {
        if (grounded && jump)
        {
            position.y += 1;
            velocity.y = JUMP_VELOCITY;
            jump = false;
            //grounded = false;
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
            if (rightMove && !doubleJumped) // If the player is holding left they will go forwards diagonally.
            {
                velocity.y = JUMP_VELOCITY;
                velocity.x = JUMP_VELOCITY;
                doubleJumped = true;
            }else if (leftMove && !doubleJumped) // If the player is holding left they will go backwards diagonally
            {
                velocity.y = JUMP_VELOCITY;
                velocity.x = -JUMP_VELOCITY;
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
     *
     * Currently bugged
     * TODO: Fix teleporting
     */
    public void dash()
    {
        if (rightMove)
        {
            velocity.x += 3000f;
        }else if (leftMove)
        {
            velocity.x -= 3000f;
        }
    }

    /**
     * Allows the player to hang onto the wall, a.k.a. wall-ride
     *
     * This method has a few things making it work. If the player is touching a wall and either right or left is being held,
     * wallRiding is set to true, which is handled by wallRideCheck().
     * Within the input class, key down and key up help control when wallRiding is set to true or false.
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
            velocity.y = -MAX_VELOCITY / 2f;
        }
    }


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
    private void drawSprite(String name, float x, float y)
    {
        Sprite sprite = sprites.get(name);

        sprite.setBounds(x - (WIDTH / 2f) - 5f,y,SPRITE_WIDTH,SPRITE_HEIGHT);

        if (facingRight && sprite.isFlipX())
        {
            sprite.flip(true,false);
        } else if (!facingRight && !sprite.isFlipX())
        {
            sprite.flip(true, false);
        }
        sprite.draw(spriteBatch);
    }

    /**
     * drawSpriteBattle draws the sprite on screen inside of a battle screen.
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

    public float getAttack()
    {
        return attack;
    }

    public void setAttack(float attack)
    {
        this.attack = attack;
    }

    public float getDefense()
    {
        return defense;
    }

    public void setDefense(float defense)
    {
        this.defense = defense;
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

    public boolean isAttacking() {
        return attacking;
    }

    public void setAttacking(boolean attacking) {
        this.attacking = attacking;
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

    public void dispose()
    {
        texture.dispose();
        textureAtlas.dispose();
    }
}

