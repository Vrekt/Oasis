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
     * Rotation index
     */
    private int rotationIndex;

    /**
     * Initialize
     *
     * @param velocityX     velocity X
     * @param velocityY     velocity Y
     * @param rotationIndex rotation index
     */
    public ClientVelocity(float velocityX, float velocityY, int rotationIndex) {
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.rotationIndex = rotationIndex;
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
    public int rotationIndex() {
        return rotationIndex;
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
        writeInt(rotationIndex);
    }

    @Override
    public void decode() {
        velocityX = readFloat();
        velocityY = readFloat();
        rotationIndex = readInt();
    }
}
