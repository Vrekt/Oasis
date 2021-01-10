package protocol.packet.handlers;

import protocol.packet.PacketHandler;
import protocol.packet.server.ServerCreateLobbyResponse;
import protocol.packet.server.ServerDisconnect;
import protocol.packet.server.ServerHandshakeResponse;

/**
 * Handles packets received from the server.
 */
public interface ServerPacketHandler extends PacketHandler {

    /**
     * Handle the handshake response
     *
     * @param response the response
     */
    void handleHandshakeResponse(ServerHandshakeResponse response);

    /**
     * Handle the disconnect
     *
     * @param disconnect the disconnect
     */
    void handleDisconnect(ServerDisconnect disconnect);

    /**
     * Handle the create lobby response
     *
     * @param response the response
     */
    void handleCreateLobbyResponse(ServerCreateLobbyResponse response);

}
