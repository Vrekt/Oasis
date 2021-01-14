package me.vrekt.oasis.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Timer;
import me.vrekt.oasis.Oasis;

import java.util.HashMap;
import java.util.Map;

/**
 * An abstract user interface type utility
 */
public abstract class UserInterface extends ScreenAdapter {

    /**
     * A set of containers for this UI.
     */
    private final Map<String, Table> containers = new HashMap<>();

    /**
     * The main stage
     */
    protected final Stage stage = new Stage();

    /**
     * The game
     */
    protected final Oasis game;

    /**
     * The default UI skin
     */
    protected final Skin skin;

    /**
     * The timer
     */
    protected final Timer timer;

    /**
     * If the UI is initialized
     */
    protected boolean isInitialized;

    /**
     * Sets the internal skin
     */
    public UserInterface() {
        skin = Oasis.get().assets().defaultUiSkin();
        game = Oasis.get();
        timer = new Timer();

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void show() {
        Gdx.app.log("UserInterface", "Showing UI.");
        initializeInternal();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.25f, .25f, .25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    /**
     * Show a dialog
     *
     * @param title   the tittle
     * @param message the message
     */
    public void showDialog(String title, String message) {
        final Dialog dialog = new Dialog(title, skin);
        dialog.text(message);
        dialog.button("Ok");
        dialog.show(stage);
        stage.setScrollFocus(dialog);
    }

    /**
     * Create a new container.
     * This method will automatically set {@code setFillParent} to {@code true}
     *
     * @param name the container name
     * @return the new container
     */
    protected Table createContainer(String name) {
        final Table container = new Table();
        container.setFillParent(true);
        containers.put(name, container);
        return container;
    }

    /**
     * Get a container
     *
     * @param name the name
     * @return the table
     */
    protected Table getContainer(String name) {
        return containers.get(name);
    }

    /**
     * Show a container
     *
     * @param name the name
     */
    protected void showContainer(String name) {
        stage.clear();
        stage.addActor(containers.get(name));
    }

    /**
     * Show a container then run the action
     *
     * @param name   the name
     * @param action the action
     */
    protected void showContainerThen(String name, Runnable action) {
        showContainer(name);
        action.run();
    }

    /**
     * Do something in the future
     *
     * @param runnable the runnable
     * @param delay    the delay
     */
    protected void inFuture(Runnable runnable, float delay) {
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                runnable.run();
            }
        }, delay);
    }

    /**
     * Initialize internal
     */
    private void initializeInternal() {
        if (!isInitialized) initialize();
    }

    /**
     * Initialize the UI
     */
    protected abstract void initialize();

}
