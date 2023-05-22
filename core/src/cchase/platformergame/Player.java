package cchase.platformergame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Player {
    private Texture texture;
    private Sprite sprite;
    private Vector2 position;
    private Vector2 velocity;
    private Rectangle bounds;
    private PlatformerInput platformerInput;

    public Player(float x, float y) {
        texture = new Texture("debugSquare.png");
        sprite = new Sprite(texture, (int) x, (int) y, 32, 32);
        System.out.println("Width: " + sprite.getWidth() + " Height: " + sprite.getHeight());
        position = new Vector2(x, y);
        velocity = new Vector2();
        platformerInput = new PlatformerInput();
        bounds = new Rectangle(position.x, position.y, sprite.getWidth(), sprite.getHeight());
        bounds.setSize(sprite.getWidth(), sprite.getHeight()); // Update the bounds size
    }

    public void input() {
        platformerInput.update();
        if (platformerInput.isLeftPressed()) {
            velocity.x -= 5;
        }

        if (platformerInput.isRightPressed()) {
            velocity.x += 5;
        }

        if (platformerInput.isUpPressed()) {
            velocity.y += 5;
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

    public void setPosition(float x, float y) {
        position.x = x;
        position.y = y;
        bounds.setPosition(x, y); // Update the bounds position
    }

    public void setPosition(Vector2 position) {
        this.position = position;
        bounds.setPosition(position.x, position.y); // Update the bounds position
    }


    public Vector2 getPosition()
    {
        return position;
    }

    public void setVelocity(float x, float y) {
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
        position.add(velocity.x * delta, velocity.y * delta);

        input();
    }

    public void dispose()
    {
        texture.dispose();
    }


}

