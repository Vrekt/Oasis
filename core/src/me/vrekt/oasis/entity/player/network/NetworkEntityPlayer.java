package me.vrekt.oasis.entity.player.network;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import me.vrekt.oasis.asset.character.CharacterType;
import me.vrekt.oasis.entity.player.EntityPlayer;
import me.vrekt.oasis.entity.rotation.Rotation;

/**
 * A MP entity player
 */
public final class NetworkEntityPlayer extends EntityPlayer {

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
        velocity.set(velocityX, velocityY);
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
        final float dst = currentPosition.dst2(x, y);

        // interpolate to pos if too far away (de sync)
        if (dst >= 10) {
            doPositionInterpolation = true;
            interpolateToX = x;
            interpolateToY = y;
        }
    }

    /**
     * Capture current state
     */
    public void captureState() {
        previousPosition.set(body.getPosition());
    }

    /**
     * Interpolate the position of this player
     *
     * @param alpha alpha
     */
    public void interpolate(float alpha) {
        interpolatedPosition.x = Interpolation.linear.apply(previousPosition.x, currentPosition.x, alpha);
        interpolatedPosition.y = Interpolation.linear.apply(previousPosition.y, currentPosition.y, alpha);
    }

    @Override
    public void update(float delta) {
        if (doPositionInterpolation) {
            interpolatedPosition.x = Interpolation.linear.apply(currentPosition.x, interpolateToX, 0.5f);
            interpolatedPosition.y = Interpolation.linear.apply(currentPosition.y, interpolateToY, 0.5f);
            doPositionInterpolation = false;
            return;
        }

        body.setLinearVelocity(velocity.x, velocity.y);
        currentPosition.set(body.getPosition());
        renderer.update(rotation, !velocity.isZero());
    }

    @Override
    public void render(float delta, SpriteBatch batch) {
        renderer.render(delta, currentPosition, batch);
    }

}
