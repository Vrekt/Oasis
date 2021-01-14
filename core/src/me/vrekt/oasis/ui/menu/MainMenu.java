package me.vrekt.oasis.ui.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import me.vrekt.oasis.Oasis;

/**
 * The game main menu
 */
public final class MainMenu extends ScreenAdapter {

    /**
     * The main stage for this menu
     */
    private final Stage stage = new Stage();

    /**
     * Root table
     */
    private final Table mainMenuContainer = new Table();

    /**
     * Lobby container
     */
    private final Table lobbyContainer = new Table();

    /**
     * If we are initialized
     */
    private boolean initialized;

    @Override
    public void show() {
        Gdx.app.log("MainMenu", "Showing game main menu");
        initialize();
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
        final Dialog dialog = new Dialog(title, Oasis.get().assets().defaultUiSkin());
        dialog.text(message);
        dialog.button("Ok");
        dialog.show(stage);
        stage.setKeyboardFocus(dialog);
    }

    /**
     * Initialize this menu
     */
    private void initialize() {
        stage.clear();
        mainMenuContainer.setFillParent(true);
        lobbyContainer.setFillParent(true);
        stage.addActor(mainMenuContainer);
        Gdx.input.setInputProcessor(stage);

        if (!initialized) {
            final Skin defaultSkin = Oasis.get().assets().defaultUiSkin();

            addGameLabel(defaultSkin);
            addUsernameField(defaultSkin);
            addCreateLobbyButton(defaultSkin);
            addJoinLobbyButton(defaultSkin);
            initializeLobbyContainer(defaultSkin);
            initialized = true;
        }
    }

    /**
     * Add the game name label
     *
     * @param skin skin
     */
    private void addGameLabel(Skin skin) {
        final Label label = new Label("Oasis", skin);
        mainMenuContainer.add(label).center().row();
    }

    /**
     * Add the username field
     *
     * @param skin skin
     */
    private void addUsernameField(Skin skin) {
        final TextField usernameField = new TextField("", skin);
        usernameField.setMessageText("Enter username");
        mainMenuContainer.add(usernameField).center().row();
        usernameField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Oasis.get().thePlayer().username(usernameField.getText());
            }
        });
    }

    /**
     * Create a new lobby
     *
     * @param skin the skin
     */
    private void addCreateLobbyButton(Skin skin) {
        final TextButton button = new TextButton("Create new lobby", skin);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                button.setDisabled(true);
                if (Oasis.get().network().connectToServer()) {
                    Oasis.get().network().connection().createLobby(Oasis.get().thePlayer().username());
                } else {
                    showDialog("Failed to connect", "Failed to connect to the server");
                }
                button.setDisabled(false);
            }
        });
        mainMenuContainer.add(button).center().row();
    }

    /**
     * Add the join lobby button
     *
     * @param skin skin
     */
    private void addJoinLobbyButton(Skin skin) {
        final TextButton button = new TextButton("Join existing lobby", skin);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                button.setDisabled(true);
                if (Oasis.get().network().connectToServer()) {
                    stage.clear();
                    stage.addActor(lobbyContainer);
                } else {
                    showDialog("Failed to connect", "Failed to connect to the server");
                }
                button.setDisabled(false);
            }
        });
        mainMenuContainer.add(button).center().row();
    }

    /**
     * Initialize the lobby container
     *
     * @param skin the skin
     */
    private void initializeLobbyContainer(Skin skin) {
        final TextField field = new TextField("", skin);
        final TextButton backButton = new TextButton("Back", skin);
        final TextButton joinButton = new TextButton("Join", skin);
        field.setMessageText("Enter lobby invite code");

        lobbyContainer.add(field).center().row();
        lobbyContainer.add(joinButton).center().row();
        lobbyContainer.add(backButton).center().row();

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stage.clear();
                stage.addActor(mainMenuContainer);
            }
        });

        joinButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                try {
                    final int lobbyId = Integer.parseInt(field.getText());
                    Oasis.get().network().connection().joinLobby(Oasis.get().thePlayer().username(), lobbyId);
                } catch (Exception any) {
                    showDialog("Error", "You must enter a valid number.");
                }
            }
        });
    }

}
