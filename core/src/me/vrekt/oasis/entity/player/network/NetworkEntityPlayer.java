package me.vrekt.oasis.entity.player.network;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
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
     * Initialize
     *
     * @param entityName name
     * @param entityId   ID
     */
    public NetworkEntityPlayer(String entityName, int entityId) {
        super(entityName, entityId);
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

    @Override
    public void resetState() {
        controller.reset();
        entityBody = null;
    }

    @Override
    public void update(float delta) {
        // update locations for interpolation
        previous = current;
        current = entityBody.getPosition();

        // the interpolated velocity for (hopefully) smooth movement
        final float interpolatedVelocityX = velocityX == 0.0f ? 0.0f : Interpolation.linear.apply(previous.x, current.x, delta) * velocityX;
        final float interpolatedVelocityY = velocityY == 0.0f ? 0.0f : Interpolation.linear.apply(previous.y, current.y, delta) * velocityY;

        // update
        controller.update(rotation, (velocityX != 0.0f || velocityY != 0.0f), false);
        entityBody.setLinearVelocity(interpolatedVelocityX, interpolatedVelocityY);
    }

    @Override
    public void render(float delta, SpriteBatch batch) {
        controller.render(delta, rotation, entityBody.getPosition(), batch);
    }
}
