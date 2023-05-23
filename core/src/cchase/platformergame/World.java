package cchase.platformergame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class World
{
    //private TiledMapRenderer mapRenderer; // What does this do?
    private static final float SCALE = 2f;
    private OrthographicCamera camera;
    int objectLayerId;
    Player player;
    float tileSize = 32;
    private TiledMap map;
    private TmxMapLoader loader;
    private OrthogonalTiledMapRenderer mapRenderer;
    private MapLayer collisionLayer;
    private MapObjects objects;
    private boolean debug = true;
    private ShapeRenderer debugRenderer;

    public World(Player player)
    {
        loader = new TmxMapLoader();
        map = loader.load("test1.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1);
        // Get the collision object layer
        collisionLayer = map.getLayers().get("collision");
        objects = collisionLayer.getObjects();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth() / SCALE, Gdx.graphics.getHeight() / SCALE);
        camera.update();
        //mapRenderer.setView(camera);

        player.setSCALE(SCALE);
        debugRenderer = new ShapeRenderer();
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
        boolean isTouchingGround = false;
        boolean isTouchingLeftWall = false;
        boolean isTouchingRightWall = false;
        boolean isTouchingWall = false;
        boolean isTouchingCeiling = false;

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
                    if (playerBottom <= objectTop && playerTop > objectTop)
                    {
                        //TODO
                        // What is happening here, is that Gravity from the Player class is constantly
                        // pushing down the player, while this code is trying to push the player up
                        // out of the world. The result is a jittery mess, while also being the
                        // desirable solution.
                        // Work on a better collision method.
                        isTouchingGround = true;
                        //player.setPosition(player.getPosition().x,player.getPosition().y + 1);
                        player.setVelocity(player.getVelocity().x, 0);
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

    public void cameraUpdate()
    {
        // Set the camera's position to follow the player, considering half of the screen size
        camera.position.x = player.getPosition().x + player.getBounds().width / 2f;
        camera.position.y = player.getPosition().y + player.getBounds().height / 2f;
        player.updateCamera(camera);

        //camera.setToOrtho(false, player.getPosition().x, player.getPosition().y);

        // Update the camera's view
        camera.update();
    }


    public void render()
    {
        cameraUpdate();
        checkCollisions();
        mapRenderer.setView(camera);
        mapRenderer.render();

        // render debug rectangles
        if (debug) renderDebug();
    }

    private void renderDebug ()
    {
        debugRenderer.setProjectionMatrix(camera.combined);
        debugRenderer.begin(ShapeRenderer.ShapeType.Line);

        debugRenderer.setColor(Color.RED);
        debugRenderer.rect(player.getPosition().x, player.getPosition().y, player.getBounds().getWidth(), player.getBounds().getHeight());

        debugRenderer.setColor(Color.YELLOW);
        for (MapObject object : objects)
        {
            if (object instanceof RectangleMapObject)
            {
                RectangleMapObject rectObject = (RectangleMapObject) object;
                debugRenderer.rect(rectObject.getRectangle().x,rectObject.getRectangle().y,
                        rectObject.getRectangle().getWidth(),rectObject.getRectangle().getHeight());
            }
        }
        debugRenderer.end();
    }

    public void dispose()
    {
        mapRenderer.dispose();
        map.dispose();
    }
}
