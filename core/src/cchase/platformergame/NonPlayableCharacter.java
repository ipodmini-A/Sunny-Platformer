package cchase.platformergame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class NonPlayableCharacter extends Player
{
    private boolean touchingPLayer = false;
    private ShapeRenderer shapeRenderer;

    public NonPlayableCharacter(float x, float y)
    {
        super(x, y);
        shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void input()
    {
        //Currently this is here to override input.
    }

    public void displayMessage(Player player)
    {
        if (Gdx.input.isKeyPressed(Input.Keys.Q))
        {
            player.setDownMove(false);
        }
        System.out.println("Hi!");
        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.begin();
        shapeRenderer.setColor(1,0,0,0.5f);
        shapeRenderer.rect(100, 100, Gdx.graphics.getWidth() - 100, Gdx.graphics.getHeight() / 4f);
        shapeRenderer.end();
    }



    public boolean isTouchingPLayer() {
        return touchingPLayer;
    }

    public void setTouchingPLayer(boolean touchingPLayer) {
        this.touchingPLayer = touchingPLayer;
    }
}
