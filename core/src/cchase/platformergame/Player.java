package cchase.platformergame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Player
{
    private static final float GRAVITY = -1000f; // Adjust the gravity value as needed
    private static final float JUMP_VELOCITY = 400f; // Adjust the jump velocity as needed
    private static float SCALE = 1f;

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
        bounds = new Rectangle(position.x, position.y, 32, 32);
        bounds.setSize(32, 32); // Update the bounds size

        texture = new Texture("debugSquare.png");
        sprite = new Sprite(texture);
        sprite.setSize(bounds.getWidth(),bounds.getHeight());

        System.out.println("Width: " + sprite.getWidth() + " Height: " + sprite.getHeight());
    }

    public void input()
    {
        platformerInput.update();
        if (platformerInput.isLeftPressed()) {
            velocity.x -= 5;
        }

        if (platformerInput.isRightPressed())
        {
            velocity.x += 5;
        }

        if (platformerInput.isUpPressed() && !flying)
        {
            jump();
        } else
        {
            velocity.y += 5;
        }

        if (platformerInput.isDownPressed())
        {
            velocity.y -= 5;
        }
        flying = platformerInput.isDebugPressed();
        System.out.println(flying);
    }

    public void render(SpriteBatch spriteBatch,float delta)
    {
        input();
        update(delta);
        sprite.draw(spriteBatch);
    }

    public void setPosition(float x, float y)
    {
        position.x = x;
        position.y = y;
        bounds.setPosition(x, y); // Update the bounds position
    }

    public void setPosition(Vector2 position)
    {
        this.position = position;
        bounds.setPosition(position.x, position.y); // Update the bounds position
    }

    public void jump()
    {
        if (grounded)
        {
            velocity.y = JUMP_VELOCITY;
            grounded = false;
        }
    }


    public Vector2 getPosition()
    {
        return position;
    }

    public void setVelocityX(float x)
    {
        velocity.x = x;
    }

    public void setVelocityY(float y)
    {
        velocity.y = y;
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
     * TODO: Refactor sprite so that it doesn't look horrible
     *
     * Afterwards, collision code is checked.
     *
     * @param delta
     */
    public void update(float delta)
    {
        bounds.setPosition(position.x, position.y); // Update the bounds with the new position
        sprite.setBounds(getPosition().x, getPosition().y, bounds.width, bounds.height);
        //sprite.setBounds(getPosition().x, getPosition().y, getBounds().getWidth(), getBounds().getHeight());
        sprite.setSize(bounds.width * SCALE, bounds.height * SCALE);
        // Update position based on velocity
        float oldX = position.x;
        float oldY = position.y;
        position.add(velocity.x * delta, velocity.y * delta);

        // Apply gravity
        if (grounded)
        {
            velocity.y = 0;
            position.y = oldY;
        } else
        {
            velocity.add(0, GRAVITY * delta);
        }

        // Check if the player is trying to move into a wall
        if ((velocity.x < 0 && touchingWall))
        {
            velocity.x = 0; // Stop the player's horizontal movement
            position.x = oldX; // Reset the player's position to the previous x-coordinate
        }

        // Check if the player is trying to move into the ceiling
        if (velocity.y > 0 && touchingCeiling) {
            velocity.y = 0; // Stop the player's vertical movement
            position.y = oldY; // Reset the player's position to the previous y-coordinate
        }
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

    public void dispose()
    {
        texture.dispose();
    }


}

