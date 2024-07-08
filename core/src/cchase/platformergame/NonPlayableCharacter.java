package cchase.platformergame;

import cchase.platformergame.screens.GameScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.bullet.softbody.btSoftBody;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;

import java.util.HashMap;
import java.util.LinkedList;

public class NonPlayableCharacter extends Player {
    HashMap<String, Sprite> overworldSprites = new HashMap<String, Sprite>();
    private TextureAtlas overworldTextureAtlas;
    private boolean touchingPlayer;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch spriteBatch;
    private BitmapFont font;
    private Skin skin;
    protected Stage stage;
    private Player player;
    private Rectangle interactionBound;
    private LinkedList<String> messageList;
    private LinkedList<Emotion> emotionList;
    private TypingLabel typingLabel;
    private int messageIndex;
    private int emotionIndex;
    private boolean displayMessage;
    private Window dialogueBox;
    private TextButton nextButton;
    private enum Emotion
    {
        NEUTRAL, HAPPY
    }
    private Emotion emotion;

    public NonPlayableCharacter(float x, float y) {
        super(x, y); // NonPlayableCharacter inherits everything from Player.java at first. Things such as sprites.
        textureAtlas = new TextureAtlas("npcsprites.txt");
        overworldTextureAtlas = new TextureAtlas("standing/npcstanding.txt");
        addSprites();
        font = new BitmapFont();
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        emotion = Emotion.NEUTRAL;

        stage = new Stage();
        //stage.setDebugAll(true);
        shapeRenderer = new ShapeRenderer();
        spriteBatch = new SpriteBatch();

        //TODO: Fuse emotion index and message index
        emotionIndex = 0;
        messageIndex = 0;
        emotionList = new LinkedList<>();
        messageList = new LinkedList<>();
        messageList.add("Hi!");
        emotionList.add(Emotion.HAPPY);
        messageList.add("I'm a generic NPC!");
        emotionList.add(Emotion.NEUTRAL);
        messageList.add("I can't really move yet but hopefully in the future I gain that ability");
        emotionList.add(Emotion.NEUTRAL);
        messageList.add("Goodbye!");
        emotionList.add(Emotion.NEUTRAL);
        GameScreen.multiplexer.addProcessor(stage);

        bounds.setSize(WIDTH, HEIGHT); // Update the bounds size
        interactionBound = new Rectangle(position.x - (WIDTH / 2f), position.y,WIDTH * 2f, HEIGHT);

        facingRight = true;

        typingLabel = new TypingLabel("", skin);

        // Create and set up the dialogue box
        dialogueBox = new Window("", skin);
        dialogueBox.setSize(500, 200);
        dialogueBox.setPosition(Gdx.graphics.getWidth() / 2f - 200, 50);
        dialogueBox.add(typingLabel).width(380).pad(10).row();
        typingLabel.setWrap(true);

        // Create the next button
        nextButton = new TextButton("Next", skin);
        nextButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                if (player.isDisplayMessage())
                {
                    player.setNextMessage(true);
                }
                /*
                if (player != null && player.isNextMessage()) {
                    player.setNextMessage(false);
                    messageIndex++;

                    if (messageIndex < messageList.size()) {
                        typingLabel.restart();
                        typingLabel.setText(messageList.get(messageIndex));
                    } else {
                        // All messages have been displayed
                        player.setDisableControls(false);
                        dialogueBox.setVisible(false);
                    }
                }

                 */
            }
        });

        dialogueBox.add(nextButton).pad(10);
        stage.addActor(dialogueBox);
        dialogueBox.setVisible(false); // Initially hidden
    }

    @Override
    public void input() {
        // Currently this is here to override input.
        position.add(velocity.x * dt, velocity.y * dt);
    }

    public void Message(Player player) {
        this.player = player;
        player.getVelocity().x = 0;
        if (!player.facingRight) {
            facingRight = true;
            //player.setPositionX(position.x - 28f);
            //player.setPositionX(position.x + getWidth() - 2f);
            //player.facingRight = true;
        } else
        {
            facingRight = false;
            //player.setPositionX(position.x - 28f);
        }

        if (touchingPlayer) {
            if (messageIndex >= messageList.size()) {
                // All messages have been displayed
                player.setDisableControls(false);
                setNpcInteraction(false);
                resetDialogue();
            } else {
                disablePlayerInput();

                if (!dialogueBox.isVisible()) {
                    dialogueBox.setVisible(true);
                    typingLabel.restart();
                    typingLabel.setText(messageList.get(messageIndex));
                    emotion = emotionList.get(messageIndex);
                }

                stage.act(Gdx.graphics.getDeltaTime());
                stage.draw();

                if (player.isNextMessage()) {
                    player.setNextMessage(false);
                    messageIndex++;

                    if (messageIndex < messageList.size()) {
                        typingLabel.restart();
                        typingLabel.setText(messageList.get(messageIndex));
                        emotion = emotionList.get(messageIndex);
                    } else {
                        // All messages have been displayed
                        player.setDisableControls(false);
                        dialogueBox.setVisible(false);
                    }
                }
                Sprite npcSprite = new Sprite(overworldSprites.get("npcStanding"));
                spriteBatch.begin();
                switch (emotion)
                {
                    case NEUTRAL:
                        npcSprite = new Sprite(overworldSprites.get("npcStanding"));
                        npcSprite.setPosition(Gdx.graphics.getWidth() - 400, 0);
                        break;
                    case HAPPY:
                        npcSprite = new Sprite(overworldSprites.get("npcStandingSmiling"));
                        npcSprite.setPosition(Gdx.graphics.getWidth() - 462, 0);
                        break;
                    default:
                        npcSprite = new Sprite(overworldSprites.get("npcStanding"));
                        npcSprite.setPosition(Gdx.graphics.getWidth() - 400, 0);
                        break;

                }
                //npcSprite.setPosition(Gdx.graphics.getWidth() - 400, 0);
                npcSprite.draw(spriteBatch);
                spriteBatch.end();
            }
        } else {
            // Player is not touching the NPC
            resetDialogue();
            player.setNpcInteraction(false);
            player.setDisableControls(false);
        }
    }


    /**
     * renderMovement() controls movement and will display the correct sprite depending on what action is being performed
     * The method uses drawSprite(), and changes by using the enum State.
     *
     * TODO: Implement a new variable to position.x and position.y. I'd like to offset the sprite if it is necessary
     */
    public void renderMovement(SpriteBatch spriteBatch)
    {
        runningElapsedTime += Gdx.graphics.getDeltaTime();
        standingElapsedTime += Gdx.graphics.getDeltaTime();
        // If elapsedTime is left uncapped, it causes the current implementation of animation to continuously go faster
        // as long as the game is active. Until the animation implementation changes, the elapsed time is to remain capped.
        // A cap of two to four seems to work fine.
        // Update: There was a looping error, causing the animation to abruptly cut in the middle of it and reset.
        // Setting the cap to be the frameDuration * the amount of frames (in this case, 4) seems to fix the looping error.
        if (runningElapsedTime >= (runningFrameDuration * 4f))
        {
            runningElapsedTime = 0;
        }
        if (standingElapsedTime >= (standingFrameDuration * 6f))
        {
            standingElapsedTime = 0;
        }
        //System.out.println();
        if (velocity.x < 0)
        {
            facingRight = false;
        } else if (velocity.x > 0)
        {
            facingRight = true;
        }

        if (lookingDown && grounded)
        {
            HEIGHT = 30f;
        } else
        {
            HEIGHT = 60f;
        }

        switch (state)
        {
            case STANDING:
                drawSprite("standing", position.x - xOffset, position.y - yOffset);
                break;
            case WALKING:
                // I don't know why this works but... for know it works fine.
                // This is very flawed, as its using "sprite" even though this block of code doesn't rely on sprite at all.
                // That being said, it's a great way to check the direction of the player.
                if (facingRight && !sprite.isFlipX()) {
                    // Flip the sprite horizontally
                    runningFlippedFrame.setRegion(runningAnimation.getKeyFrame(runningElapsedTime,true));
                    //flippedFrame = new TextureRegion(animation.getKeyFrame(elapsedTime,true));
                    runningFlippedFrame.flip(true, false);
                    spriteBatch.draw(runningFlippedFrame, position.x - (WIDTH / 2f) - 5f, position.y - spriteYPosition, SPRITE_WIDTH, SPRITE_HEIGHT);
                } else
                {
                    runningFlippedFrame.setRegion(runningAnimation.getKeyFrame(runningElapsedTime,true));
                    //flippedFrame = new TextureRegion(animation.getKeyFrame(elapsedTime,true));
                    runningFlippedFrame.flip(false, false);
                    spriteBatch.draw(runningFlippedFrame, position.x - (WIDTH / 2f) - 5f, position.y - spriteYPosition, SPRITE_WIDTH, SPRITE_HEIGHT);
                }
                break;
            case JUMPING:
                drawSprite("jumping", position.x, position.y);
                break;
            case FALLING:
                drawSprite("falling", position.x, position.y);
                break;
            case WALL_RIDING:
                drawSprite("wallriding", position.x, position.y);
                break;
            case LOOKING_UP:
                drawSprite("lookingUp", position.x, position.y - spriteYPosition);
                break;
            case LOOKING_DOWN:
                drawSprite("lookingDown", position.x, position.y - spriteYPosition);
                break;
            case TOUCHING_WALL:
                drawSprite("touchingWall", position.x, position.y);
                break;
            case ATTACKING:
                drawSprite("attacking", position.x, position.y);
                break;
            case DEFENDING:
                drawSprite("defending", position.x, position.y);
                break;
            case PUNCHING:
                drawSprite("punching", position.x, position.y);
                break;
            case STANCE:
                drawSprite("stance", position.x, position.y);
                break;
        }
    }

    public void update(float delta)
    {
        super.update(delta);
        interactionBound.setPosition(position.x - (WIDTH / 2f),position.y);
    }

    public void render(SpriteBatch spriteBatch, float delta)
    {
        super.render(spriteBatch, delta);
    }

    private void resetDialogue() {
        messageIndex = 0;
        if (player != null) {
            player.setNextMessage(false);
            player.setDisplayMessage(false);
        }
        displayMessage = false;
        dialogueBox.setVisible(false);
    }

    private void addSprites() {
        Array<TextureAtlas.AtlasRegion> regions = textureAtlas.getRegions();
        Array<TextureAtlas.AtlasRegion> overworldRegions = overworldTextureAtlas.getRegions();

        for (TextureAtlas.AtlasRegion region : regions) {
            Sprite sprite = textureAtlas.createSprite(region.name);
            sprites.put(region.name, sprite);
        }
        for (TextureAtlas.AtlasRegion region : overworldRegions) {
            Sprite overworldSprite = overworldTextureAtlas.createSprite(region.name);
            overworldSprites.put(region.name, overworldSprite);
        }
    }

    public void removeAllMessages()
    {
        messageList = new LinkedList<String>();
    }

    public void disablePlayerInput() {
        player.setDisableControls(true);
    }
    //                      //
    // Setters and Getters  //
    //                      //


    public LinkedList<String> getMessageList() {
        return messageList;
    }

    public void setMessageList(LinkedList<String> messageList) {
        this.messageList = messageList;
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

    public Rectangle getInteractionBound() {
        return interactionBound;
    }

    public void setInteractionBound(Rectangle interactionBound) {
        this.interactionBound = interactionBound;
    }

    public Emotion getEmotion() {
        return emotion;
    }

    public void setEmotion(Emotion emotion) {
        this.emotion = emotion;
    }
}
