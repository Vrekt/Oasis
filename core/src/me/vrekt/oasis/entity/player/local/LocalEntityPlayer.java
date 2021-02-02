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
    private static final float MOVE_SPEED = 2.5f;

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
     * @return the connection
     */
    public Connection connection() {
        return connection;
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
        body.setLinearVelocity(velocity.x, velocity.y);

        currentPosition.set(body.getPosition().x, body.getPosition().y);
        renderer.update(rotation, !velocity.isZero());
        sendVelocityAndPosition();
    }

    /**
     * Interpolate the position of this player
     */
    public void interpolate() {
        interpolatedPosition.x = Interpolation.linear.apply(previousPosition.x, currentPosition.x, 0.5f);
        interpolatedPosition.y = Interpolation.linear.apply(previousPosition.y, currentPosition.y, 0.5f);
    }

    /**
     * Invoked before updating the player
     */
    public void captureState() {
        previousPosition.set(body.getPosition().x, body.getPosition().y);
    }

    /**
     * Poll the input
     */
    private void pollInput() {
        velocity.set(0f, 0f);

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
            velocity.set(0f, MOVE_SPEED);
            rotation = Rotation.FACING_UP;
        } else if (Gdx.input.isKeyPressed(Input.Keys.S)
                && !isInputDisabled(Input.Keys.S)) {
            velocity.set(0f, -MOVE_SPEED);
            rotation = Rotation.FACING_DOWN;
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
            connection.send(new ClientPosition(rotation.ordinal(), interpolatedPosition.x, interpolatedPosition.y));
            lastPositionSend = now;
        }
    }

    @Override
    public void render(float delta, SpriteBatch batch) {
        renderer.render(delta, interpolatedPosition, batch);
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
