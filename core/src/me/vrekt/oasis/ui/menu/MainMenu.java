package me.vrekt.oasis.ui.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import me.vrekt.oasis.ui.character.CharacterSelectionScreen;
import me.vrekt.oasis.ui.connect.ConnectLoadingScreen;
import me.vrekt.oasis.ui.lobby.LobbyInputScreen;
import me.vrekt.oasis.ui.types.MenuUserInterface;

/**
 * Main menu UI
 */
public final class MainMenu extends MenuUserInterface {

    /**
     * The character selection screen
     */
    private final CharacterSelectionScreen characterSelectionScreen;

    /**
     * Lobby invite input screen
     */
    private final LobbyInputScreen lobbyInputScreen;

    public MainMenu() {
        createComponents();

        characterSelectionScreen = new CharacterSelectionScreen();
        lobbyInputScreen = new LobbyInputScreen();
    }

    @Override
    public void show() {
        Gdx.app.log("UI", "Showing main menu");
        Gdx.input.setInputProcessor(stage);
    }

    /**
     * Create components
     */
    private void createComponents() {
        // game name label
        final Label gameLabel = new Label("Oasis", skin);
        root.add(gameLabel).fillX().uniformX();
        root.row();

        // username fields
        final TextField usernameField = new TextField("", skin);
        usernameField.setMessageText("Enter username...");
        root.add(usernameField).fillX().uniformX();
        root.row();

        // buttons
        final TextButton createLobbyButton = new TextButton("Create new lobby", skin);
        root.add(createLobbyButton).fillX().uniformX();
        root.row();

        setClickListenerTo(createLobbyButton, () -> {
            if (!handleUsername(usernameField.getText())) return;
            handleConnecting(() -> {
                characterSelectionScreen.onContinue(() -> game.network().connection().createLobby());
                game.show(characterSelectionScreen);
            });
        });

        final TextButton joinLobbyButton = new TextButton("Join existing lobby", skin);
        root.add(joinLobbyButton).fillX().uniformX();

        setClickListenerTo(joinLobbyButton, () -> {
            if (!handleUsername(usernameField.getText())) return;
            handleConnecting(() -> {
                lobbyInputScreen.onJoin(() -> game.network().connection().joinLobby());
                characterSelectionScreen.onContinue(() -> game.show(lobbyInputScreen));
                game.show(characterSelectionScreen);
            });
        });
    }

    /**
     * Handle username validation
     *
     * @param usernameText the username text
     * @return {@code true} if successful
     */
    private boolean handleUsername(String usernameText) {
        if (usernameText.isEmpty() || usernameText.trim().isEmpty()) {
            showDialog("Username", "You must enter a username.");
            return false;
        }
        game.thePlayer().username(usernameText);
        return true;
    }

    /**
     * Handle connecting
     *
     * @param callback the callback
     */
    private void handleConnecting(Runnable callback) {
        if (game.network().isConnected()) {
            callback.run();
        } else {
            final ConnectLoadingScreen screen = new ConnectLoadingScreen(callback);
            game.showSync(screen);
        }
    }

    @Override
    public void dispose() {
        characterSelectionScreen.dispose();
        lobbyInputScreen.dispose();
    }
}
