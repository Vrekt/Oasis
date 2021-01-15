package me.vrekt.oasis.ui.menu;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Disableable;
import me.vrekt.oasis.asset.character.CharacterType;
import me.vrekt.oasis.ui.UserInterface;

/**
 * The main menu UI.
 */
public final class MainMenu extends UserInterface {

    /**
     * The task to run after selecting a character.
     */
    private Runnable afterCharacterSelectionTask;

    /**
     * The lobby ID to join
     */
    private int lobbyId;

    /**
     * Creates all the new containers required
     */
    public MainMenu() {
        createContainer("Main");
        createContainer("Create");
        createContainer("JoinInput");
        createContainer("JoinLoad");
        createContainer("Character");
    }

    @Override
    public void show() {
        super.show();

        stage.clear();
        stage.addActor(getContainer("Main"));
    }

    @Override
    protected void initialize() {
        initializeMainContainer();
        initializeCreateLobbyContainer();
        initializeJoinLobbyInputContainer();
        initializeJoinLobbyLoadContainer();
        initializeCharacterSelectorContainer();

        isInitialized = true;
    }

    /**
     * Initialize the main container.
     */
    private void initializeMainContainer() {
        final Table root = getContainer("Main");

        final Label gameLabel = new Label("Oasis", skin);
        final TextField usernameField = new TextField("", skin);
        usernameField.setMessageText("Enter username...");
        usernameField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // set the players username
                game.thePlayer().username(usernameField.getText());
            }
        });

        final TextButton createNewLobbyButton = new TextButton("Create new lobby", skin);
        final TextButton joinLobbyButton = new TextButton("Join existing lobby", skin);
        createNewLobbyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                afterCharacterSelectionTask = () -> showContainerThen("Create", MainMenu.this::handleCreateLobbyContainer);
                showContainer("Character");
            }
        });

        joinLobbyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                afterCharacterSelectionTask = () -> showContainer("JoinInput");
                showContainer("Character");
            }
        });

        root.add(gameLabel).fillX().uniformX();
        root.row();
        root.add(usernameField).fillX().uniformX();
        root.row();
        root.add(createNewLobbyButton).fillX().uniformX();
        root.row();
        root.add(joinLobbyButton).fillX().uniformX();
        stage.addActor(root);
    }

    /**
     * Initialize the create lobby container
     */
    private void initializeCreateLobbyContainer() {
        final Table root = getContainer("Create");
        final Label label = new Label("Creating lobby...", skin);
        root.add(label).center();
    }

    /**
     * Initialize the join lobby container
     */
    private void initializeJoinLobbyInputContainer() {
        final Table root = getContainer("JoinInput");
        final Label label = new Label("Enter lobby invite code", skin);
        final TextField lobbyInviteInput = new TextField("", skin);
        final TextButton joinButton = new TextButton("Join", skin);
        joinButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                try {
                    lobbyId = Integer.parseInt(lobbyInviteInput.getText());
                    showContainerThen("JoinLoad", MainMenu.this::handleJoinLobbyLoadContainer);
                } catch (NumberFormatException exception) {
                    showDialog("Invalid", "Lobby invites can only be numerical");
                }
            }
        });

        root.add(label).fillX().uniformX().padRight(10f);
        root.add(lobbyInviteInput).fillX().uniformX();
        root.row();
        root.add(joinButton).fillX().uniformX();
    }

    /**
     * Initialize the join lobby load container
     */
    private void initializeJoinLobbyLoadContainer() {
        final Table root = getContainer("JoinLoad");
        final Label label = new Label("Joining lobby...", skin);
        root.add(label).center();
    }

    /**
     * Handle the create lobby container
     */
    private void handleCreateLobbyContainer() {
        final Table root = getContainer("Create");
        final Label label = (Label) root.getChild(0);

        // connect + create a fake delay here.
        inFuture(() -> label.setText("Lobby created!"), 1f);
        inFuture(() -> {
            // connect to the server
            if (connect()) {
                game.network().connection().createLobby(game.thePlayer().username(), game.thePlayer().character().ordinal());
            } else {
                showContainer("Main");
                showDialog("Failed to connect", "Could not connect to the server.");
            }
        }, 2.5f);
    }

    /**
     * Handle joining the lobby
     */
    private void handleJoinLobbyLoadContainer() {
        // connect
        inFuture(() -> {
            // connect to the server
            if (connect()) {
                game.network().connection().joinLobby(game.thePlayer().username(), game.thePlayer().character().ordinal(), lobbyId);
            } else {
                showContainer("Main");
                showDialog("Failed to connect", "Could not connect to the server.");
            }
        }, 1.5f);
    }

    /**
     * Initialize the character selector container
     */
    private void initializeCharacterSelectorContainer() {
        final Table root = getContainer("Character");
        final Label label = new Label("Select a character.", skin);
        root.add(label).center().uniformX();
        root.row();

        for (final CharacterType character : CharacterType.values()) {
            // retrieve the walking down idle state as a display
            final TextureRegion region = game.assets().getCharacter(character).get().findRegion("walking_down_idle");
            // create image
            final Label characterNameLabel = new Label(character.name(), skin);
            characterNameLabel.setColor(1f, 1f, 1f, 1f);

            final Image image = new Image(region);
            image.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    // reset other actors colors
                    root.getChildren().forEach(actor -> {
                        if (actor instanceof Label) actor.setColor(1f, 1f, 1f, 1f);
                    });
                    // set the text gray to indicate its been selected
                    characterNameLabel.setColor(.55f, .55f, .55f, 1f);
                    game.thePlayer().character(character);
                }
            });

            root.add(characterNameLabel).uniformX();
            root.add(image).padTop(20f).uniformX();
            root.row();
        }

        final TextButton applyButton = new TextButton("Apply", skin);
        applyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                disableAll(root);
                afterCharacterSelectionTask.run();
                enableAll(root);
            }
        });
        root.add(applyButton);
    }

    /**
     * Disable all elements
     *
     * @param container the container
     */
    private void disableAll(Table container) {
        container.getChildren().forEach(actor -> {
            if (actor instanceof Disableable) ((Disableable) actor).setDisabled(true);
        });
    }

    /**
     * Enable all elements
     *
     * @param container the container
     */
    private void enableAll(Table container) {
        container.getChildren().forEach(actor -> {
            if (actor instanceof Disableable) ((Disableable) actor).setDisabled(false);
        });
    }

    /**
     * Connect
     *
     * @return the result
     */
    private boolean connect() {
        return game.network().connectToServer();
    }

}
