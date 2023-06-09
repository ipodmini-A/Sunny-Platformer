package cchase.platformergame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class BattleScreen extends ScreenAdapter
{
    PlatformerGame game;
    private SpriteBatch batch;
    Player player;
    Enemy enemy;
    Battle battle;

    public BattleScreen(PlatformerGame game, Player player, Enemy enemy)
    {
        this.game = game;
        batch = new SpriteBatch();
        this.player = player;
        this.enemy = enemy;
        battle = new Battle(player,enemy);
    }

    public void show()
    {
        super.show();
    }

    public void render(float delta)
    {
        Gdx.gl.glClearColor(0.0f, 0.1f, 0.2f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        battle.render(delta);

        if (Gdx.input.isKeyPressed(Input.Keys.P))
        {
            game.setScreen(GameState.gameScreen);
        }
    }

    public void dispose()
    {
        batch.dispose();
        player.dispose();
    }

}
