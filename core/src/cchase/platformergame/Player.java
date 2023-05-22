package cchase.platformergame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Player {

    private Texture texture;
    private Vector2 position;
    private Rectangle bounds;
    private PlatformerInput input;

    public Player(float x, float y) {
        texture = new Texture("debugSquare.png");
        position = new Vector2(x, y);
        input = new PlatformerInput();
        bounds = new Rectangle(position.x, position.y, texture.getWidth(), texture.getHeight());
    }

    public void update()
    {
        handleInput();
    }

    private void handleInput()
    {
        input.update();
        if (input.isLeftPressed())
        {
            position.x -= 5;
        }
        if (input.isRightPressed())
        {
            position.x += 5;
        }
        if (input.isUpPressed())
        {
            position.y += 5;
        }
        if (input.isDownPressed())
        {
            position.y -= 5;
        }
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x, position.y);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void dispose() {
        texture.dispose();
    }
}
