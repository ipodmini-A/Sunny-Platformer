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
 * This class serves as a platform to be extended from to create collectable objects
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
    protected boolean collected;
    protected Vector2 velocity;


    public Item(float x, float y)
    {
        position = new Vector2(x,y);
        velocity = new Vector2();
        grounded = true;
        bounds = new Rectangle(position.x, position.y, WIDTH, HEIGHT);
        bounds.setSize(WIDTH, HEIGHT); // Update the bounds size
        touchingCeiling = false;
        touchingLeftWall = false;
        touchingRightWall = false;
        touchingWall = false;

        collected = false;

        position.x = x;
        position.y = y;

        spriteBatch = new SpriteBatch();
        texture = new Texture("debugSquare.png");
        sprite = new Sprite(texture);

        camera = new OrthographicCamera();
    }

    /**
     * Updates the items camera
     * Ensures that the item is utilizing the right camera
     * @param camera
     */
    public void updateCamera(OrthographicCamera camera)
    {
        this.camera = camera;
    }

    /**
     * Renders the item on screen.
     * Utilizes update and spriteBatch
     * @param spriteBatch
     * @param delta
     */
    public void render(SpriteBatch spriteBatch, float delta)
    {
        if (!collected) {
            this.spriteBatch = spriteBatch;
            spriteBatch.setProjectionMatrix(this.camera.combined);
            spriteBatch.begin();
            update(delta);
            spriteBatch.end();
        }
    }

    /**
     * Updates the position of the sprite
     * @param delta
     */
    public void update(float delta)
    {
        sprite.setBounds(
                position.x,
                position.y,
                WIDTH,
                HEIGHT);
        sprite.draw(spriteBatch);
        bounds.setPosition(position.x, position.y);
    }

    //                              //
    // Various setters and getters  //
    //                              //
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

    public boolean isCollected() {
        return collected;
    }

    public void setCollected(boolean collected) {
        this.collected = collected;
    }

    public void dispose()
    {
        texture.dispose();
        //spriteBatch.dispose();
    }
}
