package me.vrekt.oasis.level;

import com.badlogic.gdx.utils.Disposable;
import me.vrekt.oasis.level.lobby.PreLobbyLevel;

import java.util.HashMap;
import java.util.Map;

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
