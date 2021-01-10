package me.vrekt.oasis;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Disposable;
import me.vrekt.oasis.entity.player.PlayerController;
import me.vrekt.oasis.entity.player.local.LocalEntityPlayer;
import me.vrekt.oasis.level.LevelManager;
import me.vrekt.oasis.network.NetworkHandler;
import me.vrekt.oasis.screen.MainMenuScreen;

/**
 * Handles the game.
 */
public final class Oasis implements Disposable {

    /**
     * Singleton
     */
    private static Oasis thisInstance;

    /**
     * The tag.
     */
    public static final String TAG = "Oasis";

    /**
     * The game version
     */
    public static final int GAME_VERSION = 10;

    /**
     * The main menu
     */
    private final MainMenuScreen mainMenu;

    /**
     * The network handler.
     */
    private final NetworkHandler networkHandler;

    /**
     * The level manager
     */
    private final LevelManager levelManager;

    /**
     * The player
     */
    private final LocalEntityPlayer thePlayer;

    /**
     * The default controller for any player
     */
    private final PlayerController controller;

    /**
     * Initialize the game
     */
    public Oasis(OasisGameAdapter adapter) {
        if (thisInstance != null) throw new UnsupportedOperationException();
        thisInstance = this;

        thePlayer = new LocalEntityPlayer();

        mainMenu = new MainMenuScreen();
        networkHandler = new NetworkHandler();
        levelManager = new LevelManager();
        controller = new PlayerController();

        adapter.setScreen(mainMenu);
    }

    /**
     * @return the instance
     */
    public static Oasis get() {
        return thisInstance;
    }

    /**
     * @return the network.
     */
    public NetworkHandler network() {
        return networkHandler;
    }

    /**
     * @return the level manager
     */
    public LevelManager levels() {
        return levelManager;
    }

    /**
     * @return the local player
     */
    public LocalEntityPlayer thePlayer() {
        return thePlayer;
    }

    /**
     * @return the controller
     */
    public PlayerController controller() {
        return controller;
    }

    /**
     * Set the player username
     *
     * @param username the username
     */
    public void setPlayerUsername(String username) {
        thePlayer.entityName(username);
    }

    /**
     * Network the local player
     *
     * @param entityId the network entity ID.
     */
    public void networkLocalPlayer(int entityId) {
        thePlayer.entityId(entityId);
    }

    /**
     * Load into a local pre-lobby.
     * This lobby can be networked.
     */
    public void loadIntoLocalLobby() {
        Gdx.app.postRunnable(levelManager::startPreLobbyLevel);
    }

    /**
     * Show an error dialog
     *
     * @param title the title
     * @param error the error message
     */
    public void showErrorDialog(String title, String error) {
        // TODO:
    }

    @Override
    public void dispose() {
        mainMenu.dispose();
        networkHandler.dispose();
        levelManager.dispose();
        thePlayer.dispose();
        controller.dispose();
    }
}
