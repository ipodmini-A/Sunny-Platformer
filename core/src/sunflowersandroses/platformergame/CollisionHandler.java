package sunflowersandroses.platformergame;

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

