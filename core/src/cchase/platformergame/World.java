package cchase.platformergame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class World
{
    private TiledMapRenderer tiledMapRenderer; // What does this do?
    private OrthographicCamera camera;
    int objectLayerId;
    Player player;
    float tileSize = 32;
    private MapLayer collisionLayer;
    private TiledMap map;
    private TmxMapLoader loader;
    private OrthogonalTiledMapRenderer mapRenderer;

    public World(Player player)
    {
        loader = new TmxMapLoader();
        map = loader.load("test1.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map);
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.update();
    }

    public void WorldUpdate(Player player)
    {
        this.player = player;
    }


    /**
     * checkCollisions()
     *
     * A basic method that is used to test for collisions
     * Using MapLayer, the collision layer is gathered. It then iterates through the objects to see if each object had some
     * form of interaction.
     * Currently in progress
     * TODO: See if the iteration can be changed.
     * TODO: Calculate a way to allow for the method to jump.
     */
    public void checkCollisions()
    {
        // Get the collision object layer
        MapLayer collisionLayer = map.getLayers().get("collision");
        MapObjects objects = collisionLayer.getObjects();

        // Iterate through all objects in the collision layer
        for (MapObject object : objects)
        {
            if (object instanceof RectangleMapObject)
            {
                RectangleMapObject rectObject = (RectangleMapObject) object;

                // Check if the player's bounding box overlaps with the object's rectangle
                Rectangle rect = rectObject.getRectangle();
                if (player.getBounds().overlaps(rect))
                {
                    player.setGrounded(true);
                    //System.out.println(player.isGrounded());
                    //player.setVelocity(player.getVelocity().x, (50)); // Stop the player's movement
                    break;
                }
            }
        }
    }

    public void render()
    {
        checkCollisions();
        mapRenderer.setView(camera);
        mapRenderer.render();
    }

    public void dispose()
    {
        mapRenderer.dispose();
        map.dispose();
    }
}
