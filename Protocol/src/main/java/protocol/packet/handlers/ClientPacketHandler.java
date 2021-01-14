package protocol.packet.handlers;

import protocol.packet.PacketHandler;
import protocol.packet.client.*;

/**
 * Handles packets received from the client.
 */
public interface ClientPacketHandler extends PacketHandler {

    /**
     * Handle a handshake packet
     *
     * @param handshake the handshake
     */
    void handleHandshake(ClientHandshake handshake);

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
     * Handle position packets
     *
     * @param position position
     */
    void handlePosition(ClientPosition position);

    /**
     * Handle a client disconnect.
     */
    void handleDisconnect();

}
