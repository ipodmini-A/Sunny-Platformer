package cchase.platformergame;


import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Battle
{
    Player player;
    Enemy enemy;
    PlatformerInput platformerInput;

    ShapeRenderer shapeRenderer;
    SpriteBatch fontBatch;
    BitmapFont font;

    public Battle(Player player, Enemy enemy)
    {
        this.player = player;
        this.enemy = enemy;
        fontBatch = new SpriteBatch();
        font = new BitmapFont();
        shapeRenderer = new ShapeRenderer();
        platformerInput = new PlatformerInput();
    }

    public void BattleLayout()
    {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1,1,1,0);
        shapeRenderer.rect(100, 100, 100, 100);
        shapeRenderer.end();
        fontBatch.begin();
        font.draw(fontBatch, "DEBUG", 25,25);
        fontBatch.end();
    }

    public void render()
    {
        BattleLayout();
        platformerInput.update();
    }

    public void dispose()
    {
        fontBatch.dispose();
        font.dispose();
        shapeRenderer.dispose();
    }
}
