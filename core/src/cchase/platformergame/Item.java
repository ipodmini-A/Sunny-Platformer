package cchase.platformergame;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Item.java
 *
 * This class is supposed to show objects in the world.
 * TODO: Give objects collision and gravity
 * TODO: Allow the player to collect objects
 */
public class Item
{
    SpriteBatch spriteBatch;
    Texture texture;
    Sprite sprite;
    OrthographicCamera camera;
    float x;
    float y;

    public Item(float x, float y)
    {
        this.x = x;
        this.y = y;
        spriteBatch = new SpriteBatch();
        texture = new Texture("debugSquare.png");
        sprite = new Sprite(texture);
        camera = new OrthographicCamera();
    }

    public void render(SpriteBatch spriteBatch,OrthographicCamera camera, float delta)
    {
        this.camera = camera;
        this.spriteBatch = spriteBatch;
        spriteBatch.setProjectionMatrix(this.camera.combined);
        spriteBatch.begin();
        update(delta);
        //drawSprite("standing", position.x, position.y);
        spriteBatch.end();
    }

    public void update(float delta)
    {
        sprite.setBounds(
                x,
                y,
                50,
                50);
        sprite.draw(spriteBatch);
    }


}
