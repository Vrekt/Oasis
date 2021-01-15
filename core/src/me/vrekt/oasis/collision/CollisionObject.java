package me.vrekt.oasis.collision;

/**
 * Represents an object you can collide with
 */
public final class CollisionObject {

    /**
     * The collision type
     */
    private final CollisionType collisionType;

    /**
     * Initialize
     *
     * @param collisionType the collision type
     */
    public CollisionObject(CollisionType collisionType) {
        this.collisionType = collisionType;
    }

    /**
     * @return the type
     */
    public CollisionType collisionType() {
        return collisionType;
    }
}
