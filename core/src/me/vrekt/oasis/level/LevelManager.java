package me.vrekt.oasis.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Disposable;
import me.vrekt.oasis.OasisGameAdapter;
import me.vrekt.oasis.level.lobby.PreLobbyLevel;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Manages levels
 */
public final class LevelManager implements Disposable {

    /**
     * Map of all levels in the game
     */
    private final Map<String, Level> levels = new HashMap<>();

    /**
     * The current level
     */
    private Level level;

    /**
     * Creates all the levels and maps them
     */
    public LevelManager() {
        levels.put("PreLobby", new PreLobbyLevel());
    }

    /**
     * Get a level
     *
     * @param level the level name
     * @return the level
     */
    public Level getLevel(String level) {
        return levels.get(level);
    }

    /**
     * Start a new level
     *
     * @param level the level
     */
    public CompletableFuture<Boolean> startLevel(Level level) {
        this.level = level;
        final CompletableFuture<Boolean> result = new CompletableFuture<>();

        Gdx.app.postRunnable(() -> result.complete(level.load()));
        return result;
    }

    /**
     * Show a level
     *
     * @param level the level
     */
    public void showLevel(Level level) {
        Gdx.app.postRunnable(() -> OasisGameAdapter.get().setScreen(level));
    }

    /**
     * End the current level
     */
    public void endCurrentLevel() {
        if (level != null) {
            level.unload();
            level.dispose();
            level = null;
        }
    }

    @Override
    public void dispose() {
        endCurrentLevel();
        levels.clear();
    }
}
