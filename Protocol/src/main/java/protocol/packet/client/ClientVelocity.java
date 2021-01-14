package protocol.packet.client;

import io.netty.buffer.ByteBuf;
import protocol.packet.Packet;
import protocol.packet.handlers.ClientPacketHandler;

/**
 * Sent by clients to update their velocity
 */
public final class ClientVelocity extends Packet {

    public static final int PID = 12;

    /**
     * Velocity
     */
    private float velocityX, velocityY;

    /**
     * Rotation
     */
    private int rotation;

    /**
     * Initialize
     *
     * @param velocityX velocity X
     * @param velocityY velocity Y
     * @param rotation  rotation
     */
    public ClientVelocity(float velocityX, float velocityY, int rotation) {
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.rotation = rotation;
    }

    /**
     * Initialize
     *
     * @param buffer  buffer
     * @param handler handler
     */
    public ClientVelocity(ByteBuf buffer, ClientPacketHandler handler) {
        super(buffer);
        handler.handleVelocity(this);
    }

    /**
     * @return x vel
     */
    public float velocityX() {
        return velocityX;
    }

    /**
     * @return y vel
     */
    public float velocityY() {
        return velocityY;
    }

    /**
     * @return the rotation value
     */
    public int rotation() {
        return rotation;
    }

    @Override
    public int pid() {
        return PID;
    }

    @Override
    public void encode() {
        createBuffer();
        writePid();
        writeFloat(velocityX);
        writeFloat(velocityY);
        writeInt(rotation);
    }

    @Override
    public void decode() {
        velocityX = readFloat();
        velocityY = readFloat();
        rotation = readInt();
    }
}
