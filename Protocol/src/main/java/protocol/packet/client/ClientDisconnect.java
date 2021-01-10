package protocol.packet.client;

import io.netty.buffer.ByteBuf;
import protocol.packet.Packet;
import protocol.packet.handlers.ClientPacketHandler;

/**
 * Sent by clients to indicate a disconnect
 */
public final class ClientDisconnect extends Packet<ClientPacketHandler> {

    /**
     * PID
     */
    public static final int PID = 4;

    /**
     * Initialize
     *
     * @param byteBuf buffer
     * @param handler handler
     */
    public ClientDisconnect(ByteBuf byteBuf, ClientPacketHandler handler) {
        handler.handleDisconnect();
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
