package me.vrekt.oasis.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Disposable;
import me.vrekt.oasis.entity.rotation.Rotation;

/**
 * Represents a bare-bones entity.
 */
public abstract class Entity implements Disposable {

    /**
     * The name of this entity
     */
    private String entityName;

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
     * @param entityName the name
     * @param entityId   the ID
     */
    public Entity(String entityName, int entityId) {
        this.entityName = entityName;
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
     * Reset velocity
     */
    protected void resetVelocity() {
        velocity.set(0, 0);
    }

    /**
     * @return get the name of this entity
     */
    public String entityName() {
        return entityName;
    }

    /**
     * Set the name of this entity
     *
     * @param entityName the name
     */
    public void entityName(String entityName) {
        this.entityName = entityName;
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