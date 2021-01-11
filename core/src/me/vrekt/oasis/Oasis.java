package me.vrekt.oasis;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Disposable;
import me.vrekt.oasis.entity.player.local.LocalEntityPlayer;
import me.vrekt.oasis.level.Level;
import me.vrekt.oasis.level.lobby.PreLobbyLevel;
import me.vrekt.oasis.level.world.LevelWorld;
import me.vrekt.oasis.network.NetworkHandler;
import me.vrekt.oasis.screen.MainMenuScreen;

import java.util.HashMap;
import java.util.Map;

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
    private final MainMenuScreen mainMenu;

    /**
     * The network handler.
     */
    private final NetworkHandler networkHandler;

    /**
     * The player
     */
    private final LocalEntityPlayer thePlayer;

    /**
     * Asset manager
     */
    private final AssetManager assetManager;

    /**
     * Levels
     */
    private final Map<String, Level> levels = new HashMap<>();

    /**
     * The current level.
     */
    private Level level;

    /**
     * Initialize the game
     */
    public Oasis(OasisGameAdapter adapter) {
        if (thisInstance != null) throw new UnsupportedOperationException();
        thisInstance = this;

        networkHandler = new NetworkHandler();
        thePlayer = new LocalEntityPlayer();
        mainMenu = new MainMenuScreen();

        assetManager = new AssetManager();
        assetManager.load("player/Character.atlas", TextureAtlas.class);
        assetManager.finishLoading();

        initializeLevels();

        adapter.setScreen(mainMenu);
    }

    /**
     * Initialize levels
     */
    private void initializeLevels() {
        levels.put("PreLobby", new PreLobbyLevel());
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
     * @return the local player
     */
    public LocalEntityPlayer thePlayer() {
        return thePlayer;
    }

    /**
     * @return the assets
     */
    public AssetManager assets() {
        return assetManager;
    }

    /**
     * Start a level
     */
    public void startLevel(String levelName) {
        level = levels.get(levelName);
        Gdx.app.postRunnable(() -> OasisGameAdapter.get().setScreen(level));
    }

    /**
     * End the level
     */
    public void endLevel() {
        showMainMenuAsync();
        level.unload();
        level.dispose();
        level = null;
    }

    /**
     * Show main menu
     */
    public void showMainMenuSync() {
        OasisGameAdapter.get().setScreen(mainMenu);
    }

    /**
     * Show main menu from another thread
     */
    public void showMainMenuAsync() {
        Gdx.app.postRunnable(this::showMainMenuSync);
    }

    /**
     * @return if we have a world.
     */
    public boolean hasWorld() {
        return level != null && level.world() != null;
    }

    /**
     * The current world we are in
     *
     * @return the world
     */
    public LevelWorld world() {
        return level.world();
    }

    /**
     * Show a dialog
     * Most commonly for errors
     *
     * @param title   the title
     * @param message the message
     */
    public void showDialog(String title, String message) {
        mainMenu.showDialog(title, message);
    }

    @Override
    public void dispose() {
        mainMenu.dispose();
        networkHandler.dispose();
        thePlayer.dispose();
        assetManager.dispose();
        if (level != null)
            level.dispose();
    }
}
