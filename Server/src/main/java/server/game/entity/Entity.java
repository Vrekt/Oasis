package server.game.entity;

import server.game.location.Location;

/**
 * Represents a bare bone entity
 */
public abstract class Entity {

    /**
     * Location of this entity
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
     * @return the location
     */
    public Location location() {
        return location;
    }

    /**
     * @return the entity ID
     */
    public int entityId() {
        return entityId;
    }

    /**
     * @return X
     */
    public float x() {
        return location.x();
    }

    /**
     * @return Y
     */
    public float y() {
        return location.y();
    }

    /**
     * Update this entity
     */
    public abstract void update();

}
