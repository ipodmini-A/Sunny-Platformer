package sunflowersandroses.platformergame;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import sunflowersandroses.platformergame.enums.Emotion;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import sunflowersandroses.platformergame.player.Player;

import java.util.HashMap;
import java.util.LinkedList;

public class NonPlayableCharacter extends Player {
    HashMap<String, Sprite> overworldSprites = new HashMap<String, Sprite>();
    private OrthographicCamera UICamera;
    private TextureAtlas dialogueTextureAtlas;
    private boolean touchingPlayer;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch spriteBatch;
    private BitmapFont font;
    private Skin skin;
    protected Stage stage;
    protected Viewport viewport;
    private Player player;
    private Rectangle interactionBound;
    private LinkedList<Dialogue.dialogueString> messageList;
    private int messageIndex;
    private boolean displayMessage;
    private Emotion emotion;
    private int characterID;
    private int messageID;
    public Dialogue dialogue;
    private HashMap<String, String> emotionSprites;

    public NonPlayableCharacter(float x, float y) {
        super(x, y); // NonPlayableCharacter inherits everything from Player.java at first. Things such as sprites.
        loadCharacterData(1);
        addSprites();
        font = new BitmapFont();
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        emotion = Emotion.NEUTRAL;

        UICamera = new OrthographicCamera();
        viewport = new FitViewport(1280, 720, UICamera);
        stage = new Stage(viewport);
        //stage.setDebugAll(true);

        shapeRenderer = new ShapeRenderer();
        spriteBatch = new SpriteBatch();

        messageIndex = 0;
        messageID = 0;
        messageList = Dialogue.getMessageGroup(messageID);
        LevelManager.multiplexer.addProcessor(stage);

        bounds.setSize(WIDTH, HEIGHT); // Update the bounds size
        interactionBound = new Rectangle(position.x - (WIDTH / 2f), position.y,WIDTH * 2f, HEIGHT);

        facingRight = true;

        viewport.apply();
        dialogue = new Dialogue(name);

    }

    @Override
    public void input(float delta) {
        // Currently this is here to override input.
        position.add(velocity.x * dt, velocity.y * dt);
    }

    /**
     * Used within Dialogue. Displays the specified sprite within the dialogue box.
     * @param spriteBatch sprite batch
     */
    public void dialogueCharacterDraw(SpriteBatch spriteBatch)
    {
        try {
            switch (emotion) {
                case NEUTRAL:
                    spriteBatch.draw(overworldSprites.get(emotionSprites.get("NEUTRAL")), UICamera.viewportWidth * (4 / 6f), 0);
                    break;
                case HAPPY:
                    spriteBatch.draw(overworldSprites.get(emotionSprites.get("HAPPY")), UICamera.viewportWidth * (4 / 6f), 0);
                    break;
                case NERVOUS:
                    spriteBatch.draw(overworldSprites.get(emotionSprites.get("NERVOUS")), UICamera.viewportWidth * (4 / 6f), 0);
                    break;
                case ANGRY:
                    spriteBatch.draw(overworldSprites.get(emotionSprites.get("ANGRY")), UICamera.viewportWidth * (4 / 6f), 0);
                    break;
                case TIRED:
                    spriteBatch.draw(overworldSprites.get(emotionSprites.get("TIRED")), UICamera.viewportWidth * (4 / 6f), 0);
                    break;
                default:
                    spriteBatch.draw(overworldSprites.get(emotionSprites.get("NEUTRAL")), UICamera.viewportWidth * (4 / 6f), 0);
                    break;
            }
        } catch (Exception e)
        {
            // Show nothing.
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

    private void addSprites() {
        Array<TextureAtlas.AtlasRegion> regions = textureAtlas.getRegions();
        Array<TextureAtlas.AtlasRegion> overworldRegions = dialogueTextureAtlas.getRegions();

        for (TextureAtlas.AtlasRegion region : regions) {
            Sprite sprite = textureAtlas.createSprite(region.name);
            sprites.put(region.name, sprite);
        }
        for (TextureAtlas.AtlasRegion region : overworldRegions) {
            Sprite overworldSprite = dialogueTextureAtlas.createSprite(region.name);
            overworldSprites.put(region.name, overworldSprite);
        }
    }

    public void loadCharacterData(int characterId) {
        JsonReader jsonReader = new JsonReader();
        JsonValue jsonData = jsonReader.parse(Gdx.files.internal("nonPlayableCharactersData/nonPlayableCharactersData.json"));

        for (JsonValue character : jsonData.get("Characters")) {
            if (character.getInt("id") == characterId) {
                name = character.getString("name");
                JsonValue emotions = character.get("emotions").get(0);
                dialogueTextureAtlas = new TextureAtlas(character.getString("dialogueTextureAtlas"));
                textureAtlas = new TextureAtlas(character.getString("spriteTextureAtlas"));

                emotionSprites = new HashMap<>();
                for (JsonValue emotion : emotions) {
                    emotionSprites.put(emotion.name(), emotion.asString());
                }
                break;
            }
        }
    }


    public void disablePlayerInput() {
        player.setDisableControls(true);
    }
    //                      //
    // Setters and Getters  //
    //                      //


    public LinkedList<Dialogue.dialogueString> getMessageList() {
        return messageList;
    }

    /**
     * Sets the messages using an ID system. Look at dialogue.csv for which message list you want to be displayed.
     * @param id
     */
    public void setMessageList(int id)
    {
        dialogue.setMessageIndex(0);
        //resetDialogue();
        dialogue.setMessageID(id);
        dialogue.setMessageList(Dialogue.getMessageGroup(id));
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

    public void setMessageList(LinkedList<Dialogue.dialogueString> messageList) {
        this.messageList = messageList;
    }

    public int getMessageIndex() {
        return messageIndex;
    }

    public void setMessageIndex(int messageIndex) {
        this.messageIndex = messageIndex;
    }
}
