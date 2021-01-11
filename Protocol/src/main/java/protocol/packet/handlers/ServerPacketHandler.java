package protocol.packet.handlers;

import protocol.packet.PacketHandler;
import protocol.packet.server.*;

/**
 * Handles packets received from the server.
 */
public interface ServerPacketHandler extends PacketHandler {

    /**
     * Handle the handshake reply
     *
     * @param reply the reply
     */
    void handleHandshakeReply(ServerHandshakeReply reply);

    /**
     * Handle the create lobby reply
     *
     * @param reply the reply
     */
    void handleCreateLobbyReply(ServerCreateLobbyReply reply);

    /**
     * Handle creating a player
     *
     * @param createPlayer the player
     */
    void handleCreatePlayer(ServerCreatePlayer createPlayer);

    /**
     * Handle removing a player
     *
     * @param removePlayer the player
     */
    void handleRemovePlayer(ServerRemovePlayer removePlayer);

    /**
     * Handle joining a lobby
     *
     * @param reply the reply
     */
    void handleJoinLobbyReply(ServerJoinLobbyReply reply);

    /**
     * Handle loading a level
     *
     * @param loadLevel the level
     */
    void handleLoadLevel(ServerLoadLevel loadLevel);

    /**
     * Handle player velocity
     *
     * @param velocity the velocity
     */
    void handlePlayerVelocity(ServerPlayerVelocity velocity);

    /**
     * Handle disconnect
     *
     * @param disconnect the disconnect
     */
    void handleDisconnect(ServerDisconnect disconnect);

}
