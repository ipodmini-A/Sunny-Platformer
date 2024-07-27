package sunflowersandroses.platformergame.items;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import sunflowersandroses.platformergame.player.Player;
import sunflowersandroses.platformergame.screens.GameScreen;
import sunflowersandroses.platformergame.screens.SlotsScreen;

import java.util.LinkedList;
import java.util.Random;

/**
 * Slot Machine item. Utilizes the SlotsGame.java class to function. See that class for more information.
 * ID: 1
 */
public class SlotMachine extends Item {
    Random random;

    public SlotMachine(float x, float y, int id) {
        super(x, y, false);
        this.id = id;
        position = new Vector2(x, y);
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
     * <p>
     * TODO: Rework how the player interacts with both NPS and items.
     *
     * @param p
     */
    public void interact(Player p) {
        if (p.isItemInteraction()) {
            p.setItemInteraction(false);
            p.setDisplayMessage(false);
            GameScreen.game.setScreen(new SlotsScreen(GameScreen.game, p));
        }
    }
}
