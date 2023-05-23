package cchase.platformergame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Player
{
    private static final float GRAVITY = -1000f; // Adjust the gravity value as needed
    private static final float JUMP_VELOCITY = 400f; // Adjust the jump velocity as needed

    private Texture texture;
    private Sprite sprite;
    private Vector2 position;
    private Vector2 velocity;
    private Rectangle bounds;
    private float gravity;
    private PlatformerInput platformerInput;
    private boolean grounded;

    public Player(float x, float y)
    {
        texture = new Texture("debugSquare.png");
        sprite = new Sprite(texture, (int) x, (int) y, 32, 32);
        System.out.println("Width: " + sprite.getWidth() + " Height: " + sprite.getHeight());
        position = new Vector2(x, y);
        velocity = new Vector2();
        grounded = false;
        platformerInput = new PlatformerInput();
        bounds = new Rectangle(position.x, position.y, sprite.getWidth(), sprite.getHeight());
        bounds.setSize(sprite.getWidth(), sprite.getHeight()); // Update the bounds size
    }

    public void input()
    {
        platformerInput.update();
        if (platformerInput.isLeftPressed()) {
            velocity.x -= 5;
        }

        if (platformerInput.isRightPressed()) {
            velocity.x += 5;
        }

        if (platformerInput.isUpPressed())
        {

            jump();
            //velocity.y += 5;
        }

        if (platformerInput.isDownPressed()) {
            velocity.y -= 5;
        }

        bounds.setPosition(position.x, position.y); // Update the bounds with the new position
    }

    public void render(SpriteBatch spriteBatch)
    {
        sprite.setPosition(position.x, position.y);
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

    public void update(float delta)
    {
        input();
        // Apply gravity
        if (grounded)
        {
            velocity.set(velocity.x,0);
            //velocity.add(0, 0);
        }else
        {
            velocity.add(0, GRAVITY * delta);
        }

        // Update position based on velocity
        position.add(velocity.x * delta, velocity.y * delta);

        // Check if the player is grounded
        grounded = position.y <= 0;

        // Reset vertical velocity if grounded
        if (grounded)
        {
            velocity.y = 0;
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

    public void dispose()
    {
        texture.dispose();
    }


}

