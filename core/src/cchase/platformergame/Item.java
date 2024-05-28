package cchase.platformergame;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Item.java
 *
 * This class is supposed to show objects in the world.
 * TODO: Give objects collision and gravity
 * TODO: Allow the player to collect objects
 */
public class Item
{
    protected static final float HEIGHT = 30f;
    protected static final float WIDTH = 30f;
    SpriteBatch spriteBatch;
    Texture texture;
    Sprite sprite;
    OrthographicCamera camera;
    protected Vector2 position;
    private boolean touchingLeftWall;
    private boolean touchingRightWall;
    private boolean touchingWall;
    private boolean touchingCeiling;
    protected Rectangle bounds;
    protected boolean grounded;
    protected Vector2 velocity;
    float x;
    float y;


    public Item(float x, float y)
    {
        position = new Vector2(x,y);
        velocity = new Vector2();
        bounds = new Rectangle(position.x, position.y, WIDTH, HEIGHT);
        touchingCeiling = false;
        touchingLeftWall = false;
        touchingRightWall = false;
        touchingWall = false;
        this.x = x;
        this.y = y;
        spriteBatch = new SpriteBatch();
        texture = new Texture("debugSquare.png");
        sprite = new Sprite(texture);
        camera = new OrthographicCamera();
        grounded = true;
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
                WIDTH,
                HEIGHT);
        sprite.draw(spriteBatch);
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }

    public boolean isGrounded() {
        return grounded;
    }

    public void setGrounded(boolean grounded) {
        this.grounded = grounded;
    }

    public boolean isTouchingLeftWall() {
        return touchingLeftWall;
    }

    public void setTouchingLeftWall(boolean touchingLeftWall) {
        this.touchingLeftWall = touchingLeftWall;
    }

    public boolean isTouchingRightWall() {
        return touchingRightWall;
    }

    public void setTouchingRightWall(boolean touchingRightWall) {
        this.touchingRightWall = touchingRightWall;
    }

    public boolean isTouchingWall() {
        return touchingWall;
    }

    public void setTouchingWall(boolean touchingWall) {
        this.touchingWall = touchingWall;
    }

    public boolean isTouchingCeiling() {
        return touchingCeiling;
    }

    public void setTouchingCeiling(boolean touchingCeiling) {
        this.touchingCeiling = touchingCeiling;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2 velocity) {
        this.velocity = velocity;
    }

    public float getWidth() {
        return WIDTH;
    }

    public float getHeight() {
        return HEIGHT;
    }
}
