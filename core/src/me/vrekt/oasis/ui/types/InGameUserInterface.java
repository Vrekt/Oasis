package me.vrekt.oasis.ui.types;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import me.vrekt.oasis.ui.UserInterface;

/**
 * An in-game user interface
 */
public abstract class InGameUserInterface extends UserInterface {

    /**
     * The label to draw the current FPS value.
     */
    protected final Label fpsTextLabel;

    public InGameUserInterface() {
        fpsTextLabel = new Label("FPS: " + Gdx.graphics.getFramesPerSecond(), skin);
    }

    /**
     * Update UI
     *
     * @param delta delta
     */
    public void updateUi(float delta) {
        fpsTextLabel.setText("FPS: " + Gdx.graphics.getFramesPerSecond());
        stage.act(delta);
    }

    /**
     * Draw UI.
     */
    public void drawUi() {
        stage.draw();
    }

}
