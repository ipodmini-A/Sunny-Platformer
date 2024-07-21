package sunflowersandroses.platformergame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

/**
 * BattleOld is the old UI system for the game. I still see some benefit to having this form of UI exist, so until
 * further notice this class will stay here until Battle.java is in a useable state that I am okay with.
 *
 * @Depricated
 */
public class BattleOld {
    Player player;
    Enemy enemy;
    PlatformerInput platformerInput;
    ShapeRenderer shapeRenderer;
    SpriteBatch spriteBatch;
    private Rectangle rectangle;
    BitmapFont font;
    float mouseX;
    float mouseY;
    private boolean magicClicked;
    private Rectangle attackButtonBounds;
    private Rectangle defendButtonBounds;
    private Rectangle magicButtonBounds;
    private float width = 100;
    private float height = 50;

    public BattleOld(Player player, Enemy enemy)
    {
        this.player = player;
        this.enemy = enemy;
        spriteBatch = new SpriteBatch();
        font = new BitmapFont();
        shapeRenderer = new ShapeRenderer();
        //platformerInput = new PlatformerInput(player);
        magicClicked = false;

        attackButtonBounds = new Rectangle(100, 150, width, height);
        defendButtonBounds = new Rectangle(100, 100, width, height);
        magicButtonBounds = new Rectangle(100, 50, width, height);
    }

    public void render()
    {
        if (platformerInput.isLeftMouseClicked())
        {
            System.out.println(platformerInput.getLeftMouseClickedX());
            System.out.println(platformerInput.getLeftMouseClickedY());
            mouseX = platformerInput.getLeftMouseClickedX();
            mouseY = (Gdx.graphics.getHeight() - platformerInput.getLeftMouseClickedY()); //lol y is inverted so this is to un-invert it.

            /*
            Controls for the Magic button.
            TODO: Rename attack, defend, etc to "Button1, Button2, Button3, etc"
             */
            if (mouseX > magicButtonBounds.x && mouseX < magicButtonBounds.x + magicButtonBounds.getWidth()
                    && mouseY > magicButtonBounds.y &&  mouseY < magicButtonBounds.y + magicButtonBounds.getHeight() && !magicClicked)
            {
                magicClicked = true;
            }else if (mouseX > magicButtonBounds.x && mouseX < magicButtonBounds.x + magicButtonBounds.getWidth()
                    && mouseY > magicButtonBounds.y &&  mouseY < magicButtonBounds.y + magicButtonBounds.getHeight() && magicClicked)
            {
                magicClicked = false;
            }

        }
        renderUI();
        //platformerInput.update();
    }

    public void renderUI()
    {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.CYAN);
        shapeRenderer.rect(attackButtonBounds.x, attackButtonBounds.y, attackButtonBounds.width, attackButtonBounds.height); // Attack
        shapeRenderer.rect(defendButtonBounds.x, defendButtonBounds.y, defendButtonBounds.width, defendButtonBounds.height); // Defend
        shapeRenderer.rect(magicButtonBounds.x, magicButtonBounds.y, magicButtonBounds.width, magicButtonBounds.height); // Magic
        shapeRenderer.end();

        spriteBatch.begin();
        // Render player and enemy information
        font.draw(spriteBatch, "Player HP: " + 100/* HP */, 100, 500);
        font.draw(spriteBatch, "Enemy HP: " + 100 /* HP */, 100, 450);

        if (!magicClicked)
        {
            // Render action buttons
            if (100 /* HP */ > 0)
            {
                font.draw(spriteBatch, "Attack", attackButtonBounds.x + (attackButtonBounds.getWidth() / 2),
                        attackButtonBounds.y + (attackButtonBounds.getHeight() / 2));
                font.draw(spriteBatch, "Defend", defendButtonBounds.x + (defendButtonBounds.getWidth() / 2),
                        defendButtonBounds.y + (defendButtonBounds.getHeight() / 2));
                font.draw(spriteBatch, "Magic", magicButtonBounds.x + (magicButtonBounds.getWidth() / 2),
                        magicButtonBounds.y + (magicButtonBounds.getHeight() / 2));
            } else
            {
                font.draw(spriteBatch, "Game Over", 100, 300);
            }
        } else
        {
            font.draw(spriteBatch, "Magic1", attackButtonBounds.x + (attackButtonBounds.getWidth() / 2),
                    attackButtonBounds.y + (attackButtonBounds.getHeight() / 2));
            font.draw(spriteBatch, "Magic2", defendButtonBounds.x + (defendButtonBounds.getWidth() / 2),
                    defendButtonBounds.y + (defendButtonBounds.getHeight() / 2));
            font.draw(spriteBatch, "Back", magicButtonBounds.x + (magicButtonBounds.getWidth() / 2),
                    magicButtonBounds.y + (magicButtonBounds.getHeight() / 2));
        }

        spriteBatch.end();
    }

    public void dispose() {
        spriteBatch.dispose();
        font.dispose();
        shapeRenderer.dispose();
    }
}