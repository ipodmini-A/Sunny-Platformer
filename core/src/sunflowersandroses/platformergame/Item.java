package sunflowersandroses.platformergame;

import sunflowersandroses.platformergame.screens.GameScreen;
import sunflowersandroses.platformergame.screens.SlotsScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.LinkedList;
import java.util.Random;

/**
 * Item.java
 *
 * This class serves as a platform to be extended from to create collectable objects
 * TODO: Allow the player to collect objects
 */
public class Item
{
    protected final float HEIGHT = 30f;
    protected final float WIDTH = 30f;
    protected SpriteBatch spriteBatch;
    protected Texture texture;
    protected Sprite sprite;
    protected OrthographicCamera camera;
    protected Vector2 position;
    protected boolean touchingLeftWall;
    protected boolean touchingRightWall;
    protected boolean touchingWall;
    protected boolean touchingCeiling;
    protected Rectangle bounds;
    protected boolean grounded;
    protected boolean allowedToBeCollected;
    protected boolean allowedToBeInteractedWith;
    protected boolean collected;
    protected Vector2 velocity;
    protected ShapeRenderer shapeRenderer;
    protected BitmapFont font;
    protected LinkedList<String> messageList;
    protected int messageIndex;
    protected boolean displayMessage;
    protected String message;
    protected int id;

    public static class SlotMachine extends Item
    {
        Random random;
        public SlotMachine(float x, float y, int id)
        {
            super(x,y,false);
            this.id = id;
            position = new Vector2(x,y);
            velocity = new Vector2();
            grounded = true;
            bounds = new Rectangle(position.x, position.y, WIDTH, HEIGHT);
            bounds.setSize(WIDTH, HEIGHT); // Update the bounds size
            touchingCeiling = false;
            touchingLeftWall = false;
            touchingRightWall = false;
            touchingWall = false;

            allowedToBeCollected = false;
            // "collected" not being here might cause issues
            allowedToBeInteractedWith = true;

            random = new Random();

            position.x = x;
            position.y = y;

            font = new BitmapFont();
            shapeRenderer = new ShapeRenderer();

            messageIndex = 0;
            messageList = new LinkedList<String>();
            messageList.add("Your random number is ");
            messageList.add("Uhhhh ");

            texture = new Texture("slot-machine.png");
            sprite = new Sprite(texture);

            camera = new OrthographicCamera();
        }

        boolean generatedNumber = false;
        /**
         * Takes the player to the slot screen to play slots
         *
         * TODO: Rework how the player interacts with both NPS and items.
         * @param p
         */
        public void interact(Player p)
        {
            if (p.isItemInteraction()) {
                p.setItemInteraction(false);
                p.setDisplayMessage(false);
                GameScreen.game.setScreen(new SlotsScreen(GameScreen.game, p));
            }
        }
    }

    public Item(float x, float y, boolean allowedtoBeCollected)
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

        this.allowedToBeCollected = allowedtoBeCollected;
        collected = false;

        position.x = x;
        position.y = y;

        spriteBatch = new SpriteBatch();
        texture = new Texture("debugSquare.png");
        sprite = new Sprite(texture);

        camera = new OrthographicCamera();
    }

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

        allowedToBeCollected = true;
        collected = false;

        position.x = x;
        position.y = y;

        spriteBatch = new SpriteBatch();
        texture = new Texture("debugSquare.png");
        sprite = new Sprite(texture);

        camera = new OrthographicCamera();
    }
    public Item(float x, float y, int id)
    {
        this.id = id;
        position = new Vector2(x,y);
        velocity = new Vector2();
        grounded = true;
        bounds = new Rectangle(position.x, position.y, WIDTH, HEIGHT);
        bounds.setSize(WIDTH, HEIGHT); // Update the bounds size
        touchingCeiling = false;
        touchingLeftWall = false;
        touchingRightWall = false;
        touchingWall = false;

        allowedToBeCollected = true;
        collected = false;

        position.x = x;
        position.y = y;

        spriteBatch = new SpriteBatch();
        texture = new Texture("debugSquare.png");
        sprite = new Sprite(texture);

        camera = new OrthographicCamera();
    }

    private static Player p;
    private static boolean touchingPlayer = false;

    /**
     * interact serves as a default way for items to interact with the player. This was ripped from NonPlayableCharacter.
     * @param player
     */
    public void interact(Player player)
    {
        if (messageList != null && player.isItemInteraction()) {
            System.out.println("AA");
            p = player;
            if (touchingPlayer) {
                player.getVelocity().x = 0;
                //This plays after the message list is over
                if (messageIndex >= messageList.size()) {
                    // All messages have been displayed
                    resetDialogue();
                    player.setDisableControls(false);
                } else {
                    disablePlayerInput();

                    shapeRenderer.setAutoShapeType(true);
                    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                    shapeRenderer.setColor(255f / 255f, 165f / 255f, 0, 0.1f);
                    shapeRenderer.rect(100, 100, Gdx.graphics.getWidth() - 100, Gdx.graphics.getHeight() / 4f);
                    shapeRenderer.end();

                    // Creating a new SpriteBatch to get the font to display is not efficient.
                    // TODO: Fix this spritebatch issue
                    spriteBatch = new SpriteBatch();
                    spriteBatch.begin();
                    font.draw(spriteBatch, messageList.get(messageIndex), 120, 160);
                    spriteBatch.end();
                    spriteBatch.dispose();

                    if (player.isNextMessage()) {
                        player.setNextMessage(false);
                        messageIndex++;

                        if (messageIndex == messageList.size()) {
                            // All messages have been displayed
                            player.setDisableControls(false);
                        }
                    }
                }
            } else {
                // Player is not touching the NPC
                resetDialogue();
                player.setDisableControls(false);
            }
        }
    }

    public static Item itemSelector(int id, float x, float y)
    {
        switch (id) {
            case 0:
                Item genericItem = new Item(x, y, id);
                return genericItem;
            case 1:
                Item slotMachine = new SlotMachine(x,y,id);
                return slotMachine;
            default:
                Item defaultItem = new Item(x, y, id);
                return defaultItem;
        }
    }

    protected void resetDialogue() {
        messageIndex = 0;
        p.setNextMessage(false);
        p.setDisplayMessage(false);
        displayMessage = false;
    }

    public void disablePlayerInput()
    {
        p.setDisableControls(true);
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

    public boolean isAllowedToBeCollected() {
        return allowedToBeCollected;
    }

    public void setAllowedToBeCollected(boolean allowedToBeCollected) {
        this.allowedToBeCollected = allowedToBeCollected;
    }

    public boolean isTouchingPlayer() {
        return touchingPlayer;
    }

    public void setTouchingPlayer(boolean touchingPlayer) {
        this.touchingPlayer = touchingPlayer;
    }

    public boolean isDisplayMessage() {
        return displayMessage;
    }

    public void setDisplayMessage(boolean displayMessage) {
        this.displayMessage = displayMessage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void dispose()
    {
        texture.dispose();
        //spriteBatch.dispose();
    }
}
