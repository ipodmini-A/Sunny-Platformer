package cchase.platformergame;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.utils.Array;

/**
 * Currently not in use.
 * This class will be used to handle collision. Currently Collision is being handled by the World class
 */
public class CollisionHandler
{
    private Player player;
    private World world;

    public CollisionHandler(Player player, World world) {
        this.player = player;
        this.world = world;
    }

}

