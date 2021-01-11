package protocol.packet.handlers;

import protocol.packet.PacketHandler;
import protocol.packet.client.ClientCreateLobby;
import protocol.packet.client.ClientHandshake;
import protocol.packet.client.ClientJoinLobby;
import protocol.packet.client.ClientVelocity;

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
     * Handle creating a lobby.
     *
     * @param createLobby packet
     */
    void handleCreateLobby(ClientCreateLobby createLobby);

    /**
     * Handle joining a lobby
     *
     * @param joinLobby packet
     */
    void handleJoinLobby(ClientJoinLobby joinLobby);

    /**
     * Handle velocity
     *
     * @param velocity the velocity
     */
    void handleVelocity(ClientVelocity velocity);

    /**
     * Handle when a level is loaded.
     */
    void handleLevelLoaded();

    /**
     * Handle a client disconnect.
     */
    void handleDisconnect();

}
