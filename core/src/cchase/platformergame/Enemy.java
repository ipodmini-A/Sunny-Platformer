package cchase.platformergame;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

/**
 * Enemy.java
 * Enemy object. This class serves as a template for future enemy objects.
 * Enemy extends from Player, and has its input() method overridden. Enemy also has a modified updateBattle and renderBattle
 * method.
 */
public class Enemy extends Player
{
    public Enemy(float x, float y)
    {
        super(x,y);
        health = 100f;
        attack = 10f;
        defense = 3f;
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
        //state = State.STANDING;
    }

    public void renderBattle(SpriteBatch spriteBatch, float delta, float scale)
    {
        this.spriteBatch = spriteBatch;
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        //input();
        updateBattle(delta, scale);
        //drawSpriteBattle("standing", position.x, position.y, scale);
        //drawSprite("standing", position.x, position.y);
        switch(state)
        {
            case ATTACKING:
                drawSpriteBattle("attacking", position.x, position.y,scale);
                break;
            case DEFENDING:
                drawSpriteBattle("defending", position.x, position.y,scale);
                break;
            case STANCE:
                drawSpriteBattle("stance", position.x, position.y,scale);
                break;
        }
        spriteBatch.end();
        //System.out.println("Sprite X:" + sprite.getX() + " Sprite Y:" + sprite.getY());
        //System.out.println("Bounding X:" + bounds.getX() + " Bounding Y:" + bounds.getY());
    }
}
