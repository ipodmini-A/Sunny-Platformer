package cchase.platformergame.screens;

import cchase.platformergame.*;
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
    Player[] player = new Player[3];
    Enemy[] enemy = new Enemy[2];
    BattleNew battle;

    public BattleScreen(PlatformerGame game, Player player, Enemy enemy)
    {
        this.game = game;
        batch = new SpriteBatch();
        this.player[0] = player;
        this.player[1] = new Player();
        this.player[1].setName("Player 2");
        this.player[2] = new Player();
        this.player[2].setName("Player 3");
        this.enemy[0] = enemy;
        this.enemy[1] = new Enemy(0,0);
        this.enemy[0].setName("Enemy 1");
        this.enemy[1].setName("Enemy 2");
        battle = new BattleNew(this.player,this.enemy);
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
            //battle.music.stop();
            game.setScreen(GameState.gameScreen);
        }

        /*
        if (enemy[0].getHealth() <= 0)
        {
            //battle.music.stop();
            game.setScreen(GameState.gameScreen);
        }

         */

    }

    public void dispose()
    {
        batch.dispose();
        for (int i = 0; i < player.length; i++)
        {
            player[i].dispose();
        }
        for (int i = 0; i < enemy.length; i++)
        {
            enemy[i].dispose();
        }
        battle.dispose();
    }

}
