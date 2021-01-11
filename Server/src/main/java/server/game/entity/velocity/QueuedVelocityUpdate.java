package server.game.entity.velocity;

/**
 * A queued velocity update
 */
public final class QueuedVelocityUpdate {

    /**
     * Entity ID from
     */
    private final int entityId;

    /**
     * Velocity
     */
    private final float velocityX, velocityY;

    /**
     * Rotation index
     */
    private final int rotationIndex;

    /**
     * Initialize
     *
     * @param entityId      the entity from
     * @param velocityX     velocity X
     * @param velocityY     velocity Y
     * @param rotationIndex rotation index
     */
    public QueuedVelocityUpdate(int entityId, float velocityX, float velocityY, int rotationIndex) {
        this.entityId = entityId;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.rotationIndex = rotationIndex;
    }

    /**
     * @return entity ID
     */
    public int entityId() {
        return entityId;
    }

    /**
     * @return x vel
     */
    public float velocityX() {
        return velocityX;
    }

    /**
     * @return y vel
     */
    public float velocityY() {
        return velocityY;
    }

    /**
     * @return the rotation value
     */
    public int rotationIndex() {
        return rotationIndex;
    }

}
