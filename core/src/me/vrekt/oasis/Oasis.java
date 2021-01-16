package me.vrekt.oasis;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.utils.Disposable;
import me.vrekt.oasis.asset.GameAssets;
import me.vrekt.oasis.entity.player.local.LocalEntityPlayer;
import me.vrekt.oasis.level.LevelManager;
import me.vrekt.oasis.network.Connection;
import me.vrekt.oasis.network.Network;
import me.vrekt.oasis.ui.menu.MainMenu;

/**
 * Handles the game.
 */
public final class Oasis implements Disposable {

    /**
     * Singleton
     */
    private static Oasis thisInstance;

    /**
     * The game version
     */
    public static final int GAME_VERSION = 10;

    /**
     * The adapter
     */
    private final OasisGameAdapter adapter;

    /**
     * The network handler.
     */
    private final Network network;

    /**
     * The player
     */
    private final LocalEntityPlayer thePlayer;

    /**
     * Level manager
     */
    private final LevelManager levelManager;

    /**
     * Asset manager
     */
    private final GameAssets assets;

    /**
     * Main menu
     */
    private final MainMenu mainMenu;

    /**
     * Initialize the game
     */
    public Oasis(OasisGameAdapter adapter) {
        if (thisInstance != null) throw new UnsupportedOperationException();
        thisInstance = this;
        this.adapter = adapter;

        assets = new GameAssets();

        network = new Network();
        thePlayer = new LocalEntityPlayer();
        levelManager = new LevelManager();
        mainMenu = new MainMenu();
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
    public Network network() {
        return network;
    }

    /**
     * @return the network connection
     */
    public Connection connection() {
        return network.connection();
    }

    /**
     * @return the local player
     */
    public LocalEntityPlayer thePlayer() {
        return thePlayer;
    }

    /**
     * @return the level manager
     */
    public LevelManager level() {
        return levelManager;
    }

    /**
     * @return the assets
     */
    public GameAssets assets() {
        return assets;
    }

    /**
     * Show the main menu
     */
    public void showMainMenu() {
        Gdx.app.postRunnable(() -> adapter.setScreen(mainMenu));
    }

    /**
     * Show the main menu with an error
     *
     * @param title   the title
     * @param message the message
     */
    public void showMainMenuWithError(String title, String message) {
        Gdx.app.postRunnable(() -> {
            showMainMenu();
            mainMenu.showDialog(title, message);
        });
    }

    /**
     * Show a screen
     *
     * @param screen the screen
     */
    public void show(ScreenAdapter screen) {
        Gdx.app.postRunnable(() -> OasisGameAdapter.get().setScreen(screen));
    }

    /**
     * Show a screen
     *
     * @param adapter the adapter
     */
    public void showSync(ScreenAdapter adapter) {
        OasisGameAdapter.get().setScreen(adapter);
    }

    @Override
    public void dispose() {
        mainMenu.dispose();
        network.dispose();
        thePlayer.dispose();
        assets.dispose();
        levelManager.dispose();
    }
}
