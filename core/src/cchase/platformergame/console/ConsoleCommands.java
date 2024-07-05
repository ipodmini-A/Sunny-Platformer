package cchase.platformergame.console;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.strongjoshua.console.CommandExecutor;
import com.strongjoshua.console.Console;
import com.strongjoshua.console.GUIConsole;

public class ConsoleCommands extends CommandExecutor {

    private static GUIConsole console;

    public ConsoleCommands(){

        Skin skin = new Skin(Gdx.files.internal("ui/uiskin.json"), new TextureAtlas(Gdx.files.internal("ui/uiskin.atlas")));
        console = new GUIConsole(skin, false);
        console.setCommandExecutor(this);
        console.setSizePercent(50, 50);
        console.enableSubmitButton(true);
    }

    public static Console getConsole(){
        return console;
    }

    public static void draw(){
        if (console.isVisible()) {
            console.draw();
        }
    }

    public void test(){
        Gdx.app.log("Hi!","I am your friendly console");
    }

    public void hide(){
        console.setVisible(false);
    }
}