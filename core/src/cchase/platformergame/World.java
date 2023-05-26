package cchase.platformergame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class World
{
    //private TiledMapRenderer mapRenderer; // What does this do?
    private static final float GRAVITY = -1000f; // Adjust the gravity value as needed -1000f
    private static final float JUMP_VELOCITY = 400f; // Adjust the jump velocity as needed
    private static final float SCALE = 2f;
    private static float FRICTION = 3f;
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

    private Pool<Rectangle> rectPool = new Pool<Rectangle>() {
        @Override
        protected Rectangle newObject () {
            return new Rectangle();
        }
    };
    private Array<Rectangle> tiles = new Array<Rectangle>();
    private ShapeRenderer debugRenderer;
    private BitmapFont debugFont;
    private SpriteBatch debugBatch;
    boolean isTouchingGround = false;
    boolean isTouchingLeftWall = false;
    boolean isTouchingRightWall = false;
    boolean isTouchingWall = false;
    boolean isTouchingCeiling = false;
    boolean collisionOccurred = false;

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
        debugFont = new BitmapFont();
        debugBatch = new SpriteBatch();
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
     * TODO: Refactor
     */
    public void checkCollisions(float delta)
    {
        float playerBottom;
        float playerTop;
        float playerLeft;
        float playerRight;

        float objectBottom;
        float objectTop;
        float objectLeft;
        float objectRight;

        float oldX = player.getPosition().x;
        float oldY = player.getPosition().y;

        // Iterate through all objects in the collision layer
        for (MapObject object : objects)
        {
            if (object instanceof RectangleMapObject)
            {

                RectangleMapObject rectObject = (RectangleMapObject) object;
                // Check if the player's bounding box overlaps with the object's rectangle
                Rectangle rect = rectObject.getRectangle();

                // TODO: Fix collision issue. Maybe increase the objects hit box by a small amount.
                // Check the relative position of the object with respect to the player
                playerBottom = player.getPosition().y;
                playerTop = player.getPosition().y + player.getHeight();
                playerLeft = player.getPosition().x;
                playerRight = player.getPosition().x + player.getWidth();

                objectBottom = rect.y;
                objectTop = rect.y + rect.height;
                objectLeft = rect.x;
                objectRight = rect.x + rect.width;

                if (player.getBounds().overlaps(rect))
                {
                    // Check for ground collision
                    if (playerBottom <= objectTop + 5f && playerTop > objectTop + 16f) // +16f
                    {
                        //TODO
                        // When colliding with a corner, the player will jut to the top of the corner and get stuck
                        // Work out a solution to end this.

                        if (!(playerLeft < objectRight) || !(playerRight > objectLeft))
                        {
                            System.out.println("test");
                        } else
                        {
                            player.getPosition().y = objectTop;
                        }
                        isTouchingGround = true;
                    }

                    // Check for left wall collision
                    if (playerRight > objectLeft && playerLeft < objectLeft)
                    {
                        if (isTouchingGround && (playerBottom > objectLeft))
                        {
                            player.getVelocity().x = 0; // Stop the player's horizontal movement
                            player.getPosition().x = oldX - 1; // Reset the player's position to the previous x-coordinate
                        } else
                        {
                            player.getPosition().x = oldX - 1; // Reset the player's position to the previous x-coordinate
                        }
                        isTouchingLeftWall = true;
                        isTouchingWall = true;
                    }

                    // Check for right wall collision
                    if (playerLeft < objectRight && playerRight > objectRight)
                    {
                        if (isTouchingGround && (playerBottom > objectRight))
                        {
                            player.getVelocity().x = 0; // Stop the player's horizontal movement
                            player.getPosition().x = oldX + 1; // Reset the player's position to the previous x-coordinate
                        } else
                        {
                            player.getPosition().x = oldX + 1; // Reset the player's position to the previous x-coordinate

                        }
                        isTouchingRightWall = true;
                        isTouchingWall = true;
                    }

                    // Check for ceiling collision
                    if (playerTop > objectBottom - 5f && playerBottom < objectBottom - 16f)
                    {
                        player.getPosition().y = objectBottom - player.getHeight();
                        player.getVelocity().y = 0;
                        //player.getPosition().y = objectTop;
                        isTouchingCeiling = true;
                    }
                }
            }
        }

        if (player.getVelocity().y > 1)
        {
            isTouchingGround = false;
        }
        if (player.getVelocity().x > 1)
        {
            isTouchingLeftWall = false;
        }
        if (player.getVelocity().x < 1)
        {
            isTouchingRightWall = false;
        }
        if (!isTouchingLeftWall || !isTouchingRightWall)
        {
            isTouchingWall = false;
        }
        if (player.getVelocity().y < 1)
        {
            isTouchingCeiling = false;
        }

        // Apply gravity
        if (isTouchingGround)
        {
            player.getVelocity().y = 0;
            //player.getPosition().y = oldY;
        } else
        {
            player.getVelocity().add(0, GRAVITY * delta);
        }

        if (player.getVelocity().x > 0)
        {
            player.getVelocity().sub(FRICTION,0);
        }

        if (player.getVelocity().x < 0)
        {
            player.getVelocity().add(FRICTION,0);
        }

        if (isTouchingGround)
        {
            player.getPosition().x += player.getVelocity().x * delta;
        } else
        {
            player.getPosition().add(player.getVelocity().x * delta, player.getVelocity().y * delta);
        }

        if ((!player.getPlatformerInput().isRightPressed() && !player.getPlatformerInput().isLeftPressed()) &&
                (player.getVelocity().x <= 2 && player.getVelocity().x > 0))
        {
            player.getVelocity().x = 0;
        }

        if (!isTouchingAnything())
        {
            isTouchingGround = false;
        }

        // Update the player's collision status
        player.setGrounded(isTouchingGround);
        player.setTouchingLeftWall(isTouchingLeftWall);
        player.setTouchingRightWall(isTouchingRightWall);
        player.setTouchingWall(isTouchingWall);
        player.setTouchingCeiling(isTouchingCeiling);
        //System.out.println(isTouchingAnything());
    }

    public boolean isTouchingAnything() {
        float tolerance = 1f;

        for (MapObject object : objects) {
            if (object instanceof RectangleMapObject)
            {
                RectangleMapObject rectObject = (RectangleMapObject) object;
                Rectangle rect = rectObject.getRectangle();

                float left = rect.x - tolerance;
                float right = rect.x + rect.width + tolerance;
                float bottom = rect.y - tolerance;
                float top = rect.y + rect.height + tolerance;

                if (player.getPosition().x < right && player.getPosition().x + player.getWidth() > left &&
                        player.getPosition().y < top && player.getPosition().y + player.getHeight() > bottom) {
                    return true; // Player is touching the object
                }
            }
        }

        return false; // Player is not touching anything
    }

    public void render(float delta)
    {
        // Update the camera's view
        camera.update();

        // Set the camera's position to follow the player, considering half of the screen size
        camera.position.x = player.getPosition().x + player.getWidth() / SCALE;
        camera.position.y = player.getPosition().y + player.getHeight() / SCALE;
        player.updateCamera(camera);

        //newCheckCollisions(delta);
        checkCollisions(delta);
        mapRenderer.setView(camera);
        mapRenderer.render();

        // render debug rectangles
        if (debug) renderDebug();
    }

    private void renderDebug ()
    {

        debugBatch.begin();
        debugFont.draw(debugBatch, "Velocity: " + player.getVelocity(), Gdx.graphics.getWidth() * .05f, Gdx.graphics.getHeight() * .95f);
        debugFont.draw(debugBatch, "Position: " + player.getPosition(), Gdx.graphics.getWidth() * .05f, Gdx.graphics.getHeight() * .85f);
        debugBatch.end();

        debugRenderer.setProjectionMatrix(camera.combined);
        debugRenderer.begin(ShapeRenderer.ShapeType.Line);

        debugRenderer.setColor(Color.RED);
        debugRenderer.rect(player.getPosition().x, player.getPosition().y, player.getWidth(), player.getHeight());
        //System.out.println("debugRender X:" + player.getPosition().x + " debugRender Y:" + player.getPosition().y);

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
