package cchase.platformergame.screens;

import cchase.platformergame.PlatformerGame;
import cchase.platformergame.Player;
import cchase.platformergame.SlotsGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;

public class SlotsScreen extends ScreenAdapter {

    private Stage stage;
    private Skin skin;
    private PlatformerGame game;

    private SlotsGame slotsGame;

    public SlotsScreen(final PlatformerGame game, Player p)
    {
        this.game = game;
        stage = new Stage();

        slotsGame = new SlotsGame(game, p);


    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(slotsGame.getStage());
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(54/255f, 89/255f, 74/255f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        slotsGame.render(delta);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int i, int i1) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
