package me.vrekt.oasis.entity.player.network;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.asset.character.CharacterType;
import me.vrekt.oasis.entity.player.EntityPlayer;
import me.vrekt.oasis.entity.rotation.Rotation;

/**
 * A MP entity player
 */
public final class NetworkEntityPlayer extends EntityPlayer {

    /**
     * Velocity
     */
    private float velocityX, velocityY;

    /**
     * If position should be interpolated
     */
    private boolean doPositionInterpolation;

    /**
     * The position to interpolate to.
     */
    private float interpolateToX, interpolateToY;

    /**
     * Initialize
     *
     * @param username  their username
     * @param character character ID
     * @param entityId  ID
     */
    public NetworkEntityPlayer(String username, int character, int entityId) {
        super(entityId);
        this.characterType = CharacterType.values()[character];
        this.username = username;
    }

    /**
     * Update velocity
     *
     * @param velocityX X
     * @param velocityY Y
     * @param rotation  rotation
     */
    public void updateVelocity(float velocityX, float velocityY, Rotation rotation) {
        this.rotation = rotation;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
    }

    /**
     * Update position
     *
     * @param x        x
     * @param y        y
     * @param rotation rotation
     */
    public void updatePosition(float x, float y, Rotation rotation) {
        this.rotation = rotation;
        final float dst = current.dst2(x, y);

        // interpolate to pos if too far away (de sync)
        if (dst >= 10) {
            doPositionInterpolation = true;
            interpolateToX = x;
            interpolateToY = y;
        }
    }

    @Override
    public void update(float delta) {
        // update locations for interpolation
        previous = current;
        current = body.getPosition().cpy();

        if (doPositionInterpolation) {
            final Vector2 to = new Vector2(interpolateToX, interpolateToY);
            current.interpolate(to, delta, Interpolation.linear);
            doPositionInterpolation = false;
            return;
        }

        // the interpolated velocity for (hopefully) smooth movement
        final float interpolatedVelocityX = velocityX == 0.0f ? 0.0f : Interpolation.linear.apply(previous.x, current.x, delta) * velocityX;
        final float interpolatedVelocityY = velocityY == 0.0f ? 0.0f : Interpolation.linear.apply(previous.y, current.y, delta) * velocityY;

        // update
        controller.update(rotation, (velocityX != 0.0f || velocityY != 0.0f), false);
        body.setLinearVelocity(interpolatedVelocityX, interpolatedVelocityY);
    }

    @Override
    public void render(float delta, SpriteBatch batch) {
        controller.render(delta, rotation, current, batch);
    }

}
