package sunflowersandroses.platformergame.items;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import sunflowersandroses.platformergame.player.Player;

/**
 * Basic health pickup for the player
 * ID: 2
 */
public class Health extends Item {
    public Health(float x, float y, int id) {
        super(x, y, id);
        sprite = new Sprite(texture);
        textureAtlas = new TextureAtlas("sprites/genericItems_spritesheet_colored.txt");
        sprite = new Sprite(textureAtlas.findRegion("genericItem_color_102.png"));
    }

    @Override
    public void collectedAction(Player p) {
        super.collectedAction(p);
        p.setHealth(p.getHealth() + 10);
    }
}
