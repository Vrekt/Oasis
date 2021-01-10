package protocol.packet.handlers;

import protocol.packet.PacketHandler;
import protocol.packet.client.ClientCreateLobbyRequest;
import protocol.packet.client.ClientHandshake;

/**
 * Handles packets received from the client.
 */
public interface ClientPacketHandler extends PacketHandler {

    /**
     * Handle a handshake packet
     *
     * @param packet the packet
     */
    void handleHandshake(ClientHandshake packet);

    /**
     * Handle a request to create a lobby
     *
     * @param request the request
     */
    void handleCreateLobbyRequest(ClientCreateLobbyRequest request);

    /**
     * Handle a client disconnect.
     */
    void handleDisconnect();

}
