package me.vrekt.oasis.level;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import me.vrekt.oasis.Oasis;

/**
 * Represents a basic level.
 */
public abstract class Level implements Disposable {

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
    protected TiledMap levelMap;

    /**
     * The renderer for this level.
     */
    protected OrthogonalTiledMapRenderer renderer;

    /**
     * The batch
     */
    protected SpriteBatch batch;

    /**
     * The world for this level.
     */
    protected World levelWorld;

    /**
     * The spawn for the level.
     */
    protected Vector2 spawn;

    /**
     * Step accumulator
     */
    protected float worldStepAccumulator;

    /**
     * Initialize
     *
     * @param levelName the name of the level.
     */
    public Level(String levelName) {
        this.levelName = levelName;
        game = Oasis.get();
    }

    /**
     * @return the level name
     */
    public String levelName() {
        return levelName;
    }

    /**
     * Load the level
     */
    public abstract boolean loadLevel();

    /**
     * Update this level
     *
     * @param delta the delta
     */
    public abstract void update(float delta);

    /**
     * Render this level
     *
     * @param delta the delta
     */
    public abstract void render(float delta);

    @Override
    public void dispose() {
        if (renderer != null) renderer.dispose();
        if (levelMap != null) levelMap.dispose();
        if (batch != null) batch.dispose();
    }
}
