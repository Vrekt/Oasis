package me.vrekt.oasis;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Disposable;
import io.netty.util.ResourceLeakDetector;
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
     * The main menu
     */
    private final MainMenu mainMenu;

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
     * Initialize the game
     */
    public Oasis(OasisGameAdapter adapter) {
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.PARANOID);
        if (thisInstance != null) throw new UnsupportedOperationException();
        thisInstance = this;

        network = new Network();
        thePlayer = new LocalEntityPlayer();
        levelManager = new LevelManager();
        mainMenu = new MainMenu();

        assets = new GameAssets();
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
        Gdx.app.postRunnable(() -> OasisGameAdapter.get().setScreen(mainMenu));
    }

    /**
     * Show the main menu then execute a action
     *
     * @param after the after
     */
    private void showMainMenuThen(Runnable after) {
        Gdx.app.postRunnable(() -> {
            if (OasisGameAdapter.get().getScreen() != mainMenu) {
                OasisGameAdapter.get().setScreen(mainMenu);
                after.run();
            }
        });
    }

    /**
     * Show an error
     *
     * @param title the title
     * @param error the error
     */
    public void showMainMenuWithError(String title, String error) {
        showMainMenuThen(() -> mainMenu.showDialog(title, error));
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
