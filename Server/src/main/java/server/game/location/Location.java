package server.game.location;

/**
 * Represents a basic location
 */
public final class Location {

    /**
     * The rotation
     */
    private final Rotation rotation = new Rotation();

    /**
     * The X and Y
     */
    private float x = 0, y = 0;

    /**
     * Set this location
     *
     * @param x X
     * @param y Y
     */
    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Set this location to another
     *
     * @param other the other
     */
    public void set(Location other) {
        this.x = other.x;
        this.y = other.y;
    }

    /**
     * @return x
     */
    public float x() {
        return x;
    }

    /**
     * @return y
     */
    public float y() {
        return y;
    }
}
