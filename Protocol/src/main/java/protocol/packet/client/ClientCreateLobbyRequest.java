package protocol.packet.client;

import io.netty.buffer.ByteBuf;
import protocol.packet.Packet;
import protocol.packet.handlers.ClientPacketHandler;

/**
 * Sent by clients to create a lobby.
 */
public final class ClientCreateLobbyRequest extends Packet<ClientPacketHandler> {

    /**
     * PID
     */
    public static final int PID = 5;

    public ClientCreateLobbyRequest() {
    }

    /**
     * Initialize
     *
     * @param byteBuf buffer
     * @param handler handler
     */
    public ClientCreateLobbyRequest(ByteBuf byteBuf, ClientPacketHandler handler) {
        handler.handleCreateLobbyRequest(this);
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
