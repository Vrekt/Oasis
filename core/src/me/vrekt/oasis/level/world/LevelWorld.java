package me.vrekt.oasis.level.world;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Disposable;
import me.vrekt.oasis.Oasis;
import me.vrekt.oasis.collision.CollisionContactHandler;
import me.vrekt.oasis.entity.player.local.LocalEntityPlayer;
import me.vrekt.oasis.entity.player.network.NetworkEntityPlayer;
import me.vrekt.oasis.entity.rotation.Rotation;
import me.vrekt.oasis.utilities.CollisionShapeCreator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles the world within the level
 */
public final class LevelWorld implements Disposable {

    /**
     * Static body
     */
    private static final BodyDef STATIC_BODY = new BodyDef();

    static {
        STATIC_BODY.type = BodyDef.BodyType.StaticBody;
    }

    /**
     * Default world step
     */
    private static final float DEFAULT_STEP = 1.0f / 60.0f;

    /**
     * The max frame time allowed
     */
    private static final float MAX_FRAME_TIME = 0.25f;

    /**
     * List of players for this world
     */
    private final Map<Integer, NetworkEntityPlayer> players = new ConcurrentHashMap<>();

    /**
     * The local player
     */
    private final LocalEntityPlayer thePlayer;

    /**
     * The world
     */
    private final World world;

    /**
     * Spawn area
     */
    private final Vector2 spawn;

    /**
     * Accumulator
     */
    private float accumulator;

    /**
     * Creates a new world for a level
     *
     * @param map the map of the level
     */
    public LevelWorld(TiledMap map, float scale) {
        world = new World(new Vector2(0, 0), false);
        world.setContactListener(new CollisionContactHandler());

        thePlayer = Oasis.get().thePlayer();
        spawn = loadSpawn(map, scale);
        loadCollision(map, scale);
        initializeLocalPlayerIn(spawn);
    }

    /**
     * @return the box 2d world.
     */
    public World box2dWorld() {
        return world;
    }

    /**
     * Initialize the local player
     */
    private void initializeLocalPlayerIn(Vector2 spawn) {
        thePlayer.createPlayerRenderer();
        thePlayer.spawnEntityInWorld(this, spawn.x, spawn.y);
    }

    /**
     * Load the spawning area
     *
     * @param map   the map
     * @param scale the scale
     * @return the spawn area
     */
    private Vector2 loadSpawn(TiledMap map, float scale) {
        final RectangleMapObject spawnArea = (RectangleMapObject) map.getLayers()
                .get("Triggers")
                .getObjects()
                .get("Spawn");

        final Vector2 location = new Vector2(spawnArea.getRectangle().x, spawnArea.getRectangle().y);
        return location.scl(scale);
    }

    /**
     * Load collision for this world
     *
     * @param map   the map
     * @param scale the scale
     */
    private void loadCollision(TiledMap map, float scale) {
        map.getLayers()
                .get("Collision")
                .getObjects()
                .forEach((object -> {
                    if (object instanceof PolylineMapObject) {
                        final ChainShape shape = CollisionShapeCreator.createPolylineShape((PolylineMapObject) object, scale);
                        createStaticBody(shape);
                    } else if (object instanceof PolygonMapObject) {
                        final PolygonShape shape = CollisionShapeCreator.createPolygonShape((PolygonMapObject) object, scale);
                        createStaticBody(shape);
                    } else if (object instanceof RectangleMapObject) {
                        final PolygonShape shape = CollisionShapeCreator.createPolygonShape((RectangleMapObject) object, scale);
                        createStaticBody(shape);
                    }
                }));
    }

    /**
     * Create a static body from shape
     *
     * @param shape the shape
     */
    private void createStaticBody(Shape shape) {
        world.createBody(STATIC_BODY).createFixture(shape, 1.0f);
        shape.dispose();
    }

    /**
     * @return the spawn vec
     */
    public Vector2 spawn() {
        return spawn;
    }

    /**
     * Spawn a player in this world
     *
     * @param player the player
     */
    public void spawnPlayer(NetworkEntityPlayer player) {
        createPlayer(player, spawn.x, spawn.y);
    }

    /**
     * Create a player in the world
     *
     * @param player the player
     * @param x      their current X location
     * @param y      their current Y location
     */
    public void createPlayer(NetworkEntityPlayer player, float x, float y) {
        players.put(player.entityId(), player);
        player.createPlayerRenderer();
        player.spawnEntityInWorld(this, x, y);
    }

    /**
     * Remove a player from this world
     *
     * @param entityId the entity ID
     */
    public void removePlayer(int entityId) {
        final NetworkEntityPlayer player = players.remove(entityId);
        if (player != null) world.destroyBody(player.body());
    }

    /**
     * Update a players velocity
     *
     * @param entityId  entity ID
     * @param velocityX velocity X
     * @param velocityY velocity Y
     * @param rotation  the rotation
     */
    public void updatePlayerVelocity(int entityId, float velocityX, float velocityY, int rotation) {
        final NetworkEntityPlayer player = players.get(entityId);
        if (player != null) {
            player.updateVelocity(velocityX, velocityY, Rotation.values()[rotation]);
        }
    }

    /**
     * Update a players position
     *
     * @param entityId the entity ID
     * @param x        x
     * @param y        y
     * @param rotation rotation
     */
    public void updatePlayerPosition(int entityId, float x, float y, int rotation) {
        final NetworkEntityPlayer player = players.get(entityId);
        if (player != null) {
            player.updatePosition(x, y, Rotation.values()[rotation]);
        }
    }

    /**
     * Update the world
     *
     * @param d delta time
     */
    public void update(float d) {
        final float delta = Math.min(d, MAX_FRAME_TIME);
        accumulator += delta;

        players.forEach((id, player) -> player.captureState());
        while (accumulator >= DEFAULT_STEP) {
            thePlayer.captureState();

            world.step(DEFAULT_STEP, 8, 3);
            accumulator -= DEFAULT_STEP;
        }

        thePlayer.update(delta);
        thePlayer.interpolate();

        players.forEach((id, player) -> {
            player.update(delta);
            player.interpolate();
        });
    }

    /**
     * Render the world
     *
     * @param delta the delta
     * @param batch the batch
     */
    public void render(float delta, SpriteBatch batch) {
        thePlayer.render(delta, batch);
        players.forEach((id, player) -> player.render(delta, batch));
    }

    @Override
    public void dispose() {
        players.clear();
        world.dispose();
    }
}
