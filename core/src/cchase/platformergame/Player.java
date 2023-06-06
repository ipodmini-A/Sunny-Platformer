package cchase.platformergame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;

public class Player
{
    private static final float GRAVITY = -1000f; // Adjust the gravity value as needed -1000f
    private static final float JUMP_VELOCITY = 450f; // Adjust the jump velocity as needed
    private static final float HEIGHT = 60f;
    private static final float WIDTH = 60f;
    private static float SCALE = 1f;
    final HashMap<String, Sprite> sprites = new HashMap<String, Sprite>();

    private Texture texture;
    private Sprite sprite;
    private Vector2 position;
    private Vector2 velocity;
    private Rectangle bounds;
    private PlatformerInput platformerInput;
    private boolean grounded;
    private boolean touchingLeftWall;
    private boolean touchingRightWall;
    private boolean touchingWall;
    private boolean touchingCeiling;
    private boolean flying;
    private OrthographicCamera camera;
    private SpriteBatch spriteBatch;
    private TextureAtlas textureAtlas;
    enum State
    {
        STANDING, WALKING, JUMPING
    }
    private State state;
    private boolean facingRight = false;

    public Player()
    {
        position = new Vector2(0,0);
        velocity = new Vector2();
        grounded = false;
        touchingCeiling = false;
        touchingLeftWall = false;
        touchingRightWall = false;
        touchingWall = false;
        flying = false;
        platformerInput = new PlatformerInput();
        bounds = new Rectangle(position.x, position.y, WIDTH, HEIGHT);
        bounds.setSize(WIDTH, HEIGHT); // Update the bounds size
        state = State.STANDING;

        textureAtlas = new TextureAtlas("sprites.txt");
        spriteBatch = new SpriteBatch();
        texture = new Texture("debugSquare.png");
        sprite = new Sprite(texture);

        addSprites();
        sprite.setSize(WIDTH,HEIGHT);

        System.out.println("Width: " + sprite.getWidth() + " Height: " + sprite.getHeight());
    }

    public Player(float x, float y)
    {
        position = new Vector2(x, y);
        velocity = new Vector2();
        grounded = false;
        touchingCeiling = false;
        touchingLeftWall = false;
        touchingRightWall = false;
        touchingWall = false;
        flying = false;
        platformerInput = new PlatformerInput();
        bounds = new Rectangle(position.x, position.y, WIDTH, HEIGHT);
        bounds.setSize(WIDTH, HEIGHT); // Update the bounds size
        state = State.STANDING;

        textureAtlas = new TextureAtlas("sprites.txt");
        spriteBatch = new SpriteBatch();
        texture = new Texture("debugSquare.png");
        sprite = new Sprite(texture);

        addSprites();
        sprite.setSize(WIDTH,HEIGHT);

        System.out.println("Width: " + sprite.getWidth() + " Height: " + sprite.getHeight());
    }

    public void input()
    {
        platformerInput.update();
        if (platformerInput.isLeftPressed())
        {
            velocity.x -= 5;
        }

        if (platformerInput.isRightPressed())
        {
            velocity.x += 5;
        }

        if (platformerInput.isUpPressed() && !flying)
        {
            jump();
        }

        if (platformerInput.isDownPressed())
        {
            velocity.y -= 5;
        }
        flying = platformerInput.isDebugPressed();
        //System.out.println(flying);
    }

    public void render(SpriteBatch spriteBatch,float delta)
    {
        this.spriteBatch = spriteBatch;
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        input();
        update(delta);
        if (velocity.x < 0)
        {
            facingRight = true;
        } else if (velocity.x > 0)
        {
            facingRight = false;
        }
        if (state == State.STANDING)
        {
            drawSprite("standing", position.x, position.y);
        } else if (state == State.WALKING)
        {
            drawSprite("running", position.x, position.y);
        } else if ( state == State.JUMPING)
        {
            drawSprite("jumping", position.x, position.y);
        }
        //drawSprite("standing", position.x, position.y);
        spriteBatch.end();
        //System.out.println("Sprite X:" + sprite.getX() + " Sprite Y:" + sprite.getY());
        //System.out.println("Bounding X:" + bounds.getX() + " Bounding Y:" + bounds.getY());
    }

    public void jump()
    {
        if (grounded)
        {
            position.y += 1;
            velocity.y = JUMP_VELOCITY;
            grounded = false;
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
        if (grounded && velocity.x == 0)
        {
            state = State.STANDING;
        }else if (grounded && velocity.x != 0)
        {
            state = State.WALKING;
        }else if (!grounded)
        {
            state = State.JUMPING;
        }
    }

    private void addSprites()
    {
        Array<TextureAtlas.AtlasRegion> regions = textureAtlas.getRegions();

        for (TextureAtlas.AtlasRegion region : regions) {
            Sprite sprite = textureAtlas.createSprite(region.name);

            sprites.put(region.name, sprite);
        }
    }

    private void drawSprite(String name, float x, float y)
    {
        Sprite sprite = sprites.get(name);

        sprite.setBounds(x,y,WIDTH + 10f,HEIGHT + 10f);

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

    public void dispose()
    {
        texture.dispose();
        textureAtlas.dispose();
        spriteBatch.dispose();
    }

}

