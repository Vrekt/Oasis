package me.vrekt.oasis.ui.character;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import me.vrekt.oasis.asset.character.CharacterType;
import me.vrekt.oasis.ui.types.MenuUserInterface;

/**
 * Character selection screenF
 */
public final class CharacterSelectionScreen extends MenuUserInterface {

    /**
     * The callback
     */
    private Runnable callback;

    public CharacterSelectionScreen() {
        createComponents();
    }

    /**
     * Set the "on continue" action listener
     *
     * @param action action
     */
    public void onContinue(Runnable action) {
        this.callback = action;
    }

    @Override
    public void show() {
        Gdx.app.log("UI", "Showing character selection screen");
        Gdx.input.setInputProcessor(stage);
    }

    /**
     * Create components
     */
    private void createComponents() {
        // sel character label
        final Label label = new Label("Select a character.", skin);
        root.add(label).fillX().uniformX();
        root.row();

        // create all character types and add them
        for (CharacterType character : CharacterType.values()) {
            final TextureRegion display = game.assets().getCharacter(character).get().findRegion("display");
            final Label displayName = new Label(character.name(), skin);
            displayName.setColor(1f, 1f, 1f, 1f);

            final Image image = new Image(display);
            setClickListenerTo(image, () -> {
                // reset all other actor colors
                root.getChildren().forEach(actor -> {
                    if (actor instanceof Label) actor.setColor(1f, 1f, 1f, 1f);
                });
                displayName.setColor(.55f, .55f, .55f, 1f);
                game.thePlayer().character(character);
            });

            root.add(displayName).uniformX();
            root.add(image).uniformX();
        }
        root.row();

        // continue and back buttons
        final TextButton continueButton = new TextButton("Continue", skin);
        setClickListenerTo(continueButton, () -> callback.run());

        final TextButton backButton = new TextButton("Back", skin);
        setClickListenerTo(backButton, game::showMainMenu);

        root.add(continueButton).fillX().uniformX();
        root.row();
        root.add(backButton).fillX().uniformX();
    }

}
