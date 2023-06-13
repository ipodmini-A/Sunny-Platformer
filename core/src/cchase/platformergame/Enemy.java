package cchase.platformergame;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Enemy extends Player
{
    public Enemy(float x, float y)
    {
        super(x,y);

    }

    /**
     * Currently this method is here to override player input.
     * TODO: This method will serve as the movement for the enemy.
     */
    @Override
    public void input()
    {
        //Currently this is here to override input.
        /*
        if (velocity.x < 200)
        {
            velocity.x += 10;
        }
         */
        //jump();
    }

    public void updateBattle(float delta, float scale)
    {
        sprite.setBounds(
                position.x,
                position.y,
                WIDTH * scale,
                HEIGHT * scale);
        // Update position based on velocity
        bounds.setPosition(position.x, position.y); // Update the bounds with the new position
        facingRight = true;
        state = State.STANDING;
    }

    public void renderBattle(SpriteBatch spriteBatch, float delta, float scale)
    {
        this.spriteBatch = spriteBatch;
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        //input();
        updateBattle(delta, scale);
        drawSpriteBattle("standing", position.x, position.y, scale);
        //drawSprite("standing", position.x, position.y);
        spriteBatch.end();
        //System.out.println("Sprite X:" + sprite.getX() + " Sprite Y:" + sprite.getY());
        //System.out.println("Bounding X:" + bounds.getX() + " Bounding Y:" + bounds.getY());
    }
}
