package protocol.packet.server;

import io.netty.buffer.ByteBuf;
import protocol.packet.Packet;
import protocol.packet.handlers.ServerPacketHandler;

/**
 * A response to {@link protocol.packet.client.ClientHandshake}
 */
public final class ServerHandshakeResponse extends Packet<ServerPacketHandler> {

    /**
     * PID
     */
    public static final int PID = 2;

    /**
     * The entity ID
     */
    private int entityId;

    /**
     * Initialize
     *
     * @param entityId the entity ID
     */
    public ServerHandshakeResponse(int entityId) {
        this.entityId = entityId;
    }

    /**
     * Initialize
     *
     * @param buffer  buffer
     * @param handler handler
     */
    public ServerHandshakeResponse(ByteBuf buffer, ServerPacketHandler handler) {
        super(buffer);
        handler.handleHandshakeResponse(this);
    }

    /**
     * @return the entity ID
     */
    public int entityId() {
        return entityId;
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
    }

    @Override
    public void decode() {
        entityId = readInt();
    }
}
