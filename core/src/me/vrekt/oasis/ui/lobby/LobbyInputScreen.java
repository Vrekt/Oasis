package me.vrekt.oasis.ui.lobby;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import me.vrekt.oasis.ui.types.MenuUserInterface;

/**
 * Screen for inputting lobby invite code
 */
public final class LobbyInputScreen extends MenuUserInterface {

    /**
     * The UI callback
     */
    private Runnable callback;

    public LobbyInputScreen() {
        createComponents();
    }

    /**
     * Invoked when the join button is clicked
     *
     * @param callback the callback
     */
    public void onJoin(Runnable callback) {
        this.callback = callback;
    }

    @Override
    public void show() {
        Gdx.app.log("UI", "Showing lobby invite input screen");
        Gdx.input.setInputProcessor(stage);
    }

    /**
     * Create components
     */
    private void createComponents() {
        final Label label = new Label("Enter the lobby invite code below", skin);
        root.add(label).fillX().uniformX();
        root.row();

        final TextField input = new TextField("", skin);
        input.setMessageText("Enter lobby invite code");
        root.add(input).fillX().uniformX();

        final TextButton joinButton = new TextButton("Join", skin);
        root.add(joinButton).fillX().uniformX();
        root.row();

        setClickListenerTo(joinButton, () -> {
            if (input.getText().isEmpty() || input.getText().trim().isEmpty()) {
                showDialog("Invalid code", "You must enter a numerical invite code");
                return;
            }

            try {
                final int lobby = Integer.parseInt(input.getText());
                game.thePlayer().lobbyIn(lobby);

                callback.run();
            } catch (NumberFormatException exception) {
                showDialog("Invalid code", "You must enter a numerical invite code");
                input.setText("");
            }

        });

        final TextButton backButton = new TextButton("Back", skin);
        root.add(backButton).center();
        setClickListenerTo(backButton, game::showMainMenu);
    }

}
