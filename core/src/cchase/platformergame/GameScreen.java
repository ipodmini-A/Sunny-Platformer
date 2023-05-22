package cchase.platformergame;

import com.badlogic.gdx.ScreenAdapter;

public class GameScreen extends ScreenAdapter
{
    PlatformerGame game;
    PlatformerInput input = new PlatformerInput();
    public GameScreen(PlatformerGame game)
    {
        this.game = game;
    }

    @Override
    public void show()
    {
        super.show();
    }

    @Override
    public void render(float delta)
    {
        input.update();
    }
}
