package cchase.platformergame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
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

    public World() {
        // Load the TiledMap from the Tiled Map Editor file
        tiledMap = new TmxMapLoader().load("test1.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

        // Create an OrthographicCamera and position it
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.update();
    }

    public void render() {
        // Set the camera's projection matrix
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();

        // Perform collision detection

        MapLayer collisionLayer = tiledMap.getLayers().get("collision");
        for (MapObject object : collisionLayer.getObjects()) {
            if (object instanceof RectangleMapObject) {
                Rectangle rectangle = ((RectangleMapObject) object).getRectangle();
                // Perform collision checks with the player or other game objects
                // Implement your collision logic here
            }
        }

    }

    public void dispose()
    {
        tiledMap.dispose();
    }
}