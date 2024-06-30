package cchase.platformergame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;

import java.util.LinkedList;

public class NonPlayableCharacter extends Player {
    private boolean touchingPlayer = false;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch spriteBatch;
    private String message;
    private BitmapFont font;
    private Player player;
    private LinkedList<String> messageList;
    private int messageIndex;
    private boolean displayMessage;

    public NonPlayableCharacter(float x, float y)
    {
        super(x, y); // NonPlayableCharacter inherits everything from Player.java at first. Things such as sprites.
        textureAtlas = new TextureAtlas("npcsprites.txt");
        addSprites();
        font = new BitmapFont();
        shapeRenderer = new ShapeRenderer();
        spriteBatch = new SpriteBatch();
        messageIndex = 0;
        messageList = new LinkedList<String>();
        messageList.add("Hi!");
        messageList.add("I'm a generic NPC!");
        messageList.add("I can't really move yet but hopefully in the future I gain that ability");
        messageList.add("Goodbye!");
    }

    @Override
    public void input() {
        // Currently this is here to override input.
        position.add(velocity.x * dt, velocity.y * dt);
    }

    public void Message(Player player)
    {
        this.player = player;
        player.getVelocity().x = 0;
        if (touchingPlayer)
        {
            if (messageIndex >= messageList.size())
            {
                // All messages have been displayed
                player.setDisableControls(false);
                resetDialogue();
            } else
            {
                disablePlayerInput();
                shapeRenderer.setAutoShapeType(true);
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                shapeRenderer.setColor(255f / 255f, 165f / 255f, 0, 0.1f);
                shapeRenderer.rect(100, 100, Gdx.graphics.getWidth() - 100, Gdx.graphics.getHeight() / 4f);
                shapeRenderer.end();

                spriteBatch.begin();
                font.draw(spriteBatch, messageList.get(messageIndex), 120, 160);
                spriteBatch.end();

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

    private void resetDialogue() {
        messageIndex = 0;
        player.setNextMessage(false);
        player.setDisplayMessage(false);
        displayMessage = false;
    }

    private void addSprites()
    {
        Array<TextureAtlas.AtlasRegion> regions = textureAtlas.getRegions();

        for (TextureAtlas.AtlasRegion region : regions) {
            Sprite sprite = textureAtlas.createSprite(region.name);

            sprites.put(region.name, sprite);
        }
    }

    public void disablePlayerInput()
    {
        player.setDisableControls(true);
    }

    public boolean isTouchingPlayer() {
        return touchingPlayer;
    }

    public void setTouchingPlayer(boolean touchingPlayer) {
        this.touchingPlayer = touchingPlayer;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isDisplayMessage() {
        return displayMessage;
    }

    public void setDisplayMessage(boolean displayMessage) {
        this.displayMessage = displayMessage;
    }
}
