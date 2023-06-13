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
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;

/**
 * This class is a mess, im not even joking
 * TODO: Clean up
 *
 * The World class contains the details in the world and how the player along with enemies interact with it.
 * This class is intended to serve as a template for levels to be created from. As this class gets more refined, more
 * documentation will be added.
 */
public class World
{
    //private TiledMapRenderer mapRenderer; // What does this do?
    private static final float GRAVITY = -1000f; // Adjust the gravity value as needed -1000f
    //private static final float JUMP_VELOCITY = 400f; // Adjust the jump velocity as needed
    private static final float SCALE = 2f;
    private static final float FRICTION = 3f;
    private final OrthographicCamera camera;
    Player player;
    Enemy enemy;
    private final TiledMap map;
    private TmxMapLoader loader;
    private OrthogonalTiledMapRenderer mapRenderer;
    private MapLayer collisionLayer;
    private MapLayer endGoalLayer;
    private MapObjects objects;
    private MapObjects endGameObject;
    private boolean debug = true;
    private SpriteBatch spriteBatch;
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
        // Map creation
        loader = new TmxMapLoader();
        map = loader.load("test1.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1);

        //sprite batch creation
        spriteBatch = new SpriteBatch();

        // Layers from the map that was spawned above.
        collisionLayer = map.getLayers().get("collision");
        endGoalLayer = map.getLayers().get("endgoal");
        objects = collisionLayer.getObjects();
        endGameObject = endGoalLayer.getObjects();

        // Camera creation
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth() / SCALE, Gdx.graphics.getHeight() / SCALE);
        camera.update();
        //mapRenderer.setView(camera);

        // Sets the size of the player. Going to be honest, forgot what this does.
        // Enemy creation
        player.setSCALE(SCALE);
        enemy = new Enemy(player.getPosition().x + 300, player.getPosition().y);
        enemy.setSCALE(SCALE);

        // Debug
        debugRenderer = new ShapeRenderer();
        debugFont = new BitmapFont();
        debugBatch = new SpriteBatch();
    }

    /**
     * WorldUpdate updates the World class with the player class from GameScreen. Removing this shouldn't cause a null
     * exception but it does :/
     * @param player
     */
    public void WorldUpdate(Player player)
    {
        this.player = player;
    }


    /**
     * checkCollisions()
     *
     * A method that is used to test for collisions
     * Using MapLayer, the collision layer is gathered. It then iterates through the objects to see if each object had some
     * form of interaction.
     * Currently in progress
     * TODO: See if the iteration can be changed.
     * TODO: Calculate a way to allow for the method to jump.
     * TODO: Refactor
     *
     * @param delta float
     * @param p A player object. This includes players and objects that are extended from it such as enemies.
     */
    public void checkCollisions(float delta, Player p)
    {
        float playerBottom;
        float playerTop;
        float playerLeft;
        float playerRight;

        float objectBottom;
        float objectTop;
        float objectLeft;
        float objectRight;

        float oldX = p.getPosition().x;
        float oldY = p.getPosition().y;


        isTouchingWall = isTouchingWall(player);
        //System.out.println(isTouchingWall);

        // Iterate through all objects in the collision layer
        for (MapObject object : objects) {
            if (object instanceof RectangleMapObject) {
                RectangleMapObject rectObject = (RectangleMapObject) object;
                Rectangle rect = rectObject.getRectangle();

                playerBottom = p.getPosition().y;
                playerTop = p.getPosition().y + p.getHeight();
                playerLeft = p.getPosition().x ;
                playerRight = p.getPosition().x + p.getWidth();

                objectBottom = rect.y;
                objectTop = rect.y + rect.height;
                objectLeft = rect.x;
                objectRight = rect.x + rect.width;

                if (p.getBounds().overlaps(rect))
                {
                    // Check for ground collision
                    if (playerBottom < objectTop + 5f && playerTop - 50f > objectTop)
                    {
                        if (!(playerLeft < objectRight) || !(playerRight > objectLeft))
                        {
                            System.out.println("test");
                            p.getPosition().x = 0;
                        } else
                        {
                            p.getPosition().y = objectTop;
                            isTouchingGround = true;
                            p.setGrounded(true);
                        }
                    }

                    // Check for left wall collision
                    if (playerRight > objectLeft && playerLeft < objectLeft)
                    {
                        if (p.getVelocity().x > 0 && playerRight <= objectLeft + p.getVelocity().x)
                        {
                            if (p.isGrounded() && playerBottom < objectTop + 5f && playerTop - 50f > objectTop)
                            {
                                System.out.println("Inside first if");
                                p.getVelocity().y = 0;
                            } else
                            {
                                //p.getPosition().x = objectLeft - p.getWidth();
                                p.getVelocity().x = 0;
                            //System.out.println("Inside first if");
                            }

                        } else if (p.isGrounded() && (playerBottom < objectLeft))
                        {
                            p.getVelocity().y = 0; // Stop the player's horizontal movement
                            //p.getPosition().x = oldX - 1; // Reset the player's position to the previous x-coordinate
                        } else
                        {
                            p.getPosition().x = oldX - 1; // Reset the player's position to the previous x-coordinate
                        }

                        p.setTouchingWall(true);
                        p.setTouchingRightWall(true);
                        isTouchingLeftWall = true;
                        System.out.println("Touching left wall");
                    }

                    // Check for right wall collision
                    if (playerLeft < objectRight && playerRight > objectRight)
                    {
                        if (p.getVelocity().x < 0 && playerLeft >= objectRight + p.getVelocity().x)
                        {
                            if (p.isGrounded() && playerBottom < objectTop + 5f && playerTop - 50f > objectTop)
                            {
                                System.out.println("Inside first if");
                                p.getVelocity().y = 0;
                            }else
                            {
                                //p.getPosition().x = objectRight;
                                p.getVelocity().x = 0;
                            }
                        } else if (p.isGrounded() && (playerBottom > objectRight))
                        {
                            p.getVelocity().y = 0; // Stop the player's horizontal movement
                            //p.getPosition().x = oldX + 1; // Reset the player's position to the previous x-coordinate
                        } else
                        {
                            p.getPosition().x = oldX + 1; // Reset the player's position to the previous x-coordinate
                        }
                        p.setTouchingWall(true);
                        p.setTouchingRightWall(true);
                        isTouchingRightWall = true;
                        //System.out.println("Touching right wall");
                    }

                    // Check for ceiling collision
                    if (playerTop > objectBottom - 5f && playerBottom + 50f < objectBottom)
                    {
                        if (!p.isGrounded())
                        {
                            System.out.println("Touching ceiling");
                            p.getPosition().y = objectBottom - p.getHeight();
                            p.getVelocity().y = 0;
                            isTouchingCeiling = true;
                        }
                    }
                }
            }
        }



        if (p.getVelocity().y > 1)
        {
            p.setGrounded(false);
            isTouchingGround = false;
        }
        if (p.getVelocity().x > 1)
        {
            isTouchingLeftWall = false;
            p.setTouchingLeftWall(false);
        }
        if (p.getVelocity().x < 1)
        {
            isTouchingRightWall = false;
            p.setTouchingRightWall(false);
        }
        if (!isTouchingLeftWall || !isTouchingRightWall)
        {
            isTouchingWall = false;
            p.setTouchingWall(false);
        }
        if (p.getVelocity().y < 1)
        {
            isTouchingCeiling = false;
        }

        // Apply gravity
        if (p.isGrounded())
        {
            p.getVelocity().y = 0;
            //player.getPosition().y = oldY;
        } else
        {
            p.getVelocity().add(0, GRAVITY * delta);
        }

        if (p.getVelocity().x > 0)
        {
            p.getVelocity().sub(FRICTION,0);
        }

        if (p.getVelocity().x < 0)
        {
            p.getVelocity().add(FRICTION,0);
        }

        if (p.isGrounded())
        {
            p.getPosition().x += p.getVelocity().x * delta;
        } else
        {
            p.getPosition().add(p.getVelocity().x * delta, p.getVelocity().y * delta);
        }

        if ((!p.getPlatformerInput().isRightPressed() && !p.getPlatformerInput().isLeftPressed()) &&
                (p.getVelocity().x <= 2 && p.getVelocity().x > 0))
        {
            p.getVelocity().x = 0;
        }

        if (!isTouchingAnything(p))
        {
            p.setGrounded(false);
            isTouchingGround = false;
        }
        //System.out.println(isTouchingAnything(p));

        // Update the player's collision status
        //p.setGrounded(p.isGrounded());
        //System.out.println(isTouchingGround);
        p.setTouchingLeftWall(isTouchingLeftWall);
        p.setTouchingRightWall(isTouchingRightWall);
        p.setTouchingWall(isTouchingWall);
        p.setTouchingCeiling(isTouchingCeiling);
        //System.out.println(isTouchingEndGoal());
        //System.out.println(isTouchingAnything());
    }


    /**
     * isTouchingAnything returns true if the player is currently colliding with a level object, false if otherwise.
     * Note that this only checks for level floors, ceilings and walls. This does not check for objects such as the
     * end level
     * @param player
     * @return
     */
    public boolean isTouchingAnything(Player player) {
        float tolerance = 1f;

        boolean touching = false;

        for (MapObject object : objects) {
            if (object instanceof RectangleMapObject) {
                RectangleMapObject rectObject = (RectangleMapObject) object;
                Rectangle rect = rectObject.getRectangle();

                float left = rect.x - tolerance;
                float right = rect.x + rect.width + tolerance;
                float bottom = rect.y - tolerance;
                float top = rect.y + rect.height + tolerance;

                // Check if any part of the player's bounding box overlaps with the object's rectangle
                if (player.getPosition().x + player.getWidth() > left &&
                        player.getPosition().x < right &&
                        player.getPosition().y + player.getHeight() > bottom &&
                        player.getPosition().y < top) {
                    touching = true; // Player is touching the object
                    break; // Exit the loop, no need to check further
                }
            }
        }

        return touching; // Return the result
    }



    /**
     * isTouchingWall returns true if the player is touching a wall, returns false if otherwise.
     * @param player
     * @return True if the player is touching wall, false if otherwise.
     */
    public boolean isTouchingWall(Player player) {
        float tolerance = 1f;

        for (MapObject object : objects) {
            if (object instanceof RectangleMapObject) {
                RectangleMapObject rectObject = (RectangleMapObject) object;
                Rectangle rect = rectObject.getRectangle();

                float left = rect.x - tolerance;
                float right = rect.x + rect.width + tolerance;
                float bottom = rect.y - tolerance;
                float top = rect.y + rect.height + tolerance;

                boolean touchingLeftWall = player.getPosition().x + player.getWidth() >= left && player.getPosition().x <= left;
                boolean touchingRightWall = player.getPosition().x <= right && player.getPosition().x + player.getWidth() >= right;
                boolean aboveTop = player.getPosition().y + player.getHeight() >= top;

                if ((touchingLeftWall || touchingRightWall) && player.getPosition().y > bottom && player.getPosition().y < top && !aboveTop) {
                    return true;
                }
            }
        }

        return false;
    }


    /**
     * isTouchingEndGoal returns true if the player has collided with the end goal object. Returns false if otherwise.
     * @return True if player has collided with the end game object, false if otherwise
     */
    public boolean isTouchingEndGoal() {
        float tolerance = 1f;
        for (MapObject object : endGameObject) {
            if (object instanceof RectangleMapObject) {
                RectangleMapObject rectObject = (RectangleMapObject) object;
                Rectangle rect = rectObject.getRectangle();

                float left = rect.x - tolerance;
                float right = rect.x + rect.width + tolerance;
                float bottom = rect.y - tolerance;
                float top = rect.y + rect.height + tolerance;

                if (player.getPosition().x < right && player.getPosition().x + player.getWidth() > left &&
                        player.getPosition().y < top && player.getPosition().y + player.getHeight() > bottom) {
                    return true; // Player is touching the end goal
                }
            }
        }
        return false; // Player is not touching the end goal
    }

    /**
     * Returns true if the player is colliding with an enemy, false if otherwise
     * @return True if the player is colliding with an enemy, false if otherwise
     *
     * TODO: If there are multiple enemies within the level, it's most likely this method will have to iterate through
     *       each enemy to test to see if collision has occured. Is this idea optimal? No. Will I do it? Probably.
     */
    public boolean isCollidingWithEnemy()
    {
        if (player.getBounds().overlaps(enemy.getBounds()))
        {
            //System.out.println("Colliding with enemy");
            return true;
        }
        //System.out.println("Not colliding with enemy");
        return false;
    }


    /**
     * Map renderer
     * @param delta
     */
    public void render(float delta)
    {
        // Update the camera's view
        camera.update();

        // Set the camera's position to follow the player, considering half of the screen size
        camera.position.x = player.getPosition().x + player.getWidth() / SCALE;
        camera.position.y = player.getPosition().y + player.getHeight() / SCALE;
        player.updateCamera(camera);

        //newCheckCollisions(delta);
        checkCollisions(delta,player);
        checkCollisions(delta,enemy);
        mapRenderer.setView(camera);
        mapRenderer.render();

        player.render(spriteBatch,delta);
        if (enemy.getHealth() > 0)
        {
            enemy.updateCamera(camera);
            enemy.render(spriteBatch,delta);
        }

        // render debug rectangles
        if (debug) renderDebug();
    }

    /**
     * Map debug renderer. Draws lines around key objects such as the player or the world objects.
     */
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

        debugRenderer.setColor(Color.CYAN);
        debugRenderer.rect(enemy.getPosition().x, enemy.getPosition().y, enemy.getWidth(), enemy.getHeight());

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
