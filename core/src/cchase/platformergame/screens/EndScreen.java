package cchase.platformergame.screens;

import cchase.platformergame.GameState;
import cchase.platformergame.LevelManager;
import cchase.platformergame.PlatformerGame;
import cchase.platformergame.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;

import java.util.logging.Level;

public class EndScreen extends ScreenAdapter
{

    PlatformerGame game;
    boolean playerWon;

    public EndScreen(PlatformerGame game, boolean win)
    {
        playerWon = win;
        this.game = game;
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new InputAdapter() {

            @Override
            public boolean keyDown(int keyCode) {

                if (keyCode == Input.Keys.ENTER)
                {
                    // TODO: Figure out spawning after losing.
                    //LevelManager.player.setPosition(LevelManager.currentLevel.);
                    game.setScreen(new TitleScreen(game));
                }
                return true;
            }
        });
    }

    @Override
    public void render(float delta)
    {
        if (playerWon) {
            Gdx.gl.glClearColor(0f, .25f, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            game.batch.begin();
            game.font.draw(game.batch, "You Won!", Gdx.graphics.getWidth() * .25f, Gdx.graphics.getHeight() * .75f);
            game.font.draw(game.batch, "Press enter to restart.", Gdx.graphics.getWidth() * .25f, Gdx.graphics.getHeight() * .25f);
            game.batch.end();
        } else
        {
            Gdx.gl.glClearColor(.25f, 0f, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            game.batch.begin();
            game.font.draw(game.batch, "You Died...", Gdx.graphics.getWidth() * .25f, Gdx.graphics.getHeight() * .75f);
            game.font.draw(game.batch, "Press enter to restart.", Gdx.graphics.getWidth() * .25f, Gdx.graphics.getHeight() * .25f);
            game.batch.end();
        }
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }
}