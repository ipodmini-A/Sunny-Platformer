package cchase.platformergame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;

public class World {

    private TiledMap tiledMap;
    private TiledMapRenderer tiledMapRenderer;
    private OrthographicCamera camera;

    private Player player;

    public World(Player p) {
        tiledMap = new TmxMapLoader().load("test1.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.update();

        // Create the player at a specific position on top of the world
        player = p;
    }

    public void update()
    {
        player.update();
        checkCollision();
    }

    private void checkCollision() {
        MapLayer collisionLayer = tiledMap.getLayers().get("collision");
        for (MapObject object : collisionLayer.getObjects())
        {
            if (object instanceof RectangleMapObject)
            {
                Rectangle rectangle = ((RectangleMapObject) object).getRectangle();
                if (player.getBounds().overlaps(rectangle))
                {
                    // Adjust player position to be on top of the collision object
                    player.getBounds().y = rectangle.y + rectangle.height;
                }
            }
        }
    }

    public void render()
    {
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();

        SpriteBatch batch = new SpriteBatch();
        batch.begin();
        player.render(batch);
        batch.end();
    }

    public void dispose() {
        tiledMap.dispose();
        player.dispose();
    }
}
