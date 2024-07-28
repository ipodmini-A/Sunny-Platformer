package sunflowersandroses.platformergame;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;
import sunflowersandroses.platformergame.enums.Emotion;
import sunflowersandroses.platformergame.player.Player;

import static sunflowersandroses.platformergame.PlatformerGame.dialogueLines;


public class Dialogue {

    private OrthographicCamera UICamera;
    protected Viewport viewport;
    protected Stage stage;
    private SpriteBatch spriteBatch;
    private int messageIndex;
    private int messageID;
    private LinkedList<Dialogue.dialogueString> messageList;
    private TypingLabel typingLabel;
    private Window dialogueBox;
    private Skin skin;
    private TextButton nextButton;
    private TextButton choice1;
    private TextButton choice2;
    private TextureAtlas overworldTextureAtlas;
    private Player player;
    private NonPlayableCharacter NPC;

    public Dialogue(String name)
    {
        UICamera = new OrthographicCamera();
        viewport = new FitViewport(1280, 720, UICamera);
        stage = new Stage(viewport);
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        //stage.setDebugAll(true);

        spriteBatch = new SpriteBatch();

        messageIndex = 0;
        messageID = 0;
        messageList = Dialogue.getMessageGroup(messageID);
        LevelManager.multiplexer.addProcessor(stage);

        typingLabel = new TypingLabel("", skin);

        // Create and set up the dialogue box
        dialogueBox = new Window(name, skin);
        dialogueBox.setSize(UICamera.viewportWidth / 2f, 200);
        dialogueBox.setPosition((UICamera.viewportWidth / 4f), 50);
        dialogueBox.add(typingLabel).width(UICamera.viewportWidth / 3f).pad(10).row();
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
            }
        });

        choice1 = new TextButton("Choice1", skin);
        choice1.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (player.isDisplayMessage())
                {
                    //messageList = new LinkedList<>(Dialogue.getMessageGroup(1000 + messageID));
                   // messageList = new LinkedList<>(Dialogue.getMessageGroup);
                    messageList = new LinkedList<>(Dialogue.getMessageGroup(messageList.get(messageIndex).getOptions().get(0).getNextDialogueId()));
                    messageIndex = -1;
                    player.setNextMessage(true);
                    // add (Specific ID)
                }
            }
        });

        choice2 = new TextButton("Choice2", skin);
        choice2.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (player.isDisplayMessage())
                {
                    //messageList = new LinkedList<>(Dialogue.getMessageGroup(2000 + messageID));
                    messageList = new LinkedList<>(Dialogue.getMessageGroup(messageList.get(messageIndex).getOptions().get(1).getNextDialogueId()));
                    messageIndex = -1;
                    player.setNextMessage(true);
                    // add (Specific ID)
                }
            }
        });

        Table buttonTable = new Table();
        buttonTable.add(choice1).pad(10).left();
        buttonTable.add(nextButton).pad(10).center().width(100f);
        buttonTable.add(choice2).pad(10).right();
        dialogueBox.add(buttonTable).center().row();
        choice1.setVisible(false);
        choice2.setVisible(false);

        //dialogueBox.add(nextButton).pad(10);
        //dialogueBox.removeActor(nextButton);
        nextButton.setVisible(true);
        stage.addActor(dialogueBox);
        //stage.setDebugAll(true);
        dialogueBox.setVisible(false); // Initially hidden
        viewport.apply();
    }

    public static List<DialogueLine> loadDialogue(String filePath) {
        List<DialogueLine> dialogueLines = new ArrayList<>();

        // Read the JSON file
        JsonReader jsonReader = new JsonReader();
        JsonValue JsonData = jsonReader.parse(Gdx.files.internal(filePath).readString());

        // Iterate through each dialogue entry
        for (JsonValue dialogue : JsonData.get("dialogue")) {
            int id = dialogue.getInt("id");
            //String message = dialogue.getString("message");
            //String message = dialogue.get("messages").get("message").asString();
            for (JsonValue messages : dialogue.get("messages")) {
                String message = messages.getString("message");
                Emotion emotion;
                try {
                    emotion = Emotion.valueOf(messages.getString("emotion"));
                } catch (Exception e) {
                    emotion = Emotion.NEUTRAL;
                }

                DialogueLine dialogueLine = new DialogueLine(id, message, emotion);

                // Parse options if they exist
                JsonValue optionsJson = messages.get("options");
                if (optionsJson != null && optionsJson.isArray()) {
                    LinkedList<DialogueOption> options = new LinkedList<>();
                    for (JsonValue option : optionsJson) {
                        String optionText = option.getString("text");
                        int nextDialogueId = option.getInt("nextId");
                        options.add(new DialogueOption(optionText, nextDialogueId));
                    }
                    System.out.println(options);
                    dialogueLine.setOptions(options);
                }
                dialogueLines.add(dialogueLine);
            }
        }

        return dialogueLines;
    }



    public static LinkedList<dialogueString> getMessageGroup(int i)
    {
        LinkedList<dialogueString> list = new LinkedList<>();
        for (int j = 0; j < dialogueLines.size(); j++)
        {
            if (dialogueLines.get(j).id == i)
            {
                if (dialogueLines.get(j).getOptions() == null) {
                    list.add(new dialogueString(dialogueLines.get(j).getMessage(), dialogueLines.get(j).getEmotion()));
                } else
                {
                    list.add(new dialogueString(dialogueLines.get(j).getMessage(), dialogueLines.get(j).getEmotion(), dialogueLines.get(j).getOptions()));
                }
            }
        }
        return list;
    }

    /**
     * This class is dedicated to storing data for both Strings and Emotions. This is important for the portraits
     * that are displayed in the overworld
     * TODO: Figure out a way to make creating a new object not painful
     */
    public static class dialogueString
    {
        private String message;
        private Emotion emotion;
        private LinkedList<DialogueOption> options; // New field for options
        public dialogueString(String s, Emotion e)
        {
            message = s;
            emotion = e;
        }

        public dialogueString(String s, Emotion e, LinkedList<DialogueOption> o)
        {
            message = s;
            emotion = e;
            this.options = o;
        }

        public dialogueString(String s)
        {
            message = s;
            emotion = Emotion.NEUTRAL;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Emotion getEmotion() {
            return emotion;
        }

        public void setEmotion(Emotion emotion) {
            this.emotion = emotion;
        }

        public LinkedList<DialogueOption> getOptions() {
            return options;
        }

        public void setOptions(LinkedList<DialogueOption> options) {
            this.options = options;
        }
    }

    /**
     * Handles how messages are displayed to the player. This class is quite long. Requires a Player and NPC object to function
     * @param player Player
     * @param NPC NonPlayableCharacter
     */
    public void Message(Player player, NonPlayableCharacter NPC)
    {
        this.player = player;
        this.NPC = NPC;
        player.getVelocity().x = 0;
        if (player.getPosition().x >= NPC.getPosition().x) {
            player.setFacingRight(false);
            NPC.setFacingRight(true);
        } else
        {
            player.setFacingRight(true);
            NPC.setFacingRight(false);
        }

        if (NPC.isTouchingPlayer()) {
            if (messageIndex >= messageList.size()) {
                // All messages have been displayed
                player.setDisableControls(false);
                NPC.setInteraction(false);
                resetDialogue();
            } else {
                player.setDisableControls(true);

                //Allows the first message to be displayed
                if (!dialogueBox.isVisible()) {
                    if (!messageList.get(messageIndex).getOptions().isEmpty())
                    {
                        player.setMessageChoiceAvailable(true);
                        System.out.println("AAA");
                        dialogueBox.setVisible(true);
                        typingLabel.restart();
                        typingLabel.setText(messageList.get(messageIndex).getMessage());
                        nextButton.setVisible(false);
                        choice1.setText(messageList.get(messageIndex).getOptions().get(0).getText());
                        choice1.setVisible(true);
                        choice2.setText(messageList.get(messageIndex).getOptions().get(1).getText());
                        choice2.setVisible(true);
                        //setMessageList(2000 + messageID);
                        //messageIndex = -1;
                    } else {
                        player.setMessageChoiceAvailable(false);
                        dialogueBox.setVisible(true);
                        typingLabel.restart();
                        typingLabel.setText(messageList.get(messageIndex).getMessage());
                        NPC.setEmotion(messageList.get(messageIndex).getEmotion());
                    }
                }

                stage.act(Gdx.graphics.getDeltaTime());
                stage.draw();

                // Allows the rest of the messages to be displayed
                // Going to be honest, this looks horrible. This whole thing has to be re worked... But it works though!
                if (player.isNextMessage()) {
                    player.setNextMessage(false);
                    messageIndex++;

                    if (messageIndex < messageList.size()) {
                        if (!messageList.get(messageIndex).getOptions().isEmpty())
                        {
                            player.setMessageChoiceAvailable(true);
                            System.out.println("AAA");
                            typingLabel.restart();
                            typingLabel.setText(messageList.get(messageIndex).getMessage());
                            nextButton.setVisible(false);
                            choice1.setText(messageList.get(messageIndex).getOptions().get(0).getText());
                            choice1.setVisible(true);
                            choice2.setText(messageList.get(messageIndex).getOptions().get(1).getText());
                            choice2.setVisible(true);
                            // Update: The code below is commented out as its causing more issues than solving them.
                            // What I would like is when the player presses next on the keyboard (i.e. down) it would
                            // Select the second option automatically. Currently, its busted.
                            // Honestly this might have to be reworked a little bit, as options cannot carry out events
                            // Currently a solution isn't coming to mind "except for hard coding stuff of course

                            //setMessageList(2000 + messageID); // Dialogue.csv denotes 2000 as the second option.
                            //messageIndex = -1;
                        } else {
                            player.setMessageChoiceAvailable(false);
                            choice1.setVisible(false);
                            choice2.setVisible(false);
                            nextButton.setVisible(true);
                            typingLabel.restart();
                            typingLabel.setText(messageList.get(messageIndex).getMessage());
                            NPC.setEmotion(messageList.get(messageIndex).getEmotion());
                        }
                    } else {
                        // All messages have been displayed
                        player.setDisableControls(false);
                        dialogueBox.setVisible(false);
                    }
                }
                UICamera.update();
                spriteBatch.setProjectionMatrix(UICamera.combined);
                spriteBatch.begin();
                NPC.dialogueCharacterDraw(spriteBatch);
                spriteBatch.end();
            }
        } else {
            // Player is not touching the NPC
            resetDialogue();
            player.setInteraction(false);
            player.setDisableControls(false);
        }
    }

    public void resetDialogue() {
        messageIndex = 0;
        if (player != null) {
            player.setNextMessage(false);
            player.setDisplayMessage(false);
        }
        setMessageListID(messageID);
        NPC.setDisplayMessage(false);
        dialogueBox.setVisible(false);
    }

    public void setMessageListID(int id)
    {
        setMessageIndex(0);
        //resetDialogue();
        setMessageID(id);
        setMessageList(getMessageGroup(id));
    }

    //                      //
    // Getters and Setters  //
    //                      //


    public int getMessageIndex() {
        return messageIndex;
    }

    public void setMessageIndex(int messageIndex) {
        this.messageIndex = messageIndex;
    }

    public int getMessageID() {
        return messageID;
    }

    public void setMessageID(int messageID) {
        this.messageID = messageID;
    }

    public LinkedList<dialogueString> getMessageList() {
        return messageList;
    }

    public void setMessageList(LinkedList<dialogueString> messageList) {
        this.messageList = messageList;
    }
}
