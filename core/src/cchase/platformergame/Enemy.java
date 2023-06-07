package cchase.platformergame;

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
}
