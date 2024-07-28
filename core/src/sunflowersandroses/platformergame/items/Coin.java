package sunflowersandroses.platformergame.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import sunflowersandroses.platformergame.player.Player;

import java.util.Random;

/**
 * Basic coin for the player to pick up
 * ID: 0
 */
public class Coin extends Item {
    private int coinMultiplier = 0;
    private Sound sound;

    /**
     * Creates a new coin. A random roll decides what type of coin will be spawned.
     *
     * @param x  X Position
     * @param y  Y Position
     * @param id item ID
     */
    public Coin(float x, float y, int id) {
        super(x, y, id);
        //Initialize sound
        sound = Gdx.audio.newSound(Gdx.files.internal("sounds/coinCollect.wav"));
        Random random = new Random();
        int coinRoll = random.nextInt(30) + 1;
        if (coinRoll <= 20) {
            texture = new Texture("sprites/Items/copperCoin.png"); // Kind of a placeholder
            sprite = new Sprite(texture);
            coinMultiplier = 1;
        } else if (coinRoll <= 27) {
            texture = new Texture("sprites/Items/silverCoin.png"); // Kind of a placeholder
            sprite = new Sprite(texture);
            coinMultiplier = 5;
        } else {
            texture = new Texture("sprites/Items/goldCoin.png"); // Kind of a placeholder
            sprite = new Sprite(texture);
            coinMultiplier = 10;
        }
        allowedToBeCollected = true;
    }

    @Override
    public void collectedAction(Player p) {
        sound.play(1.0f);
        p.setMoney(p.getMoney() + coinMultiplier);
    }
}
