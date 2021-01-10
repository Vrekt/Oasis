package me.vrekt.oasis.entity.player.local;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import me.vrekt.oasis.entity.player.EntityPlayer;
import me.vrekt.oasis.entity.rotation.Rotation;

/**
 * The local player
 */
public final class LocalEntityPlayer extends EntityPlayer {

    /**
     * Empty player
     */
    public LocalEntityPlayer() {
        super(null, -1);
    }

    @Override
    public void update(float delta) {
        resetVelocity();

        float velocity = 0f;

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            velocity = -55f;
            rotation = Rotation.FACING_LEFT;
        } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            velocity = 55f;
            rotation = Rotation.FACING_RIGHT;
        }

        // update
        previous = current;
        current = entityBody.getPosition();

        // the interpolated velocity for (hopefully) smooth movement
        // TODO: Some hitches here and there but def better then not having this
        final float interpolatedVelocity = Interpolation.linear.apply(previous.x, previous.x, delta) * velocity;

        // update
        controller.update(rotation, velocity != 0f, false);
        entityBody.setLinearVelocity(interpolatedVelocity, 0f);
    }

    @Override
    public void render(float delta, SpriteBatch batch) {
        controller.render(delta, rotation, entityBody.getPosition(), batch);
    }
}
