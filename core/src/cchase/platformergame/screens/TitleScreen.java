package cchase.platformergame.screens;

import cchase.platformergame.PlatformerGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;

public class TitleScreen extends ScreenAdapter
{
    PlatformerGame game;
    public TitleScreen(PlatformerGame game)
    {
        this.game = game;
    }

    @Override
    public void show()
    {
        game.camera.update();
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keyCode) {
                if (keyCode == Input.Keys.SPACE) {
                    game.setScreen(new MainMenuScreen(game));
                }
                return true;
            }
        });

    }
    @Override
    public void render(float delta)
    {
        Gdx.gl.glClearColor(0, .25f, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.begin();
        game.font.draw(game.batch, "Welcome to a generic platformer!", Gdx.graphics.getWidth() * .25f, Gdx.graphics.getHeight() * .75f);
        game.font.draw(game.batch, "Currently in development. There is no win condition.", Gdx.graphics.getWidth() * .25f, Gdx.graphics.getHeight() * .5f);
        game.font.draw(game.batch, "Press space to play.", Gdx.graphics.getWidth() * .25f, Gdx.graphics.getHeight() * .25f);
        game.batch.end();
    }
    @Override
    public void hide()
    {
        Gdx.input.setInputProcessor(null);

    }
}