package me.vrekt.oasis.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import me.vrekt.oasis.ui.menu.MainMenuUserInterface;

/**
 * The main menu screen
 */
public final class MainMenuScreen extends ScreenAdapter {

    /**
     * The UI
     */
    private final MainMenuUserInterface ui = new MainMenuUserInterface();

    /**
     * Show a dialog
     *
     * @param title the title
     * @param error the error
     */
    public void showDialog(String title, String error) {
        ui.showDialog(title, error);
    }

    @Override
    public void show() {
        Gdx.app.log("MainMenu", "Showing the main menu.");
        ui.create();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.25f, .25f, .25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        ui.render(delta);
    }

    @Override
    public void dispose() {
        ui.dispose();
    }
}
