package me.vrekt.oasis.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * A basic outline for a user interface.
 */
public abstract class UserInterface implements Disposable {

    /**
     * The main UI stage.
     */
    protected final Stage stage;

    /**
     * The sprite batch for this UI
     */
    protected final SpriteBatch spriteBatch;

    /**
     * Initialize the interface
     */
    public UserInterface() {
        spriteBatch = new SpriteBatch();
        stage = new Stage(new ScreenViewport(), spriteBatch);

        Gdx.input.setInputProcessor(stage);
    }

    /**
     * Create
     */
    public abstract void create();

    /**
     * Render
     *
     * @param delta the delta time
     */
    public void render(float delta) {
        spriteBatch.setProjectionMatrix(stage.getCamera().combined);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        disposeElements();

        spriteBatch.dispose();
        stage.dispose();
    }

    /**
     * Dispose anything internal.
     */
    protected void disposeElements() {

    }
}
