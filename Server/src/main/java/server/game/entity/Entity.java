package server.game.entity;

import server.game.location.Location;
import server.game.utilities.Disposable;

/**
 * Represents a bare bones entity
 */
public abstract class Entity implements Disposable {

    /**
     * The location of this entity
     */
    protected final Location location = new Location();

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
    public Location location() {
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

    /**
     * Set location
     *
     * @param x x
     * @param y y
     */
    public void setLocation(float x, float y) {
        location.set(x, y);
    }

}
