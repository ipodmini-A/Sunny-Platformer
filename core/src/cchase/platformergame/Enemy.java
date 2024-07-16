package cchase.platformergame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.utils.Array;

/**
 * Enemy.java
 * Enemy object. This class serves as a template for future enemy objects.
 * Enemy extends from Player, and has its input() method overridden. Enemy also has a modified updateBattle and renderBattle
 * method.
 */
public class Enemy extends Player
{
    public int id;
    public Enemy(float x, float y)
    {
        super(x,y);

        textureAtlas = new TextureAtlas("enemysprites.txt");
        addSprites();
        //This might cause issues in the future ¯\_(ツ)_/¯
        runningElapsedTime = 0f;
        runningFlippedFrame = new TextureRegion(runningAnimation.getKeyFrame(runningElapsedTime,true));

        health = 100f;
        attackPoints = 10f;
        defensePoints = 3f;
    }

    /**
     * Currently this method is here to override player input.
     * TODO: This method will serve as the movement for the enemy.
     */
    @Override
    public void input(float delta)
    {
        AIMovement(delta);
        //Currently this is here to override input.
        position.add(velocity.x * dt, velocity.y * dt);
        /*
        if (velocity.x < 200)
        {
            velocity.x += 10;
        }
         */
        //jump();
    }

    float movement = 0;
    public void AIMovement (float delta)
    {
        //AI? MORE LIKE 500 IF STATEMENTS LMAO
        movement += delta;
        if (movement >= 4)
        {
            facingRight = !facingRight;
            movement = 0;
        }
        if (isTouchingRightWall())
        {
            facingRight = !facingRight;
            position.x += 3;
        }
        if (isTouchingLeftWall())
        {
            facingRight = !facingRight;
            position.x -= 3;
        }
        if (facingRight && !isTouchingLeftWall())
        {
            velocity.x = 100f;
            //position.x += 1f;
        } else if (!facingRight && !isTouchingRightWall())
        {
            velocity.x = -100f;
            //position.x -= 1f;
        }

    }

    @Override
    public void render(SpriteBatch spriteBatch,float delta)
    {
        this.spriteBatch = spriteBatch;
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        input(delta);
        //changeAnimationSpeed(delta);
        update(delta);
        renderMovement(spriteBatch);
        //drawSprite("standing", position.x, position.y);
        spriteBatch.end();
    }

    public void renderMovement(SpriteBatch spriteBatch)
    {
        runningElapsedTime += Gdx.graphics.getDeltaTime();
        // If elapsedTime is left uncapped, it causes the current implementation of animation to continuously go faster
        // as long as the game is active. Until the animation implementation changes, the elapsed time is to remain capped.
        // A cap of two to four seems to work fine.
        // Update: There was a looping error, causing the animation to abruptly cut in the middle of it and reset.
        // Setting the cap to be the frameDuration * the amount of frames (in this case, 4) seems to fix the looping error.
        if (runningElapsedTime >= (runningFrameDuration * 4f))
        {
            runningElapsedTime = 0;
        }
        //System.out.println();
        if (velocity.x < 0)
        {
            facingRight = false;
        } else if (velocity.x > 0)
        {
            facingRight = true;
        }

        switch (state)
        {
            case STANDING:
                drawSprite("standing", position.x, position.y);
                break;
            case WALKING:
                // I don't know why this works but... for know it works fine.
                // This is very flawed, as its using "sprite" even though this block of code doesn't rely on sprite at all.
                // That being said, it's a great way to check the direction of the player.
                if (facingRight && !sprite.isFlipX()) {
                    // Flip the sprite horizontally
                    runningFlippedFrame.setRegion(runningAnimation.getKeyFrame(runningElapsedTime,true));
                    //flippedFrame = new TextureRegion(animation.getKeyFrame(elapsedTime,true));
                    runningFlippedFrame.flip(true, false);
                    spriteBatch.draw(runningFlippedFrame, position.x - (WIDTH / 2f) - 5f, position.y, SPRITE_WIDTH, SPRITE_HEIGHT);
                } else
                {
                    runningFlippedFrame.setRegion(runningAnimation.getKeyFrame(runningElapsedTime,true));
                    //flippedFrame = new TextureRegion(animation.getKeyFrame(elapsedTime,true));
                    runningFlippedFrame.flip(false, false);
                    spriteBatch.draw(runningFlippedFrame, position.x - (WIDTH / 2f) - 5f, position.y, SPRITE_WIDTH, SPRITE_HEIGHT);
                }
                break;
            case JUMPING:
                drawSprite("jumping", position.x, position.y);
                break;
            case FALLING:
                drawSprite("falling", position.x, position.y);
                break;
            case WALL_RIDING:
                drawSprite("wallriding", position.x, position.y);
                break;
            case LOOKING_UP:
                drawSprite("lookingUp", position.x, position.y);
                break;
            case LOOKING_DOWN:
                drawSprite("lookingDown", position.x, position.y);
                break;
            case TOUCHING_WALL:
                drawSprite("touchingWall", position.x, position.y);
                break;
            case ATTACKING:
                drawSprite("attacking", position.x, position.y);
                break;
            case DEFENDING:
                drawSprite("defending", position.x, position.y);
                break;
            case PUNCHING:
                drawSprite("punching", position.x, position.y);
                break;
            case STANCE:
                drawSprite("stance", position.x, position.y);
                break;
        }
    }

    private void addSprites()
    {
        Array<TextureAtlas.AtlasRegion> regions = textureAtlas.getRegions();

        for (TextureAtlas.AtlasRegion region : regions) {
            Sprite sprite = textureAtlas.createSprite(region.name);

            sprites.put(region.name, sprite);
        }
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

    //                      //
    // Setters and Getters  //
    //                      //


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
