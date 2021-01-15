package me.vrekt.oasis.level.world;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import me.vrekt.oasis.Oasis;
import me.vrekt.oasis.collision.CollisionHandler;
import me.vrekt.oasis.collision.CollisionObject;
import me.vrekt.oasis.collision.CollisionType;
import me.vrekt.oasis.entity.player.local.LocalEntityPlayer;
import me.vrekt.oasis.entity.player.network.NetworkEntityPlayer;
import me.vrekt.oasis.entity.rotation.Rotation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles the world within the level
 */
public final class LevelWorld implements Disposable {

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
     * The spawn location for players
     */
    private final Vector2 spawn;

    /**
     * Accumulator
     */
    private float worldStepAccumulator;

    /**
     * Creates a new world for a level
     *
     * @param map the map of the level
     */
    public LevelWorld(TiledMap map, float mapScale) {
        world = new World(new Vector2(0, 0), true);
        world.setContactListener(new CollisionHandler());
        thePlayer = Oasis.get().thePlayer();

        spawn = retrieveSpawnArea(map, mapScale);
        initializeLocalPlayerIn();
        initializeCollision(map);
    }

    /**
     * @return the box 2d world.
     */
    public World box2dWorld() {
        return world;
    }

    /**
     * @return the spawn area
     */
    public Vector2 spawn() {
        return spawn;
    }

    /**
     * Initialize the local player
     */
    private void initializeLocalPlayerIn() {
        thePlayer.createPlayerAnimations();
        thePlayer.spawnEntityInWorld(this, spawn.x, spawn.y);
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
        player.createPlayerAnimations();
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
     * Initialize collision for this world
     *
     * @param map the map
     */
    private void initializeCollision(TiledMap map) {
        map.getLayers()
                .get("Collision")
                .getObjects()
                .getByType(RectangleMapObject.class)
                .forEach(rect -> {
                    // create a static body for this object
                    final BodyDef bodyDef = new BodyDef();
                    bodyDef.type = BodyDef.BodyType.StaticBody;
                    // scale by *2 for the scaled world size
                    bodyDef.position.set(new Vector2(rect.getRectangle().x * 2, rect.getRectangle().y * 2));
                    bodyDef.fixedRotation = true;

                    // create a basic poly, that *roughly* matches the shape
                    // TODO: The shapes from tiled don't match up :/
                    final Body body = world.createBody(bodyDef);
                    final PolygonShape shape = new PolygonShape();
                    shape.setAsBox(rect.getRectangle().width, rect.getRectangle().height);

                    // finally, create the fixture and set the type as a collision object
                    body.createFixture(shape, 1.0f).setUserData(new CollisionObject(CollisionType.INVISIBLE_WALL));
                    shape.dispose();
                });
    }

    /**
     * Retrieve the spawn area
     *
     * @param map      the map
     * @param mapScale the scale of the map
     * @return the spawn vector
     */
    private Vector2 retrieveSpawnArea(TiledMap map, float mapScale) {
        // retrieve the spawn trigger for the map
        final RectangleMapObject spawnTrigger = (RectangleMapObject) map.getLayers()
                .get("Triggers")
                .getObjects()
                .get("Spawn");
        return new Vector2(spawnTrigger.getRectangle().x * mapScale, spawnTrigger.getRectangle().y * mapScale);
    }

    /**
     * Update the world
     *
     * @param delta the delta
     */
    public void update(float delta) {
        final float capped = Math.min(delta, MAX_FRAME_TIME);
        worldStepAccumulator += capped;

        while (worldStepAccumulator >= DEFAULT_STEP) {
            // update local player
            thePlayer.update(delta);

            // update networked players
            players.forEach((id, player) -> player.update(delta));

            // step simulation
            world.step(DEFAULT_STEP, 6, 3);
            worldStepAccumulator -= DEFAULT_STEP;
        }
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
