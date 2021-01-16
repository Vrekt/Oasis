package me.vrekt.oasis.ui;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import me.vrekt.oasis.Oasis;

/**
 * A base user interface
 */
public abstract class UserInterface extends ScreenAdapter {

    /**
     * The main stage
     */
    protected final Stage stage = new Stage();

    /**
     * The root table
     */
    protected final Table root = new Table();

    /**
     * The default UI skin
     */
    protected final Skin skin;

    /**
     * The game
     */
    protected final Oasis game;

    /**
     * Initialize
     * Retrieves the main game object and skin.
     */
    public UserInterface() {
        game = Oasis.get();
        skin = game.assets().defaultUiSkin();

        root.setFillParent(true);
        stage.addActor(root);
    }

    /**
     * Set a click listener
     *
     * @param actor  the actor
     * @param action the action to run
     */
    protected void setClickListenerTo(Actor actor, Runnable action) {
        actor.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                action.run();
            }
        });
    }

    /**
     * Show a dialog
     *
     * @param title   the title
     * @param message the message
     */
    public void showDialog(String title, String message) {
        final Dialog dialog = new Dialog(title, skin);
        dialog.text(message);
        dialog.button("Ok");
        dialog.show(stage);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
