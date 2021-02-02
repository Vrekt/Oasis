package me.vrekt.oasis.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Disposable;
import me.vrekt.oasis.Oasis;
import me.vrekt.oasis.entity.player.local.LocalEntityPlayer;
import me.vrekt.oasis.level.load.LoadingProgressCallback;
import me.vrekt.oasis.level.world.LevelWorld;
import me.vrekt.oasis.ui.types.InGameUserInterface;

/**
 * Represents a level.
 */
public abstract class Level extends InGameUserInterface implements Disposable {

    /**
     * The name of the level
     */
    protected final String levelName;

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
        thePlayer = Oasis.get().thePlayer();
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
     * @param progress the progress callback
     * @return the result of loading.
     */
    public abstract boolean load(LoadingProgressCallback progress);

    /**
     * Unload the level
     */
    public abstract void unload();

    /**
     * Load internal
     *
     * @param map     the map name
     * @param scaling the scaling
     */
    protected void loadInternal(String map, float scaling) throws Exception {
        batch = new SpriteBatch();
        tiledMap = new TmxMapLoader().load(map);

        renderer = new OrthogonalTiledMapRenderer(tiledMap, scaling, batch);
        world = new LevelWorld(tiledMap, scaling);
    }

    /**
     * Log a message
     *
     * @param message message
     */
    protected void log(String message) {
        Gdx.app.log(levelName, message);
    }

    /**
     * Log a message
     *
     * @param message message
     * @param e       e
     */
    protected void log(String message, Exception e) {
        Gdx.app.log(levelName, message, e);
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

        super.dispose();
    }
}
