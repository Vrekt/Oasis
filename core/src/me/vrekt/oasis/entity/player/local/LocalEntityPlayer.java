package me.vrekt.oasis.entity.player.local;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import me.vrekt.oasis.entity.player.EntityPlayer;
import me.vrekt.oasis.entity.rotation.Rotation;
import me.vrekt.oasis.network.Connection;
import protocol.packet.client.ClientPosition;
import protocol.packet.client.ClientVelocity;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The local player
 */
public final class LocalEntityPlayer extends EntityPlayer {

    /**
     * The ms send rate
     */
    private static final long VELOCITY_SEND_RATE_MS = 100, POSITION_SEND_RATE_MS = 200;

    /**
     * Last packet sends
     */
    private long lastVelocitySend, lastPositionSend;

    /**
     * This players connection
     */
    private Connection connection;

    /**
     * Set of inputs disabled.
     */
    private final List<Integer> inputsDisabled = new CopyOnWriteArrayList<>();

    /**
     * Empty player
     */
    public LocalEntityPlayer() {
        super(-1);
    }

    /**
     * Set the connection
     *
     * @param connection the connection
     */
    public void connection(Connection connection) {
        this.connection = connection;
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
        final float interpolatedVelocityX = velocityX == 0.0f ? 0.0f : Interpolation.linear.apply(previous.x, current.x, delta) * velocityX;
        final float interpolatedVelocityY = velocityY == 0.0f ? 0.0f : Interpolation.linear.apply(previous.y, current.y, delta) * velocityY;

        // update
        controller.update(rotation, hasMoved, false);
        entityBody.setLinearVelocity(interpolatedVelocityX, interpolatedVelocityY);

        updateToNetwork(velocityX, velocityY);
    }

    /**
     * Update networking
     * TODO: Maybe write then flush next tick
     *
     * @param velocityX vel X
     * @param velocityY vel Y
     */
    private void updateToNetwork(float velocityX, float velocityY) {
        final long now = System.currentTimeMillis();
        if (now - lastVelocitySend >= VELOCITY_SEND_RATE_MS) {
            connection.send(new ClientVelocity(velocityX, velocityY, rotation.ordinal()));
            lastVelocitySend = now;
        }

        if (now - lastPositionSend >= POSITION_SEND_RATE_MS) {
            connection.send(new ClientPosition(rotation.ordinal(), current.x, current.y));
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
        for (int key : keys) if (inputsDisabled.contains(key)) inputsDisabled.remove((Integer) key);
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
