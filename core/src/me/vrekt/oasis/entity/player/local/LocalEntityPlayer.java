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
     * The move speed
     */
    private static final float MOVE_SPEED = 100;

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
        pollInput();

        // update locations for interpolation
        previous = current;
        current = body.getPosition();

        // the interpolated velocity for smoother movement
        final float interpolatedVelocityX = velocity.x == 0.0f ? 0.0f : Interpolation.linear.apply(previous.x, current.x, delta) * velocity.x;
        final float interpolatedVelocityY = velocity.y == 0.0f ? 0.0f : Interpolation.linear.apply(previous.y, current.y, delta) * velocity.y;

        // update
        controller.update(rotation, !velocity.isZero(), colliding);
        body.setLinearVelocity(interpolatedVelocityX, interpolatedVelocityY);

        // if colliding, we set our velocity to zero so the network syncs fine
        // if we don't the players will get out of sync because the velocity will still be updated
        // over the network even tho we aren't moving
        if (colliding) velocity.setZero();
        sendVelocityAndPosition();
    }

    /**
     * Poll the input
     */
    private void pollInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.A)
                && !isInputDisabled(Input.Keys.A)) {
            velocity.set(-MOVE_SPEED, 0f);
            rotation = Rotation.FACING_LEFT;
        } else if (Gdx.input.isKeyPressed(Input.Keys.D)
                && !isInputDisabled(Input.Keys.D)) {
            velocity.set(MOVE_SPEED, 0f);
            rotation = Rotation.FACING_RIGHT;
        } else if (Gdx.input.isKeyPressed(Input.Keys.W)
                && !isInputDisabled(Input.Keys.W)) {
            velocity.set(0f, -MOVE_SPEED);
            rotation = Rotation.FACING_UP;
        } else if (Gdx.input.isKeyPressed(Input.Keys.S)
                && !isInputDisabled(Input.Keys.S)) {
            velocity.set(0f, MOVE_SPEED);
            rotation = Rotation.FACING_DOWN;
        } else {
            velocity.set(0f, 0f);
        }
    }

    /**
     * Send velocity and position to the server
     */
    private void sendVelocityAndPosition() {
        final long now = System.currentTimeMillis();
        if (now - lastVelocitySend >= VELOCITY_SEND_RATE_MS) {
            connection.send(new ClientVelocity(velocity.x, velocity.y, rotation.ordinal()));
            lastVelocitySend = now;
        }

        if (now - lastPositionSend >= POSITION_SEND_RATE_MS) {
            connection.send(new ClientPosition(rotation.ordinal(), current.x, current.y));
            lastPositionSend = now;
        }
    }

    @Override
    public void render(float delta, SpriteBatch batch) {
        controller.render(delta, rotation, current, batch);
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
