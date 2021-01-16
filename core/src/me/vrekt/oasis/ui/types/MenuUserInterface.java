package me.vrekt.oasis.ui.types;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Timer;
import me.vrekt.oasis.ui.UserInterface;

/**
 * A user interface meant for menus and other types that aren't in-game
 */
public abstract class MenuUserInterface extends UserInterface {

    /**
     * The timer
     */
    private final Timer timer = new Timer();

    /**
     * Schedule a new task
     *
     * @param action the action
     * @param delay  the delay
     */
    public void schedule(Runnable action, float delay) {
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                action.run();
            }
        }, delay);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.25f, .25f, .25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

}
