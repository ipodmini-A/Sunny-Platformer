package cchase.platformergame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class PlatformerInput
{

    private boolean jumpPressed;
    private boolean leftPressed;
    private boolean rightPressed;

    public boolean isJumpPressed()
    {
        return jumpPressed;
    }

    public boolean isLeftPressed()
    {
        return leftPressed;
    }

    public boolean isRightPressed()
    {
        return rightPressed;
    }

    public void update()
    {
        jumpPressed = Gdx.input.isKeyJustPressed(Input.Keys.SPACE)
                || Gdx.input.isKeyJustPressed(Input.Keys.UP);
        leftPressed = Gdx.input.isKeyPressed(Input.Keys.LEFT);
        rightPressed = Gdx.input.isKeyPressed(Input.Keys.RIGHT);
    }
}
