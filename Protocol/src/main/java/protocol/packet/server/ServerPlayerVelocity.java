package protocol.packet.server;

import io.netty.buffer.ByteBuf;
import protocol.packet.Packet;
import protocol.packet.handlers.ServerPacketHandler;

/**
 * Sent by the server to update a client velocity
 */
public final class ServerPlayerVelocity extends Packet {

    public static final int PID = 13;

    /**
     * The entity ID
     */
    private int entityId;

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
     * @param entityId      the entity ID
     * @param velocityX     velocity X
     * @param velocityY     velocity Y
     * @param rotationIndex rotation index
     */
    public ServerPlayerVelocity(int entityId, float velocityX, float velocityY, int rotationIndex) {
        this.entityId = entityId;
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
    public ServerPlayerVelocity(ByteBuf buffer, ServerPacketHandler handler) {
        super(buffer);
        handler.handlePlayerVelocity(this);
    }

    /**
     * @return the entity ID
     */
    public int entityId() {
        return entityId;
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
        writeInt(entityId);
        writeFloat(velocityX);
        writeFloat(velocityY);
        writeInt(rotationIndex);
    }

    @Override
    public void decode() {
        entityId = readInt();
        velocityX = readFloat();
        velocityY = readFloat();
        rotationIndex = readInt();
    }

}
