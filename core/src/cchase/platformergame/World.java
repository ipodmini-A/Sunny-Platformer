package cchase.platformergame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
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
 * The World class contains the details in the world and how the player along with enemies interact with it.
 * This class is intended to serve as a template for levels to be created from. As this class gets more refined, more
 * documentation will be added.
 * Key words to know from the map file
 * tiles: The visual tiles (Not used except for rendering)
 * endgoal: The end goal of the game.
 * collision: The collision of the map.
 */
public class World
{
    //private TiledMapRenderer mapRenderer; // What does this do?
    private static final float GRAVITY = -1000f; // Adjust the gravity value as needed -1000f
    private static final float MAX_FALL = -500f;
    //private static final float JUMP_VELOCITY = 400f; // Adjust the jump velocity as needed
    private static final float SCALE = 2f;
    private static final float FRICTION = 5f;
    private final OrthographicCamera camera;
    protected Player i;
    public Enemy enemy;
    public  NonPlayableCharacter nonPlayableCharacter;
    public Item item;
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
    public Music music;

    public World(Player player)
    {
        // Sound creation
        music = Gdx.audio.newMusic(Gdx.files.internal("sound/Tiny_Sheriff.mp3"));
        music.play();
        music.setVolume(1.0f);

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
        camera.setToOrtho(false, 480, 270);
        camera.update();
        //mapRenderer.setView(camera);

        // Sets the size of the player. Going to be honest, forgot what this does.
        player.setSCALE(SCALE);

        // Enemy creation
        enemy = new Enemy(player.getPosition().x + 300, player.getPosition().y);
        enemy.setSCALE(SCALE);

        //NPC creation
        nonPlayableCharacter = new NonPlayableCharacter(300 , 300);

        //Item creation
        item = new Item(300,300);

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
        this.i = player;
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

        //isTouchingWall = isTouchingWall(player);
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
                            p.getPosition().x = 0;
                        } else
                        {
                            p.getPosition().y = objectTop;
                            //isTouchingGround = true;
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
                                p.getVelocity().y = 0;
                            } else
                            {
                                //p.getPosition().x = objectLeft - p.getWidth();
                                p.getVelocity().x = 0;
                            }

                        } else if (p.isGrounded() && (playerBottom < objectLeft))
                        {
                            /*
                            This mess of code is a little much.
                            Uncommenting the velocity code prevents the player from jumping.
                             */
                            System.out.println("Pushing wall");
                            //p.getVelocity().y = 0; // Stop the player's horizontal movement
                            //p.getPosition().x = oldX - 1; // Reset the player's position to the previous x-coordinate
                        } else
                        {
                            p.getPosition().x = oldX - 1; // Reset the player's position to the previous x-coordinate
                        }
                        //p.setTouchingWall(true);
                        p.setTouchingLeftWall(true);
                        //isTouchingLeftWall = true;
                        //System.out.println("Touching left wall");
                    }

                    // If you're reading this, and you're not the owner of this repository, don't try to make sense of it
                    // because I currently don't know how it works
                    // Check for right wall collision
                    if (playerLeft < objectRight && playerRight > objectRight)
                    {
                        if (p.getVelocity().x < 0 && playerLeft >= objectRight + p.getVelocity().x)
                        {
                            if (p.isGrounded() && playerBottom < objectTop + 5f && playerTop - 50f > objectTop)
                            {
                                p.getVelocity().y = 0;
                            }else
                            {
                                //p.getPosition().x = objectRight;
                                p.getVelocity().x = 0;
                            }
                        } else if (p.isGrounded() && (playerBottom > objectRight))
                        {
                            /*
                            This mess of code is a little much.
                            Uncommenting the velocity code prevents the player from jumping.
                             */
                            System.out.println("Pushing wall");
                            //p.getVelocity().y = 0; // Stop the player's horizontal movement
                            //p.getPosition().x = oldX + 1; // Reset the player's position to the previous x-coordinate
                        } else
                        {
                            p.getPosition().x = oldX + 1; // Reset the player's position to the previous x-coordinate
                        }
                        p.setTouchingRightWall(true);
                        //isTouchingRightWall = true;
                    }

                    // Check for ceiling collision
                    if (playerTop > objectBottom - 5f && playerBottom + 50f < objectBottom)
                    {
                        if (!p.isGrounded())
                        {
                            System.out.println("Touching ceiling");
                            p.getPosition().y = objectBottom - p.getHeight();
                            p.getVelocity().y = 0;
                            p.setTouchingCeiling(true);
                            //isTouchingCeiling = true;
                        }
                    }
                }
            }
        }

        if (p.getVelocity().y > 1)
        {
            p.setGrounded(false);
            //isTouchingGround = false;
        }
        if (p.getVelocity().x > 1)
        {
            //isTouchingLeftWall = false;
            p.setTouchingLeftWall(false);
        }

        if (p.getVelocity().x < -1)
        {
            //isTouchingRightWall = false;
            p.setTouchingRightWall(false);
        }
        if (!p.isTouchingLeftWall() || !p.isTouchingRightWall())
        {
            //isTouchingWall = false;
            p.setTouchingWall(false);
        }
        if (p.getVelocity().y < 1)
        {
            p.setTouchingCeiling(false);
            //isTouchingCeiling = false;
        }

        // Apply gravity
        if (p.isGrounded())
        {
            p.getVelocity().y = 0;
            //player.getPosition().y = oldY;
        } else
        {
            if (p.getVelocity().y >= MAX_FALL)
            {
                p.getVelocity().add(0, GRAVITY * delta);
            }else
            {
                p.getVelocity().y = MAX_FALL;
            }
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


        if (!p.isRightMove() && !p.isLeftMove() &&
                (p.getVelocity().x <= 2 && p.getVelocity().x > 0))
        {
            p.getVelocity().x = 0;
        }

        if (!isTouchingWall(p))
        {
            p.setTouchingWall(false);
            p.setTouchingLeftWall(false);
            p.setTouchingRightWall(false);
        }

        if (p.isTouchingLeftWall() || p.isTouchingRightWall())
        {
            p.setTouchingWall(true);
        }

        if (!isTouchingAnything(p))
        {
            p.setGrounded(false);
            //isTouchingGround = false;
        }
        //System.out.println(p.isGrounded());

        // Update the player's collision status
        //p.setGrounded();
        //System.out.println(isTouchingWall(p));
        //System.out.println(isTouchingAnything(p));
        //p.setTouchingLeftWall(isTouchingLeftWall);
        //p.setTouchingRightWall(isTouchingRightWall);
        //p.setTouchingWall(isTouchingWall);
        //p.setTouchingCeiling(isTouchingCeiling);
        //System.out.println(isTouchingEndGoal());
        //System.out.println(isTouchingAnything());
    }

    public void checkCollisions(float delta, Item item)
    {
        float itemBottom;
        float itemTop;
        float itemLeft;
        float itemRight;

        float objectBottom;
        float objectTop;
        float objectLeft;
        float objectRight;

        float oldX = item.getPosition().x;
        float oldY = item.getPosition().y;

        //isTouchingWall = isTouchingWall(player);
        //System.out.println(isTouchingWall);

        // Iterate through all objects in the collision layer
        for (MapObject object : objects) {
            if (object instanceof RectangleMapObject) {
                RectangleMapObject rectObject = (RectangleMapObject) object;
                Rectangle rect = rectObject.getRectangle();

                itemBottom = item.getPosition().y;
                itemTop = item.getPosition().y + item.getHeight();
                itemLeft = item.getPosition().x ;
                itemRight = item.getPosition().x + item.getWidth();

                objectBottom = rect.y;
                objectTop = rect.y + rect.height;
                objectLeft = rect.x;
                objectRight = rect.x + rect.width;

                if (item.getBounds().overlaps(rect))
                {
                    // Check for ground collision
                    if (itemBottom < objectTop + 5f && itemTop - 50f > objectTop)
                    {
                        if (!(itemLeft < objectRight) || !(itemRight > objectLeft))
                        {
                            item.getPosition().x = 0;
                        } else
                        {
                            item.getPosition().y = objectTop;
                            //isTouchingGround = true;
                            item.setGrounded(true);
                        }
                    }

                    // Check for left wall collision
                    if (itemRight > objectLeft && itemLeft < objectLeft)
                    {
                        if (item.getVelocity().x > 0 && itemRight <= objectLeft + item.getVelocity().x)
                        {
                            if (item.isGrounded() && itemBottom < objectTop + 5f && itemTop - 50f > objectTop)
                            {
                                item.getVelocity().y = 0;
                            } else
                            {
                                //p.getPosition().x = objectLeft - p.getWidth();
                                item.getVelocity().x = 0;
                            }

                        } else if (item.isGrounded() && (itemBottom < objectLeft))
                        {
                            /*
                            This mess of code is a little much.
                            Uncommenting the velocity code prevents the player from jumping.
                             */
                            System.out.println("Pushing wall");
                            //p.getVelocity().y = 0; // Stop the player's horizontal movement
                            //p.getPosition().x = oldX - 1; // Reset the player's position to the previous x-coordinate
                        } else
                        {
                            item.getPosition().x = oldX - 1; // Reset the player's position to the previous x-coordinate
                        }
                        //p.setTouchingWall(true);
                        item.setTouchingLeftWall(true);
                        //isTouchingLeftWall = true;
                        //System.out.println("Touching left wall");
                    }

                    // If you're reading this, and you're not the owner of this repository, don't try to make sense of it
                    // because I currently don't know how it works
                    // Check for right wall collision
                    if (itemLeft < objectRight && itemRight > objectRight)
                    {
                        if (item.getVelocity().x < 0 && itemLeft >= objectRight + item.getVelocity().x)
                        {
                            if (item.isGrounded() && itemBottom < objectTop + 5f && itemTop - 50f > objectTop)
                            {
                                item.getVelocity().y = 0;
                            }else
                            {
                                //p.getPosition().x = objectRight;
                                item.getVelocity().x = 0;
                            }
                        } else if (item.isGrounded() && (itemBottom > objectRight))
                        {
                            /*
                            This mess of code is a little much.
                            Uncommenting the velocity code prevents the player from jumping.
                             */
                            System.out.println("Pushing wall");
                            //p.getVelocity().y = 0; // Stop the player's horizontal movement
                            //p.getPosition().x = oldX + 1; // Reset the player's position to the previous x-coordinate
                        } else
                        {
                            item.getPosition().x = oldX + 1; // Reset the player's position to the previous x-coordinate
                        }
                        item.setTouchingRightWall(true);
                        //isTouchingRightWall = true;
                    }

                    // Check for ceiling collision
                    if (itemTop > objectBottom - 5f && itemBottom + 50f < objectBottom)
                    {
                        if (!item.isGrounded())
                        {
                            System.out.println("Touching ceiling");
                            item.getPosition().y = objectBottom - item.getHeight();
                            item.getVelocity().y = 0;
                            item.setTouchingCeiling(true);
                            //isTouchingCeiling = true;
                        }
                    }
                }
            }
        }

        if (item.getVelocity().y > 1)
        {
            item.setGrounded(false);
            //isTouchingGround = false;
        }
        if (item.getVelocity().x > 1)
        {
            //isTouchingLeftWall = false;
            item.setTouchingLeftWall(false);
        }

        if (item.getVelocity().x < -1)
        {
            //isTouchingRightWall = false;
            item.setTouchingRightWall(false);
        }
        if (!item.isTouchingLeftWall() || !item.isTouchingRightWall())
        {
            //isTouchingWall = false;
            item.setTouchingWall(false);
        }
        if (item.getVelocity().y < 1)
        {
            item.setTouchingCeiling(false);
            //isTouchingCeiling = false;
        }

        // Apply gravity
        if (item.isGrounded())
        {
            item.getVelocity().y = 0;
            //player.getPosition().y = oldY;
        } else
        {
            if (item.getVelocity().y >= MAX_FALL)
            {
                item.getVelocity().add(0, GRAVITY * delta);
            }else
            {
                item.getVelocity().y = MAX_FALL;
            }
        }

        if (item.getVelocity().x > 0)
        {
            item.getVelocity().sub(FRICTION,0);
        }

        if (item.getVelocity().x < 0)
        {
            item.getVelocity().add(FRICTION,0);
        }

        if (item.isGrounded())
        {
            item.getPosition().x += item.getVelocity().x * delta;
        } else
        {
            item.getPosition().add(item.getVelocity().x * delta, item.getVelocity().y * delta);
        }

        if (!isTouchingWall(item))
        {
            item.setTouchingWall(false);
            item.setTouchingLeftWall(false);
            item.setTouchingRightWall(false);
        }

        if (item.isTouchingLeftWall() || item.isTouchingRightWall())
        {
            item.setTouchingWall(true);
        }

        if (!isTouchingAnything(item))
        {
            item.setGrounded(false);
            //isTouchingGround = false;
        }
        //System.out.println(p.isGrounded());

        // Update the player's collision status
        //p.setGrounded();
        //System.out.println(isTouchingWall(p));
        //System.out.println(isTouchingAnything(p));
        //p.setTouchingLeftWall(isTouchingLeftWall);
        //p.setTouchingRightWall(isTouchingRightWall);
        //p.setTouchingWall(isTouchingWall);
        //p.setTouchingCeiling(isTouchingCeiling);
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

        // Check if the player is not touching any object and is in the air
        if (!touching && !player.isGrounded()) {
            return false;
        }

        return touching; // Return the result
    }

    public boolean isTouchingAnything(Item i) {
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
                if (i.getPosition().x + i.getWidth() > left &&
                        i.getPosition().x < right &&
                        i.getPosition().y + i.getHeight() > bottom &&
                        i.getPosition().y < top) {
                    touching = true; // Player is touching the object
                    break; // Exit the loop, no need to check further
                }
            }
        }

        // Check if the player is not touching any object and is in the air
        if (!touching && !i.isGrounded()) {
            return false;
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
    public boolean isTouchingWall(Item i) {
        float tolerance = 1f;

        for (MapObject object : objects) {
            if (object instanceof RectangleMapObject) {
                RectangleMapObject rectObject = (RectangleMapObject) object;
                Rectangle rect = rectObject.getRectangle();

                float left = rect.x - tolerance;
                float right = rect.x + rect.width + tolerance;
                float bottom = rect.y - tolerance;
                float top = rect.y + rect.height + tolerance;

                boolean touchingLeftWall = i.getPosition().x + i.getWidth() >= left && i.getPosition().x <= left;
                boolean touchingRightWall = i.getPosition().x <= right && i.getPosition().x + i.getWidth() >= right;
                boolean aboveTop = i.getPosition().y + i.getHeight() >= top;

                if ((touchingLeftWall || touchingRightWall) && i.getPosition().y > bottom && i.getPosition().y < top && !aboveTop) {
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

                if (i.getPosition().x < right && i.getPosition().x + i.getWidth() > left &&
                        i.getPosition().y < top && i.getPosition().y + i.getHeight() > bottom) {
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
     *       each enemy to test to see if collision has occurred. Is this idea optimal? No. Will I do it? Probably.
     */
    public boolean isCollidingWithEnemy()
    {
        try
        {
            if (i.getBounds().overlaps(enemy.getBounds()))
            {
                return true;
            }
            return false;
        } catch (Exception e)
        {
            // Try catch is here to prevent null exceptions when a enemy is missing.
            return false;
        }
    }

    public boolean isCollidingWithNPC()
    {
        if (i.getBounds().overlaps(nonPlayableCharacter.getBounds()))
        {
            //System.out.println("Colliding with NPC");
            nonPlayableCharacter.setTouchingPlayer(true);
            i.setNpcInteraction(true);
            return true;
        }
        //System.out.println("Not colliding with NPC");
        nonPlayableCharacter.setTouchingPlayer(false);
        i.setNpcInteraction(false);
        return false;
    }

    public void isItemTouchingFloor()
    {

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
        camera.position.x = i.getPosition().x + i.getWidth() / SCALE;
        camera.position.y = i.getPosition().y + i.getHeight() / SCALE;

        mapRenderer.setView(camera);
        mapRenderer.render();

        // NPC render
        nonPlayableCharacter.updateCamera(camera);
        nonPlayableCharacter.render(spriteBatch,delta);
        checkCollisions(delta,nonPlayableCharacter);
        //System.out.println(player.nextMessage);
        //System.out.println(isCollidingWithNPC());
        if (isCollidingWithNPC() && i.isDisplayMessage())
        {
            //System.out.println("NPC interaction");
            nonPlayableCharacter.setDisplayMessage(true);
        }
        if (nonPlayableCharacter.isDisplayMessage())
        {
            nonPlayableCharacter.Message(i);
        }

        try
        {
            // Enemy render
            if (enemy.getHealth() > 0)
            {
                enemy.updateCamera(camera);
                enemy.render(spriteBatch, delta);
                checkCollisions(delta, enemy);
            } else
            {
                // Enemy is removed from the world.
                enemy = null;
            }
        } catch (Exception e)
        {
            // Handle error
        }

        // Player render
        checkCollisions(delta, i);
        i.updateCamera(camera);
        i.render(spriteBatch,delta);

        // Item render
        checkCollisions(delta, item);
        item.render(spriteBatch,camera,delta);

        // render debug rectangles
        if (debug) renderDebug();
    }

    /**
     * Map debug renderer. Draws lines around key objects such as the player or the world objects.
     */
    private void renderDebug ()
    {

        debugBatch.begin();
        debugFont.draw(debugBatch, "Velocity: " + i.getVelocity(), Gdx.graphics.getWidth() * .05f, Gdx.graphics.getHeight() * .95f);
        debugFont.draw(debugBatch, "Position: " + i.getPosition(), Gdx.graphics.getWidth() * .05f, Gdx.graphics.getHeight() * .85f);
        debugBatch.end();

        debugRenderer.setProjectionMatrix(camera.combined);
        debugRenderer.begin(ShapeRenderer.ShapeType.Line);

        //Player Debug
        debugRenderer.setColor(Color.RED);
        debugRenderer.rect(i.getPosition().x, i.getPosition().y, i.getWidth(), i.getHeight());
        //System.out.println("debugRender X:" + player.getPosition().x + " debugRender Y:" + player.getPosition().y);

        //Enemy Debug
        try
        {
            debugRenderer.setColor(Color.CYAN);
            debugRenderer.rect(enemy.getPosition().x, enemy.getPosition().y, enemy.getWidth(), enemy.getHeight());
        } catch (Exception e)
        {
            // Handle enemy removal
            // Try catch is here to handle when the enemy is missing
        }

        //NPC Debug
        debugRenderer.setColor(Color.PURPLE);
        debugRenderer.rect(nonPlayableCharacter.getPosition().x, nonPlayableCharacter.getPosition().y, nonPlayableCharacter.getWidth(), nonPlayableCharacter.getHeight());

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
        i.dispose();
        enemy.dispose();
        spriteBatch.dispose();
        music.dispose();
    }
}
