package me.vrekt.oasis.collision;

/**
 * A enum of all collision types in game
 */
public enum CollisionType {

    /**
     * Another player
     */
    PLAYER(0x01),
    /**
     * An invisible wall
     */
    INVISIBLE_WALL(0x02);

    /**
     * Box2D collision bits
     */
    private final int bits;

    CollisionType(int bits) {
        this.bits = bits;
    }

    /**
     * @return the bits
     */
    public int bits() {
        return bits;
    }
}
