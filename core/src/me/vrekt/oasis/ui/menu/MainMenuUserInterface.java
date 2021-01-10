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
     * TODO: I suck at UI
     */
    private void initializeMainMenuContainer() {
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        // game name label
        final Label gameNameLabel = new Label("Oasis", uiSkin);
        root.add(gameNameLabel).expandX().left().padLeft(25f).padTop(-400f).padBottom(2f).row();

        // username entry
        final TextField usernameField = new TextField("", uiSkin);
        usernameField.setMessageText("Enter username");
        root.add(usernameField).left().padLeft(25f).padTop(-350f).row();

        // action buttons
        final TextButton createLobbyButton = new TextButton("Create lobby", uiSkin);
        root.add(createLobbyButton).left().padLeft(25f).padTop(-300f).row();
        final TextButton joinLobbyButton = new TextButton("Join lobby", uiSkin);
        root.add(joinLobbyButton).left().padLeft(25f).padTop(-243f).row();
        final TextButton settingsButton = new TextButton("Settings", uiSkin);
        root.add(settingsButton).left().padLeft(25f).padTop((-243) + 57).row();

        createLobbyButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // disable buttons and set local state
                changeButtonState(true, createLobbyButton, joinLobbyButton, settingsButton);
                game.setPlayerUsername(usernameField.getText());

                game
                        .network()
                        .connectToMasterServer()
                        .whenComplete((result, error) -> {
                            if (result) {
                                // request to create a lobby.
                                game.network().networkCreateLobby();
                            } else {
                                error.printStackTrace();
                                showDialog("Failed to connect to server.", error.getMessage());
                                changeButtonState(false, createLobbyButton, joinLobbyButton, settingsButton);
                            }
                        });
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
     * Initialize the settings container
     * TODO
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
