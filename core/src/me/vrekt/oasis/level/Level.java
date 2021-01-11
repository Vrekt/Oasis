package me.vrekt.oasis.level;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Disposable;
import me.vrekt.oasis.Oasis;
import me.vrekt.oasis.entity.player.local.LocalEntityPlayer;
import me.vrekt.oasis.level.world.LevelWorld;

/**
 * Represents a level.
 */
public abstract class Level extends ScreenAdapter implements Disposable {

    /**
     * The name of the level
     */
    protected final String levelName;

    /**
     * The game instance
     */
    protected final Oasis game;

    /**
     * The camera for this level.
     */
    protected OrthographicCamera camera;

    /**
     * The map for this level.
     */
    protected TiledMap tiledMap;

    /**
     * The renderer for this level.
     */
    protected OrthogonalTiledMapRenderer renderer;

    /**
     * The batch
     */
    protected SpriteBatch batch;

    /**
     * The world for this level
     */
    protected LevelWorld world;

    /**
     * If this level is loaded
     */
    protected boolean loaded;

    /**
     * The player
     */
    protected final LocalEntityPlayer thePlayer;

    /**
     * Initialize the new level
     *
     * @param levelName the level name
     */
    public Level(String levelName) {
        this.levelName = levelName;
        game = Oasis.get();
        thePlayer = game.thePlayer();
    }

    /**
     * @return the name of this level.
     */
    public String levelName() {
        return levelName;
    }

    /**
     * @return the world
     */
    public LevelWorld world() {
        return world;
    }

    /**
     * Load the level
     *
     * @return the result of loading.
     */
    public abstract boolean load();

    /**
     * Unload the level
     */
    public abstract void unload();

    /**
     * @return if the level is loaded
     */
    public boolean loaded() {
        return loaded;
    }

    @Override
    public void dispose() {
        if (world != null) world.dispose();
        if (renderer != null) renderer.dispose();
        if (tiledMap != null) tiledMap.dispose();
        if (batch != null) batch.dispose();
        camera = null;
        loaded = false;
        world = null;
        renderer = null;
        tiledMap = null;
        batch = null;
    }
}
