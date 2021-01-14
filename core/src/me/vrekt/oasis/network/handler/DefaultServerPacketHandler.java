package me.vrekt.oasis.network.handler;

import com.badlogic.gdx.Gdx;
import me.vrekt.oasis.Oasis;
import me.vrekt.oasis.entity.player.network.NetworkEntityPlayer;
import me.vrekt.oasis.network.NetworkHandler;
import protocol.packet.handlers.ServerPacketHandler;
import protocol.packet.server.*;

/**
 * Handles packets from the server
 */
public final class DefaultServerPacketHandler implements ServerPacketHandler {

    /**
     * Logging tag
     */
    private static final String TAG = "ServerPacketHandler";

    /**
     * The game.
     */
    private final Oasis game;

    /**
     * The network
     */
    private final NetworkHandler network;

    /**
     * Initialize
     *
     * @param networkHandler the networking handler
     */
    public DefaultServerPacketHandler(NetworkHandler networkHandler) {
        game = Oasis.get();
        this.network = networkHandler;
    }

    @Override
    public void handleHandshakeReply(ServerHandshakeReply reply) {
        if (reply.allowed()) {
            Gdx.app.log(TAG, "Connected to master server!");
        } else {
            game.showDialog("Server error", "Could not join server: \n" + reply.notAllowedReason());
        }
    }

    @Override
    public void handleCreateLobbyReply(ServerCreateLobbyReply reply) {
        if (reply.allowed()) {
            Gdx.app.log(TAG, "Initializing local lobby, entityId=" + reply.entityId() + " lobbyId=" + reply.lobbyId());
            game.thePlayer().entityId(reply.entityId());
            game.startLevel("PreLobby");
        } else {
            Gdx.app.log(TAG, "Failed to create a new lobby, reason=" + reply.notAllowedReason());
            game.showDialog("Failed to create lobby", "Failed to create lobby, reason: \n" + reply.notAllowedReason());
        }
    }

    @Override
    public void handleCreatePlayer(ServerCreatePlayer createPlayer) {
        Gdx.app.log(TAG, "Spawning a networked player, id=" + createPlayer.entityId() + " username=" + createPlayer.username());

        if (game.hasWorld()) {
            final NetworkEntityPlayer player = new NetworkEntityPlayer(createPlayer.username(), createPlayer.entityId());
            game.world().spawnPlayer(player, createPlayer.x(), createPlayer.y());
        }
    }

    @Override
    public void handleRemovePlayer(ServerRemovePlayer removePlayer) {
        Gdx.app.log(TAG, "Removing networked player, id=" + removePlayer.entityId());

        if (game.hasWorld()) game.world().removePlayer(removePlayer.entityId());
    }

    @Override
    public void handleJoinLobbyReply(ServerJoinLobbyReply reply) {
        if (reply.allowed()) {
            Gdx.app.log(TAG, "Allowed to join lobby, entityId=" + reply.entityId());
            game.thePlayer().entityId(reply.entityId());
            game.startLevel("PreLobby");

        } else {
            Gdx.app.log(TAG, "Not allowed to join lobby: " + reply.lobbyId());
            game.showDialog("Failed to join lobby", "Failed to join lobby, reason: \n" + reply.notAllowedReason());
        }
    }

    @Override
    public void handleLoadLevel(ServerLoadLevel loadLevel) {
        if (loadLevel.levelName().equalsIgnoreCase("PreLobby")) {
            // ignore loading this level on demand.
        } else {

        }
    }

    @Override
    public void handlePlayerVelocity(ServerPlayerVelocity velocity) {
        game.world().updateNetworkPlayerVelocity(velocity.entityId(), velocity.velocityX(), velocity.velocityY(), velocity.rotation());
    }

    @Override
    public void handlePlayerPosition(ServerPlayerPosition position) {
        game.world().updateNetworkPlayerPosition(position.entityId(), position.x(), position.y(), position.rotation());
    }

    @Override
    public void handleDisconnect(ServerDisconnect disconnect) {
        Gdx.app.log(TAG, "Disconnected from the server: " + disconnect.reason());

        game.network().close();
        game.showMainMenuAsync();
        game.showDialog("Kicked from server", "You were disconnected from the server for: \n" + disconnect.reason());
    }
}
