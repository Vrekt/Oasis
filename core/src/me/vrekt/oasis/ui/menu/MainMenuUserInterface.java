package me.vrekt.oasis.ui.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import me.vrekt.oasis.Oasis;
import me.vrekt.oasis.ui.UserInterface;

/**
 * A user interface for the main menu.
 */
public final class MainMenuUserInterface extends UserInterface {

    /**
     * The game
     */
    private final Oasis game = Oasis.get();

    /**
     * The UI Skin atlas
     */
    private TextureAtlas uiSkinAtlas;

    /**
     * The UI Skin
     */
    private Skin uiSkin;

    @Override
    public void create() {
        uiSkinAtlas = new TextureAtlas("ui\\UserInterface.atlas");
        uiSkin = new Skin(Gdx.files.internal("ui\\UserInterface.json"), uiSkinAtlas);
        createElements();
    }

    /**
     * Create on-screen elements
     */
    private void createElements() {
        initializeMainMenuContainer();
        initializeSettingsContainer();
    }

    /**
     * Initialize the main menu container.
     * TODO: Improve the UI over-time.
     */
    private void initializeMainMenuContainer() {
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        // game name label
        final Label gameNameLabel = new Label("Oasis", uiSkin);
        root.add(gameNameLabel).center().row();

        // username entry
        final TextField usernameField = new TextField("", uiSkin);
        usernameField.setMessageText("Enter username");
        root.add(usernameField).center().row();

        // action buttons
        final TextButton createLobbyButton = new TextButton("Create lobby", uiSkin);
        root.add(createLobbyButton).center().row();
        final TextButton joinLobbyButton = new TextButton("Join lobby", uiSkin);
        root.add(joinLobbyButton).center().row();
        final TextButton settingsButton = new TextButton("Settings", uiSkin);
        root.add(settingsButton).center().row();

        createLobbyButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                changeButtonState(true, createLobbyButton, joinLobbyButton, settingsButton);
                game.thePlayer().entityName(usernameField.getText());
                handleCreateLobbyChange();
            }
        });

        joinLobbyButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                changeButtonState(true, createLobbyButton, joinLobbyButton, settingsButton);
                game.thePlayer().entityName(usernameField.getText());
                handleJoinLobbyChange();
            }
        });

    }

    /**
     * Enable/disable buttons
     *
     * @param state   the state
     * @param buttons the buttons
     */
    private void changeButtonState(boolean state, Button... buttons) {
        for (Button button : buttons) button.setDisabled(state);
    }

    /**
     * Connect to the master server
     *
     * @return the result
     */
    private boolean connectToMasterServer() {
        try {
            game.network().connectToMasterServer().get();
            return true;
        } catch (Exception any) {
            showDialog("Failed to connect", "Failed to connect to the server.\n" + any.getMessage());
        }
        return false;
    }

    /**
     * Handle creating a lobby
     */
    private void handleCreateLobbyChange() {
        if (connectToMasterServer()) {
            game.network().networkCreateLobby();
        }
    }

    /**
     * Handle joining a lobby
     */
    private void handleJoinLobbyChange() {
        if (connectToMasterServer()) {
            game.network().networkJoinLobby();
        }
    }

    /**
     * Initialize the settings container
     */
    private void initializeSettingsContainer() {
    }

    /**
     * Show a dialog
     *
     * @param title the title
     * @param error the error
     */
    public void showDialog(String title, String error) {
        final Dialog errorDialog = new Dialog(title, uiSkin);
        errorDialog.text(error);
        errorDialog.button("Ok");
        errorDialog.show(stage);
    }

    @Override
    protected void disposeElements() {
        uiSkin.dispose();
        uiSkinAtlas.dispose();
    }
}
