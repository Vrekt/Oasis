package protocol.packet.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import protocol.packet.Packet;
import protocol.packet.handlers.ServerPacketHandler;

/**
 * Sent by the server to update a players position
 */
public final class ServerPlayerPosition extends Packet {

    public static final int PID = 15;

    /**
     * EID
     * Rotation
     */
    private int entityId, rotation;

    /**
     * Position
     */
    private float x, y;

    /**
     * Encode the packet
     *
     * @param entityId EID
     * @param rotation rotation index
     * @param x        X
     * @param y        Y
     * @return the byte buf
     */
    public static ByteBuf encodeDirect(int entityId, int rotation, float x, float y) {
        try {
            final ServerPlayerPosition packet = new ServerPlayerPosition(entityId, rotation, x, y);
            packet.encode();

            final int length = packet.buffer.readableBytes();
            final ByteBuf direct = Unpooled.buffer();

            direct.writeInt(length);
            direct.writeBytes(packet.buffer);
            packet.dispose();
            return direct;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Initialize
     *
     * @param entityId the entity
     * @param rotation rotation
     * @param x        x
     * @param y        y
     */
    public ServerPlayerPosition(int entityId, int rotation, float x, float y) {
        this.entityId = entityId;
        this.rotation = rotation;
        this.x = x;
        this.y = y;
    }

    /**
     * Initialize
     *
     * @param buffer  buffer
     * @param handler handler
     */
    public ServerPlayerPosition(ByteBuf buffer, ServerPacketHandler handler) {
        super(buffer);
        handler.handlePlayerPosition(this);
    }

    /**
     * @return EID
     */
    public int entityId() {
        return entityId;
    }

    /**
     * @return rotation index
     */
    public int rotation() {
        return rotation;
    }

    /**
     * @return x
     */
    public float x() {
        return x;
    }

    /**
     * @return y
     */
    public float y() {
        return y;
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
        writeInt(rotation);
        writeFloat(x);
        writeFloat(y);
    }

    @Override
    public void decode() {
        entityId = readInt();
        rotation = readInt();
        x = readFloat();
        y = readFloat();
    }
}
