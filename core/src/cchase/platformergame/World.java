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
    public void checkCollisions() {
        // Get the collision object layer
        MapLayer collisionLayer = map.getLayers().get("collision");
        MapObjects objects = collisionLayer.getObjects();

        boolean isTouchingGround = false;
        boolean isTouchingLeftWall = false;
        boolean isTouchingRightWall = false;
        boolean isTouchingWall = false;
        boolean isTouchingCeiling = false;

        // Iterate through all objects in the collision layer
        for (MapObject object : objects) {
            if (object instanceof RectangleMapObject) {
                RectangleMapObject rectObject = (RectangleMapObject) object;

                // Check if the player's bounding box overlaps with the object's rectangle
                Rectangle rect = rectObject.getRectangle();

                if (player.getBounds().overlaps(rect))
                {
                    // TODO: Fix collision issue. Maybe increase the objects hit box by a small amount.
                    // Check the relative position of the object with respect to the player
                    float playerBottom = player.getPosition().y;
                    float playerTop = player.getPosition().y + player.getBounds().getHeight();
                    float playerLeft = player.getPosition().x;
                    float playerRight = player.getPosition().x + player.getBounds().getWidth();

                    float objectBottom = rect.y;
                    float objectTop = rect.y + rect.height;
                    float objectLeft = rect.x;
                    float objectRight = rect.x + rect.width;

                    // Check for ground collision
                    if (playerBottom < objectTop && playerTop > objectTop)
                    {

                        isTouchingGround = true;
                        player.setVelocity(player.getVelocity().x, 0);
                        player.setPosition(player.getPosition().x, player.getPosition().y + 5);
                    }

                    // Check for left wall collision
                    if (playerRight > objectLeft && playerLeft < objectLeft)
                    {
                        isTouchingLeftWall = true;
                        isTouchingWall = true;
                    }

                    // Check for right wall collision
                    if (playerLeft < objectRight && playerRight > objectRight)
                    {
                        isTouchingRightWall = true;
                        isTouchingWall = true;
                    }

                    // Check for ceiling collision
                    if (playerTop > objectBottom && playerBottom < objectBottom)
                    {
                        isTouchingCeiling = true;
                    }
                }
            }
        }

        // Update the player's collision status
        player.setGrounded(isTouchingGround);
        player.setTouchingLeftWall(isTouchingLeftWall);
        player.setTouchingRightWall(isTouchingRightWall);
        player.setTouchingWall(isTouchingWall);
        player.setTouchingCeiling(isTouchingCeiling);
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
