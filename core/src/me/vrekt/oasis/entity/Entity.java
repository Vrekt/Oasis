package me.vrekt.oasis.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Disposable;
import me.vrekt.oasis.entity.rotation.Rotation;
import me.vrekt.oasis.level.world.LevelWorld;

/**
 * Represents a bare-bones entity.
 */
public abstract class Entity implements Disposable {

    /**
     * The ID of this entity.
     */
    private int entityId;

    /**
     * The rotation of this entity
     */
    protected Rotation rotation = Rotation.FACING_RIGHT;

    /**
     * The Box2d body for this entity.
     */
    protected Body body;

    /**
     * The previous position
     * The current position
     * The interpolated position
     * The velocity
     */
    protected final Vector2 previousPosition, currentPosition, interpolatedPosition, velocity;

    /**
     * Initialize this entity
     *
     * @param entityId the ID
     */
    public Entity(int entityId) {
        this.entityId = entityId;

        previousPosition = new Vector2();
        currentPosition = new Vector2();
        interpolatedPosition = new Vector2();
        velocity = new Vector2();
    }

    /**
     * Update this entity
     *
     * @param delta the delta
     */
    public abstract void update(float delta);

    /**
     * Render this entity
     *
     * @param delta the delta
     * @param batch batching
     */
    public abstract void render(float delta, SpriteBatch batch);

    /**
     * Spawn this entity in the world
     *
     * @param world the world
     * @param x     X
     * @param y     Y
     */
    public abstract void spawnEntityInWorld(LevelWorld world, float x, float y);

    /**
     * @return the entity ID
     */
    public int entityId() {
        return entityId;
    }

    /**
     * Set the entity ID
     *
     * @param entityId ID
     */
    public void entityId(int entityId) {
        this.entityId = entityId;
    }

    /**
     * @return X position
     */
    public float x() {
        return interpolatedPosition.x;
    }

    public Vector2 currentPosition() {
        return currentPosition;
    }

    /**
     * @return Y position
     */
    public float y() {
        return interpolatedPosition.y;
    }

    /**
     * @return the body
     */
    public Body body() {
        return body;
    }
}