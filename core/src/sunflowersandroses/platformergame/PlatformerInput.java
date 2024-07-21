package sunflowersandroses.platformergame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

/**
 * PlatformerInput handles the input for the player. Currently, in the process of being deprecated.
 */
public class PlatformerInput implements InputProcessor
{

    private boolean jumpPressed;
    private boolean upPressed;
    private boolean leftPressed;
    private boolean rightPressed;
    private boolean downPressed;
    private boolean debugPressed;
    private boolean leftMouseClicked;
    private float leftMouseClickedX;
    private float leftMouseClickedY;

    public boolean isJumpPressed() {
        return jumpPressed;
    }

    public boolean isUpPressed() {
        return upPressed;
    }

    public boolean isLeftPressed() {
        return leftPressed;
    }

    public boolean isRightPressed() {
        return rightPressed;
    }

    public boolean isDownPressed() {
        return downPressed;
    }

    public boolean isDebugPressed() {
        return debugPressed;
    }

    public boolean isLeftMouseClicked() {
        return leftMouseClicked;
    }

    public float getLeftMouseClickedX() {
        return leftMouseClickedX;
    }

    public float getLeftMouseClickedY() {
        return leftMouseClickedY;
    }
    private Player p;

    public PlatformerInput(Player p)
    {
        this.p = p;
    }

    /**
     * update() constantly checks to see if input was entered. Currently, in the process of being deprecated.
     *
     * @deprecated
     */
    public void update()
    {
        jumpPressed = Gdx.input.isKeyJustPressed(Input.Keys.SPACE);
        upPressed = Gdx.input.isKeyPressed(Input.Keys.UP);
        leftPressed = Gdx.input.isKeyPressed(Input.Keys.LEFT);
        rightPressed = Gdx.input.isKeyPressed(Input.Keys.RIGHT);
        downPressed = Gdx.input.isKeyPressed(Input.Keys.DOWN);
        debugPressed = Gdx.input.isKeyPressed(Input.Keys.P);

        leftMouseClicked = Gdx.input.isButtonJustPressed(Input.Buttons.LEFT);
        if (leftMouseClicked)
        {
            leftMouseClickedX = Gdx.input.getX();
            leftMouseClickedY = Gdx.input.getY();
        }
    }

    @Override
    public boolean keyDown(int keycode)
    {
        /*
        switch (keycode)
        {
            case Input.Keys.UP:
                System.out.println("Jump");
                p.jump();
                break;
            default:
                break;
        }
        return true;
         */

        return false;

    }


    @Override
    public boolean keyUp(int keycode)
    {
        return false;
    }

    @Override
    public boolean keyTyped(char character)
    {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button)
    {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}

