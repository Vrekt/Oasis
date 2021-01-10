package me.vrekt.oasis.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Disposable;
import me.vrekt.oasis.OasisGameAdapter;
import me.vrekt.oasis.level.lobby.PreLobbyLevel;

/**
 * Manages levels.
 */
public final class LevelManager implements Disposable {

    /**
     * The lobby level.
     */
    private final PreLobbyLevel preLobbyLevel;

    /**
     * The main level screen.
     */
    private final LocalLevelScreenAdapter screen;

    /**
     * The current level that is being shown.
     */
    private Level currentLevel;

    /**
     * Initialize and create all the levels.
     */
    public LevelManager() {
        preLobbyLevel = new PreLobbyLevel();
        screen = new LocalLevelScreenAdapter();
    }

    /**
     * Start the pre-lobby level.
     */
    public void startPreLobbyLevel() {
        currentLevel = preLobbyLevel;
        preLobbyLevel.loadLevel();
        show();
    }

    /**
     * Show the level
     */
    public void show() {
        Gdx.app.postRunnable(() -> OasisGameAdapter.get().setScreen(screen));
    }

    /**
     * The local screen adapter for rendering and updating levels.
     */
    private final class LocalLevelScreenAdapter extends ScreenAdapter {

        @Override
        public void show() {
            Gdx.app.log("LevelScreenAdapter", "Showing level screen adapter.");
        }

        @Override
        public void render(float delta) {
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            currentLevel.render(delta);
            currentLevel.update(delta);
        }

        @Override
        public void dispose() {
            currentLevel.dispose();
        }
    }

    @Override
    public void dispose() {
        preLobbyLevel.dispose();
    }
}
