package protocol.packet.client;

import io.netty.buffer.ByteBuf;
import protocol.packet.Packet;
import protocol.packet.handlers.ClientPacketHandler;

/**
 * Sent by clients to update their position
 */
public final class ClientPosition extends Packet {

    public static final int PID = 14;

    /**
     * Rotation
     */
    private int rotation;

    /**
     * Position
     */
    private float x, y;

    /**
     * Initialize
     *
     * @param rotation rotation
     * @param x        x
     * @param y        y
     */
    public ClientPosition(int rotation, float x, float y) {
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
    public ClientPosition(ByteBuf buffer, ClientPacketHandler handler) {
        super(buffer);
        handler.handlePosition(this);
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
        writeInt(rotation);
        writeFloat(x);
        writeFloat(y);
    }

    @Override
    public void decode() {
        rotation = readInt();
        x = readFloat();
        y = readFloat();
    }
}
