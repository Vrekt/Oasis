package me.vrekt.oasis.entity.player.local;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import me.vrekt.oasis.Oasis;
import me.vrekt.oasis.entity.player.EntityPlayer;
import me.vrekt.oasis.entity.rotation.Rotation;
import me.vrekt.oasis.network.NetworkHandler;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * The local player
 */
public final class LocalEntityPlayer extends EntityPlayer {

    /**
     * The ms send rate
     */
    private final long velocitySendRateMs = 50, positionSendRateMs = 300;

    /**
     * Last packet sends
     */
    private long lastVelocitySend, lastPositionSend;

    /**
     * Net handler
     */
    private final NetworkHandler network;

    /**
     * Set of inputs disabled.
     */
    private final List<Integer> inputsDisabled = new CopyOnWriteArrayList<>();

    /**
     * Empty player
     */
    public LocalEntityPlayer() {
        super(null, -1);
        network = Oasis.get().network();
    }

    @Override
    public void resetState() {
        inputsDisabled.clear();
        controller.reset();
        entityBody = null;
    }

    @Override
    public void update(float delta) {
        resetVelocity();

        float velocityX = 0f;
        float velocityY = 0f;

        if (Gdx.input.isKeyPressed(Input.Keys.A)
                && !isInputDisabled(Input.Keys.A)) {
            velocityX = -55f;
            rotation = Rotation.FACING_LEFT;
        } else if (Gdx.input.isKeyPressed(Input.Keys.D)
                && !isInputDisabled(Input.Keys.D)) {
            velocityX = 55f;
            rotation = Rotation.FACING_RIGHT;
        } else if (Gdx.input.isKeyPressed(Input.Keys.W)
                && !isInputDisabled(Input.Keys.W)) {
            velocityY = -55f;
            rotation = Rotation.FACING_UP;
        } else if (Gdx.input.isKeyPressed(Input.Keys.S)
                && !isInputDisabled(Input.Keys.S)) {
            velocityY = 55f;
            rotation = Rotation.FACING_DOWN;
        }

        // update locations for interpolation
        previous = current;
        current = entityBody.getPosition();
        final boolean hasMoved = velocityX != 0.0 || velocityY != 0.0;

        // the interpolated velocity for (hopefully) smooth movement
        // TODO: Some hitches here and there but def better then not having this
        final float interpolatedVelocityX = velocityX == 0.0f ? 0.0f : Interpolation.linear.apply(previous.x, current.x, delta) * velocityX;
        final float interpolatedVelocityY = velocityY == 0.0f ? 0.0f : Interpolation.linear.apply(previous.y, current.y, delta) * velocityY;

        // update
        controller.update(rotation, hasMoved, false);
        entityBody.setLinearVelocity(interpolatedVelocityX, interpolatedVelocityY);

        final long now = System.currentTimeMillis();
        final long fakeLag = ThreadLocalRandom.current().nextInt(150, 350);
        if (now - lastVelocitySend >= velocitySendRateMs + fakeLag) {
            network.networkVelocity(velocityX, velocityY, rotation);
            lastVelocitySend = now;
        }

        if(now - lastPositionSend >= positionSendRateMs + fakeLag) {
            network.networkPosition(current.x, current.y, rotation);
            lastPositionSend = now;
        }
    }

    @Override
    public void render(float delta, SpriteBatch batch) {
        controller.render(delta, rotation, entityBody.getPosition(), batch);
    }

    /**
     * Disable inputs
     *
     * @param keys the keys
     */
    public void disableInputs(int... keys) {
        for (int key : keys) inputsDisabled.add(key);
    }

    /**
     * Enable inputs
     *
     * @param keys the keys
     */
    public void enableInputs(int... keys) {
        for (int key : keys) inputsDisabled.remove(key);
    }

    /**
     * Check if an input is disabled
     *
     * @param key the key
     * @return {@code true} if so
     */
    private boolean isInputDisabled(int key) {
        return inputsDisabled.contains(key);
    }

}
