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
    protected Body entityBody;

    /**
     * The velocity of this entity
     */
    protected final Vector2 velocity = new Vector2(0, 0);

    /**
     * Initialize this entity
     *
     * @param entityId   the ID
     */
    public Entity(int entityId) {
        this.entityId = entityId;
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
     * @param at    at
     */
    public abstract void spawnInWorld(LevelWorld world, Vector2 at);

    /**
     * Reset velocity
     */
    protected void resetVelocity() {
        velocity.set(0, 0);
    }

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
        return entityBody.getPosition().x;
    }

    /**
     * @return Y position
     */
    public float y() {
        return entityBody.getPosition().y;
    }

    /**
     * @return the body
     */
    public Body entityBody() {
        return entityBody;
    }

    /**
     * @return the entity rotation
     */
    public Rotation rotation() {
        return rotation;
    }

    /**
     * Set the rotation
     *
     * @param rotation the rotation
     */
    public void rotation(Rotation rotation) {
        this.rotation = rotation;
    }
}