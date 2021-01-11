package server.game.entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

/**
 * Represents a bare bones entity
 */
public abstract class Entity implements Disposable {

    /**
     * The location of this entity
     */
    protected final Vector2 location = new Vector2();

    /**
     * The entity name
     */
    protected final String entityName;

    /**
     * The ID of this entity
     */
    protected final int entityId;

    /**
     * Initialize
     *
     * @param entityName name
     * @param entityId   ID
     */
    public Entity(String entityName, int entityId) {
        this.entityName = entityName;
        this.entityId = entityId;
    }

    /**
     * @return the entity name
     */
    public String entityName() {
        return entityName;
    }


    /**
     * @return the entity ID
     */
    public int entityId() {
        return entityId;
    }

    /**
     * Update this entity
     */
    public abstract void update();

    /**
     * @return the entity location
     */
    public Vector2 location() {
        return location;
    }

    /**
     * @return X
     */
    public float x() {
        return location.x;
    }

    /**
     * @return Y
     */
    public float y() {
        return location.y;
    }
}
