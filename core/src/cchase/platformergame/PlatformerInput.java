package cchase.platformergame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class PlatformerInput
{

    private boolean jumpPressed;
    private boolean upPressed;
    private boolean leftPressed;
    private boolean rightPressed;
    private boolean downPressed;
    private boolean debugPressed;

    public boolean isJumpPressed()
    {
        return jumpPressed;
    }

    public boolean isUpPressed()
    {
        return upPressed;
    }

    public boolean isLeftPressed()
    {
        return leftPressed;
    }

    public boolean isRightPressed()
    {
        return rightPressed;
    }

    public boolean isDownPressed()
    {
        return downPressed;
    }

    public boolean isDebugPressed()
    {
        return debugPressed;
    }

    public void update()
    {
        jumpPressed = Gdx.input.isKeyJustPressed(Input.Keys.SPACE);
        upPressed = Gdx.input.isKeyPressed(Input.Keys.UP);
        leftPressed = Gdx.input.isKeyPressed(Input.Keys.LEFT);
        rightPressed = Gdx.input.isKeyPressed(Input.Keys.RIGHT);
        downPressed = Gdx.input.isKeyPressed(Input.Keys.DOWN);
        debugPressed = Gdx.input.isKeyPressed(Input.Keys.P);
    }
}
