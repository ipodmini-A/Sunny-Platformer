package sunflowersandroses.platformergame.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Rectangle;
import sunflowersandroses.platformergame.Enemy;

import java.util.Random;

public class PlayerAttack
{
    private boolean attack;
    protected boolean spriteAttacking;
    protected float attackingElapsedTime;
    protected Rectangle attackHitbox;

    protected Player player;

    private Random random;

    private float attackTimer = 0;
    protected boolean attackUp = false;
    protected boolean attackDown = false;

    private Sound sound;

    public PlayerAttack(Player p)
    {
        player = p;
        random = new Random();
        sound = Gdx.audio.newSound(Gdx.files.internal("sounds/punch0.wav"));
    }
    /**
     * Allows the player to attack. When the attack button is pressed, a box is placed in front of the player briefly.
     * This method works with attackRender()
     * TODO: Have the animation play separate from the attack
     * TODO: Think of better names then "attacking"
     * :D
     */
    public void attack()
    {
        attack = true;
        spriteAttacking = true;
        attackTimer = 0;
        attackingElapsedTime += 1/3f; // This sucks but whatever. This just controls what frame is selected when attacking
        //attack = false;
        if (player.lookingUp)
        {
            attackUp = true;
        } else if (player.lookingDown)
        {
            attackDown = true;
        }
        attackLogic();
        // Jump to attackRender() //
    }

    /**
     * A basic timer used to calculate how long an attack should occur for.
     */
    private void attackTimer()
    {
        if (spriteAttacking)
        {
            attackTimer += Gdx.graphics.getDeltaTime();
        }
        float attackInterval = 0.2f;
        if (attackTimer >= attackInterval)
        {
            // Code to execute after the delay
            attackTimer = 0;
            attackHitbox = null;
            System.out.println("HitBox removed");
            spriteAttacking = false;
            attack = false;
            attackUp = false;
            attackDown = false;
        }
    }

    /**
     * deployAttack deals damage to the enemy and then sets attack to "false" to stop the player from dealing damage
     * @param e Enemy
     */
    public void deployAttack(Enemy e)
    {
        sound.play(1.0f);

        if (player.lookingDown && !player.isGrounded())
        {
            player.velocity.y += 500f;
        } else {
            if (player.isFacingRight()) {
                e.setVelocity(e.velocity.x + 150f, e.velocity.y + 150f);
                if (player.velocity.x >= 20) {
                    player.velocity.x -= 100f;
                }
            } else {
                e.setVelocity(e.velocity.x - 150f, e.velocity.y + 150f);
                if (player.velocity.x <= -20) {
                    player.velocity.x += 100f;
                }
            }
        }
        e.setHealth(e.getHealth() - (player.getAttackPoints() * random.nextInt(1,3)));
        attack = false;
    }

    /**
     * Renders the hitbox for attack.
     * This is more for debugging purposes and this method will soon include something such as an attack image
     */
    public void attackRender()
    {
        attackTimer();
        try {
            if (attack) {
                attackLogic();
            } else
            {
                attackHitbox = null;
            }
        } catch (Exception e)
        {
            // what
        }
    }

    /**
     * AttackLogic is a method that condenses the code within attack() and attackRender()
     * Both methods had the same exact code. This is to simplify the code.
     * attack and attackRender cannot be combined, as they both serve different functions. attack() is linked to the
     * actual key press, which can only be done once. attackRender is linked to the render method. This is so that it
     * constantly shows where the hitbox is as long as the hitbox is active.
     * I suppose there could be a way to fuse the methods depending on what is passed into the method, but for now
     * this works.
     */
    public void attackLogic()
    {
        if (attackUp)
        {
            attackHitbox = new Rectangle(player.position.x + (player.getWidth() / 5f), player.position.y + player.getHeight(), 25f, 25f);
        } else if (attackDown)
        {
            attackHitbox = new Rectangle(player.position.x + (player.getWidth() / 5f),player.position.y - 25f, 25f, 25f);
        } else
        {
            if (player.isFacingRight()) {
                attackHitbox = new Rectangle(player.position.x + player.getWidth(), player.position.y + (player.getHeight() / 3f), 25f, 25f);
            } else {
                attackHitbox = new Rectangle(player.position.x - player.getWidth(), player.position.y + (player.getHeight() / 3f), 25f, 25f);
            }
        }
    }

    protected float attackingFrameDuration = 1f;
    public void attackRenderMovement(Player p)
    {
        if (attackingElapsedTime >= (attackingFrameDuration))
        {
            attackingElapsedTime = 0;
        }
        player = p;
        if (player.velocity.x >= 200 || player.velocity.x <= -200 && (!attackDown &&    !attackUp )) { // Switch to have the appearance of a dash attack if its above 200 velocity
            player.drawSprite("attacking", player.position.x, player.position.y);
        } else {
            if (player.facingRight && !player.sprite.isFlipX()) {
                // Flip the sprite horizontally
                if (attackUp)
                {
                    player.drawSprite("attackUp",player.position.x,player.position.y);
                } else if (attackDown)
                {
                    player.drawSprite("attackDown",player.position.x,player.position.y);
                } else {
                    player.attackingFlippedFrame.setRegion(player.attackingAnimation.getKeyFrame(attackingElapsedTime, false));
                    player.attackingFlippedFrame.flip(true, false);
                    player.spriteBatch.draw(player.attackingFlippedFrame, player.position.x - (player.getWidth() / 2f) - player.xOffset, player.position.y - player.yOffset, Player.SPRITE_WIDTH, Player.SPRITE_HEIGHT);
                }
            } else {
                if (attackUp)
                {
                    player.drawSprite("attackUp", player.position.x, player.position.y);
                } else if (attackDown)
                {
                    player.drawSprite("attackDown", player.position.x, player.position.y);
                } else {
                    player.attackingFlippedFrame.setRegion(player.attackingAnimation.getKeyFrame(attackingElapsedTime, false));
                    player.attackingFlippedFrame.flip(false, false);
                    player.spriteBatch.draw(player.attackingFlippedFrame, player.position.x - (player.getWidth() / 2f) - player.xOffset, player.position.y - player.yOffset, Player.SPRITE_WIDTH, Player.SPRITE_HEIGHT);
                }
            }
        }
    }

    public void attackUpdate(Player player)
    {
        this.player = player;
        if (spriteAttacking)
        {
            player.state = Player.State.PUNCHING;
        }

        if (player.dashing)
        {
            player.state = Player.State.ATTACKING;
        }
        attackRender();
    }

    //                      //
    //  Setters and Getters //
    //                      //


    public Rectangle getAttackHitbox() {
        return attackHitbox;
    }

    public void setAttackHitbox(Rectangle attackHitbox) {
        this.attackHitbox = attackHitbox;
    }

    public boolean isAttack() {
        return attack;
    }

    public void setAttack(boolean attack) {
        this.attack = attack;
    }
}
