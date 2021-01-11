package protocol.packet.server;

import io.netty.buffer.ByteBuf;
import protocol.packet.Packet;
import protocol.packet.handlers.ServerPacketHandler;

/**
 * Sent by the server to remove a player client side
 */
public final class ServerRemovePlayer extends Packet {

    public static final int PID = 8;

    /**
     * entity ID
     */
    private int entityId;

    /**
     * Initialize
     *
     * @param entityId ID to remove
     */
    public ServerRemovePlayer(int entityId) {
        this.entityId = entityId;
    }

    /**
     * Initialize
     *
     * @param buffer  buffer
     * @param handler handler
     */
    public ServerRemovePlayer(ByteBuf buffer, ServerPacketHandler handler) {
        super(buffer);
        handler.handleRemovePlayer(this);
    }

    /**
     * @return the ID
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
