package protocol.packet.client;

import io.netty.buffer.ByteBuf;
import protocol.packet.Packet;
import protocol.packet.handlers.ClientPacketHandler;

/**
 * Sent by clients to indicate a level is loaded
 */
public final class ClientLevelLoaded extends Packet {

    public static final int PID = 10;

    /**
     * Empty
     */
    public ClientLevelLoaded() {
    }

    public ClientLevelLoaded(ByteBuf buffer, ClientPacketHandler handler) {
        super(buffer);
        handler.handleLevelLoaded();
    }

    @Override
    public int pid() {
        return PID;
    }

    @Override
    public void encode() {
        createBuffer();
        writePid();
    }
}
